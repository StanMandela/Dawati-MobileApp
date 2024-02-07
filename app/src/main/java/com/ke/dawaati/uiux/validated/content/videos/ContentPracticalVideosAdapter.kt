package com.ke.dawaati.uiux.validated.content.videos

import android.os.Build
import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.ke.dawaati.R
import com.ke.dawaati.databinding.ItemVideoSubTopicsBinding
import com.ke.dawaati.db.model.PracticalsEntity
import com.ke.dawaati.util.EncryptUtil
import com.ke.dawaati.util.toTitleCase
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import java.io.File
import java.io.FileWriter
import java.io.IOException

class ContentPracticalVideosAdapter(
    private val selectedVideo: (PracticalsEntity) -> Unit,
    private val selectedPremiumVideo: (String) -> Unit
) : RecyclerView.Adapter<ContentPracticalVideosAdapter.ViewHolder>(), Filterable {

    private val items = mutableListOf<PracticalsEntity>()

    private val originalModel = mutableListOf<PracticalsEntity>()

    private val encrypt = EncryptUtil("Kelvinmkioko@gmail.com")

    private var subscriptionStatus: Boolean = true
    private var subject = ""
    private var level = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            ItemVideoSubTopicsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: ContentPracticalVideosAdapter.ViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    inner class ViewHolder(
        private val binding: ItemVideoSubTopicsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(content: PracticalsEntity, position: Int) {
            binding.apply {
                val context = subTopicName.context

                subTopicName.text = toTitleCase(
                    content.file_name,
                    handleWhiteSpace = false
                )
                subTopicViews.text = content.availability

                val imageResource = if (content.availability.equals("free", ignoreCase = true)) {
                    R.drawable.ic_play
                } else {
                    R.drawable.ic_content_premium
                }
                subTopicDownload.isVisible = content.availability.equals(
                    "free",
                    ignoreCase = true
                )

                videoPlay.setImageResource(imageResource)

                root.setOnClickListener {
                    when {
                        content.availability.equals("free", ignoreCase = true) || content.availability.equals("premium", ignoreCase = true) && subscriptionStatus ->
                            selectedVideo(content)
                        else -> selectedPremiumVideo("The content selected is premium.")
                    }
                }

                val encrypted = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                    File(
                        Environment.getExternalStorageDirectory().toString() +
                            File.separator + "Dawati" +
                            File.separator + subject +
                            File.separator + level +
                            File.separator + "videos" +
                            File.separator + content.file_name + "-enc"
                    )
                } else {
                    File(
                        context.externalCacheDir.toString(),
                        File.separator + "Dawati" +
                            File.separator + subject +
                            File.separator + level +
                            File.separator + "videos" +
                            File.separator + content.file_name + "-enc"
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
                        .create(content.file_url + content.file_name.replace(" ", "%20"))
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
                                            File.separator + content.file_name + "-enc"
                                    )
                                } else {
                                    File(
                                        context.externalCacheDir.toString(),
                                        File.separator + "Dawati" +
                                            File.separator + subject +
                                            File.separator + level +
                                            File.separator + "videos" +
                                            File.separator + content.file_name + "-enc"
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
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setPracticals(
        practicalsEntity: List<PracticalsEntity>,
        subject: String,
        level: String,
        subscriptionStatus: Boolean
    ) {
        items.apply {
            clear()
            addAll(practicalsEntity)
        }
        originalModel.apply {
            clear()
            addAll(practicalsEntity)
        }
        this.subscriptionStatus = subscriptionStatus
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
                            it.file_name.contains(constraint, ignoreCase = true)
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
