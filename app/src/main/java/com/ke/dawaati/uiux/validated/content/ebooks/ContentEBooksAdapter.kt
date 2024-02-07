package com.ke.dawaati.uiux.validated.content.ebooks

import android.os.Build
import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.ke.dawaati.databinding.ItemEbookBinding
import com.ke.dawaati.util.EncryptUtil
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import java.io.File
import java.io.FileWriter
import java.io.IOException

class ContentEBooksAdapter(
    private val selectedEBook: (DisplayEBooks) -> Unit
) : RecyclerView.Adapter<ContentEBooksAdapter.ViewHolder>(), Filterable {

    private val items = mutableListOf<DisplayEBooks>()

    private val originalModel = mutableListOf<DisplayEBooks>()

    private val encrypt = EncryptUtil("Kelvinmkioko@gmail.com")

    private var subject = ""
    private var level = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentEBooksAdapter.ViewHolder =
        ViewHolder(
            ItemEbookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: ContentEBooksAdapter.ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class ViewHolder(private val binding: ItemEbookBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        init {
            itemView.setOnClickListener {
                selectedEBook(items[position])
            }
        }

        fun bind(content: DisplayEBooks) {
            binding.apply {
                val context = eBookName.context
                eBookName.text = content.file_name

                val encrypted = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                    File(
                        Environment.getExternalStorageDirectory().toString() +
                            File.separator + "Dawati" +
                            File.separator + subject +
                            File.separator + level +
                            File.separator + "eBooks" +
                            File.separator + content.file_name + "-enc"
                    )
                } else {
                    File(
                        context.externalCacheDir.toString(),
                        File.separator + "Dawati" +
                            File.separator + subject +
                            File.separator + level +
                            File.separator + "eBooks" +
                            File.separator + content.file_name + "-enc"
                    )
                }
                eBookDownload.isGone = encrypted.exists()

                eBookDownload.setOnClickListener {
                    val outputFile = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                        File(
                            Environment.getExternalStorageDirectory().toString() +
                                File.separator + "Dawati" +
                                File.separator + subject +
                                File.separator + level +
                                File.separator + "eBooks"
                        )
                    } else {
                        File(
                            context.externalCacheDir.toString(),
                            File.separator + "Dawati" +
                                File.separator + subject +
                                File.separator + level +
                                File.separator + "eBooks"
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
                        .create((content.file_url + content.file_name + ".pdf").replace(" ", "%20"))
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
                                eBookDownload.isGone = true
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
                                eBookDownload.isGone = true
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
                                eBookDownload.isGone = true
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
                                eBookDownload.isGone = true
                                downloadProgress.isGone = true
                                val raw = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                                    File(
                                        Environment.getExternalStorageDirectory().toString() +
                                            File.separator + "Dawati" +
                                            File.separator + subject +
                                            File.separator + level +
                                            File.separator + "eBooks" +
                                            File.separator + task.filename
                                    )
                                } else {
                                    File(
                                        context.externalCacheDir.toString(),
                                        File.separator + "Dawati" +
                                            File.separator + subject +
                                            File.separator + level +
                                            File.separator + "eBooks" +
                                            File.separator + task.filename
                                    )
                                }

                                val encrypted = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                                    File(
                                        Environment.getExternalStorageDirectory().toString() +
                                            File.separator + "Dawati" +
                                            File.separator + subject +
                                            File.separator + level +
                                            File.separator + "eBooks" +
                                            File.separator + content.file_name + "-enc"
                                    )
                                } else {
                                    File(
                                        context.externalCacheDir.toString(),
                                        File.separator + "Dawati" +
                                            File.separator + subject +
                                            File.separator + level +
                                            File.separator + "eBooks" +
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
                            ) {
                            }

                            override fun error(task: BaseDownloadTask, e: Throwable) {
                                downloadProgress.isGone = true
                                loading.isGone = true
                                eBookDownload.isVisible = true
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

    fun setEBooks(eBookEntity: List<DisplayEBooks>, subject: String, level: String) {
        items.apply {
            clear()
            addAll(eBookEntity)
        }
        originalModel.apply {
            clear()
            addAll(eBookEntity)
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
