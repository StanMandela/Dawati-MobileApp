package com.ke.dawaati.uiux.validated.content.videos

import android.content.Context
import android.os.Build
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.ke.dawaati.R
import com.ke.dawaati.databinding.ItemVideoTopicsBinding
import com.ke.dawaati.util.EncryptUtil
import com.ke.dawaati.util.toTitleCase
import com.ke.dawaati.widgets.RoundCornerProgressBar
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import java.io.File
import java.io.FileWriter
import java.io.IOException

class ContentVideosAdapter(
    private val selectedVideo: (DisplayVideoSubTopics) -> Unit,
    private val selectedPremiumVideo: (String) -> Unit
) :
    RecyclerView.Adapter<ContentVideosAdapter.ViewHolder>(), Filterable {

    private val items = mutableListOf<DisplayVideoTopics>()

    private val originalModel = mutableListOf<DisplayVideoTopics>()

    private val encrypt = EncryptUtil("Kelvinmkioko@gmail.com")

    private var openTopic = ""
    private var subject = ""
    private var level = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentVideosAdapter.ViewHolder =
        ViewHolder(
            ItemVideoTopicsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: ContentVideosAdapter.ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class ViewHolder(private val binding: ItemVideoTopicsBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        init {
            binding.videoClick.setOnClickListener {
                openTopic = if (openTopic.isNotEmpty() && openTopic.equals(items[adapterPosition].topicID)) {
                    ""
                } else {
                    items[adapterPosition].topicID
                }
                notifyDataSetChanged()
            }
        }

        fun bind(content: DisplayVideoTopics) {
            binding.apply {
                val context = videoCount.context
                topicName.text = content.name
                videoCount.text = "${content.subTopicVideos.size} video${if (content.subTopicVideos.size == 1) {""} else {"s"}}"

                videoSubTopics.removeAllViews()
                content.subTopicVideos.forEach { displayVideoSubTopics ->
                    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    val rowView: View = inflater.inflate(R.layout.item_video_sub_topics, null)
                    val videoPlay = rowView.findViewById<ImageView>(R.id.videoPlay)
                    val subTopicName = rowView.findViewById<TextView>(R.id.subTopicName)
                    val subTopicViews = rowView.findViewById<TextView>(R.id.subTopicViews)
                    val loading = rowView.findViewById<LottieAnimationView>(R.id.loading)
                    val subTopicDownload = rowView.findViewById<ImageView>(R.id.subTopicDownload)
                    val downloadProgress = rowView.findViewById<RoundCornerProgressBar>(R.id.downloadProgress)

                    subTopicName.text = toTitleCase(
                        displayVideoSubTopics.file_name,
                        handleWhiteSpace = false
                    )
                    subTopicViews.text = "${displayVideoSubTopics.views} views - ${displayVideoSubTopics.availability}"

                    val imageResource = if (displayVideoSubTopics.availability.equals(
                            "free",
                            ignoreCase = true
                        )
                    ) {
                        R.drawable.ic_play
                    } else {
                        R.drawable.ic_content_premium
                    }
                    subTopicDownload.isVisible = displayVideoSubTopics.availability.equals(
                        "free",
                        ignoreCase = true
                    )

                    videoPlay.setImageResource(imageResource)

                    rowView.setOnClickListener {
                        when {
                            displayVideoSubTopics.availability.equals("free", ignoreCase = true) || displayVideoSubTopics.availability.equals("premium", ignoreCase = true) && displayVideoSubTopics.subscriptionStatus -> {
                                selectedVideo(displayVideoSubTopics)
                            }
                            else -> {
                                selectedPremiumVideo("The content selected is premium.")
                            }
                        }
                    }

                    val encrypted = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                        File(
                            Environment.getExternalStorageDirectory().toString() +
                                File.separator + "Dawati" +
                                File.separator + subject +
                                File.separator + level +
                                File.separator + "videos" +
                                File.separator + displayVideoSubTopics.file_name + "-enc"
                        )
                    } else {
                        File(
                            context.externalCacheDir.toString(),
                            File.separator + "Dawati" +
                                File.separator + subject +
                                File.separator + level +
                                File.separator + "videos" +
                                File.separator + displayVideoSubTopics.file_name + "-enc"
                        )
                    }

                    subTopicDownload.isGone = encrypted.exists()

                    subTopicDownload.setOnClickListener {
                        val outputFile = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                            File(
                                Environment.getExternalStorageDirectory().toString() +
                                    File.separator + "Dawati" +
                                    File.separator + subject +
                                    File.separator + level +
                                    File.separator + "videos"
                            )
                        } else {
                            File(
                                context.externalCacheDir.toString(),
                                File.separator + "Dawati" +
                                    File.separator + subject +
                                    File.separator + level +
                                    File.separator + "videos"
                            )
                        }
                        if (!outputFile.exists()) {
                            outputFile.mkdirs()
                            try {
                                val f = FileWriter(outputFile)
                                f.close()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }

                        FileDownloader.setup(context)
                        FileDownloader.getImpl()
                            .create(displayVideoSubTopics.file_url + displayVideoSubTopics.file_name.replace(" ", "%20"))
                            .setCallbackProgressTimes(0)
                            .setAutoRetryTimes(3)
                            .setPath(outputFile.toString(), true)
                            .setCallbackProgressTimes(300)
                            .setMinIntervalUpdateSpeed(400)
                            .setListener(object : FileDownloadListener() {
                                override fun pending(
                                    task: BaseDownloadTask,
                                    soFarBytes: Int,
                                    totalBytes: Int
                                ) {
                                    downloadProgress.apply {
                                        isVisible = true
                                        max = totalBytes.toFloat()
                                        progress = soFarBytes.toFloat()
                                    }
                                    loading.isVisible = true
                                    subTopicDownload.isGone = true
                                }

                                override fun connected(
                                    task: BaseDownloadTask,
                                    etag: String,
                                    isContinue: Boolean,
                                    soFarBytes: Int,
                                    totalBytes: Int
                                ) {
                                    downloadProgress.apply {
                                        isVisible = true
                                        max = totalBytes.toFloat()
                                        progress = soFarBytes.toFloat()
                                    }
                                    loading.isVisible = true
                                    subTopicDownload.isGone = true
                                }

                                override fun progress(
                                    task: BaseDownloadTask,
                                    soFarBytes: Int,
                                    totalBytes: Int
                                ) {
                                    downloadProgress.apply {
                                        isVisible = true
                                        max = totalBytes.toFloat()
                                        progress = soFarBytes.toFloat()
                                    }
                                    loading.isVisible = true
                                    subTopicDownload.isGone = true
                                }

                                override fun blockComplete(task: BaseDownloadTask) {}
                                override fun retry(
                                    task: BaseDownloadTask,
                                    ex: Throwable,
                                    retryingTimes: Int,
                                    soFarBytes: Int
                                ) {
                                }

                                override fun completed(task: BaseDownloadTask) {
                                    loading.isGone = true
                                    subTopicDownload.isGone = true
                                    downloadProgress.isGone = true
                                    val raw = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                                        File(
                                            Environment.getExternalStorageDirectory().toString() +
                                                File.separator + "Dawati" +
                                                File.separator + subject +
                                                File.separator + level +
                                                File.separator + "videos" +
                                                File.separator + task.filename
                                        )
                                    } else {
                                        File(
                                            context.externalCacheDir.toString(),
                                            File.separator + "Dawati" +
                                                File.separator + subject +
                                                File.separator + level +
                                                File.separator + "videos" +
                                                File.separator + task.filename
                                        )
                                    }

                                    val encrypted = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                                        File(
                                            Environment.getExternalStorageDirectory().toString() +
                                                File.separator + "Dawati" +
                                                File.separator + subject +
                                                File.separator + level +
                                                File.separator + "videos" +
                                                File.separator + displayVideoSubTopics.file_name + "-enc"
                                        )
                                    } else {
                                        File(
                                            context.externalCacheDir.toString(),
                                            File.separator + "Dawati" +
                                                File.separator + subject +
                                                File.separator + level +
                                                File.separator + "videos" +
                                                File.separator + displayVideoSubTopics.file_name + "-enc"
                                        )
                                    }

                                    encrypt.encryptFile(raw, encrypted, "Kelvinmkioko@gmail.com")
                                    raw.delete()
                                }

                                override fun paused(
                                    task: BaseDownloadTask,
                                    soFarBytes: Int,
                                    totalBytes: Int
                                ) {}

                                override fun error(task: BaseDownloadTask, e: Throwable) {
                                    downloadProgress.isGone = true
                                    loading.isGone = true
                                    subTopicDownload.isVisible = true
                                }

                                override fun warn(task: BaseDownloadTask) {}
                            }).start()
                    }

                    videoSubTopics.addView(rowView, videoSubTopics.childCount)
                }

                videoSubTopics.isVisible = openTopic.equals(content.topicID, ignoreCase = true)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setVideos(videosEntity: List<DisplayVideoTopics>, subject: String, level: String) {
        items.apply {
            clear()
            addAll(videosEntity)
        }
        originalModel.apply {
            clear()
            addAll(videosEntity)
        }
        this.subject = subject
        this.level = level
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                items.clear()
                if (!constraint.isNullOrBlank() && constraint.length > 1) {
                    items.addAll(
                        originalModel.filter {
                            it.name.contains(constraint, ignoreCase = true)
                        }
                    )
                } else {
                    items.addAll(originalModel)
                }
                return FilterResults().apply {
                    values = items
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                notifyDataSetChanged()
            }
        }
    }
}
