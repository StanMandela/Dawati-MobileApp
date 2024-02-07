package com.ke.dawaati.uiux

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.ke.dawaati.R
import com.ke.dawaati.databinding.ActivityMainBinding
import com.ke.dawaati.preference.ConfigurationPrefs
import com.ke.dawaati.util.GlobalNotificationBuilder
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var notificationManager: NotificationManager? = null

    private var notificationManagerCompat: NotificationManagerCompat? = null

    private val channelId = "dawati.notifications"

    private val configurationPrefs: ConfigurationPrefs by inject()
    private val viewModel: MainViewModel by viewModel()

    private val messageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            // Notification Channel Id is ignored for Android pre O (26).
            val notificationCompatBuilder: NotificationCompat.Builder = NotificationCompat.Builder(
                applicationContext, channelId
            )

            GlobalNotificationBuilder.notificationCompatBuilderInstance = notificationCompatBuilder

            val notification: Notification =
                notificationCompatBuilder // BIG_TEXT_STYLE sets title and content for API 16 (4.1 and after).
                    .setContentTitle(intent.extras?.getString("title"))
                    .setContentText(intent.extras?.getString("body"))
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setDefaults(NotificationCompat.DEFAULT_ALL) // Set primary color (important for Wear 2.0 Notifications).
                    .setColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.colorPrimary
                        )
                    )
                    .setCategory(Notification.CATEGORY_REMINDER)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .build()

            notificationManagerCompat?.notify(102, notification)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateStartDestination()
        doRegisterUser()

        notificationManagerCompat = NotificationManagerCompat.from(applicationContext)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        viewModel.createSessionAnalytics(context = this)
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.apply {
            itemIconTintList = null
            setupWithNavController(findNavController(R.id.navHostFragment))

            findNavController(R.id.navHostFragment).apply {
                addOnDestinationChangedListener { _, _, args ->
                    isVisible = args?.getBoolean("hasBottomNav", false) ?: false
                }
                setOnItemReselectedListener {}
            }
        }
    }

    private fun updateStartDestination() {
        var isLoggedIn: Boolean

        runBlocking {
            isLoggedIn = configurationPrefs.getValidated()
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment

        navHostFragment.navController.apply {
            if (isLoggedIn) {
                val navGraph = navInflater.inflate(R.navigation.app_nav_graph)
                navGraph.setStartDestination(R.id.homeFragment)
                graph = navGraph
            }
        }
    }

    private fun doRegisterUser() {
        // get user notification token provided by firebase
        FirebaseMessaging.getInstance().token.addOnCompleteListener(
            OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Toast.makeText(this, "token_failed Fetching FCM registration token failed $task.exception", Toast.LENGTH_LONG).show()
                    return@OnCompleteListener
                }
                configurationPrefs.setToken(token = task.result)
            }
        )
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, IntentFilter("DawatiNotification"))
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver)
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadAnalytics()
    }

    override fun onPause() {
        super.onPause()
        viewModel.closeSessionAnalytics()
    }

    override fun onDestroy() {
        viewModel.closeSessionAnalytics()
        super.onDestroy()
    }
}
