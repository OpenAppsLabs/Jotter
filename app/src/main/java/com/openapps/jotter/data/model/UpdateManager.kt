package com.openapps.jotter.data.model

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL
import javax.inject.Inject
import androidx.core.net.toUri

class UpdateManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    // 1. Fetch Release Info
    suspend fun getLatestRelease(): GithubRelease? = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://api.github.com/repos/openappslabs/Jotter/releases/latest")
            val json = url.readText()
            return@withContext Gson().fromJson(json, GithubRelease::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }

    // 2. Download APK
    fun downloadApk(downloadUrl: String): Long {
        // Delete any old update file first to avoid conflicts
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "JotterUpdate.apk")
        if (file.exists()) {
            file.delete()
        }

        val request = DownloadManager.Request(downloadUrl.toUri())
            .setTitle("Jotter Update")
            .setDescription("Downloading latest version...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, "JotterUpdate.apk")
            .setMimeType("application/vnd.android.package-archive")

        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        return manager.enqueue(request)
    }

    // Cancel Download
    fun cancelDownload(downloadId: Long) {
        try {
            val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            manager.remove(downloadId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 3. Track Download Progress
    suspend fun trackDownloadProgress(downloadId: Long): Flow<Int> = flow {
        var progress = 0
        var isDownloading = true
        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        while (isDownloading) {
            val query = DownloadManager.Query().setFilterById(downloadId)
            val cursor = manager.query(query)

            if (cursor != null && cursor.moveToFirst()) {
                val bytesDownloadedIndex = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                val bytesTotalIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)

                if (bytesDownloadedIndex != -1 && bytesTotalIndex != -1) {
                    val bytesDownloaded = cursor.getLong(bytesDownloadedIndex)
                    val bytesTotal = cursor.getLong(bytesTotalIndex)

                    if (bytesTotal > 0) {
                        progress = ((bytesDownloaded * 100) / bytesTotal).toInt()
                        emit(progress)
                    }
                }

                val status = cursor.getInt(statusIndex)
                if (status == DownloadManager.STATUS_SUCCESSFUL || status == DownloadManager.STATUS_FAILED) {
                    isDownloading = false
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        emit(100)
                    }
                }
            } else {
                // Cursor empty means download might have been cancelled/removed
                isDownloading = false
            }
            cursor?.close()
            delay(100)
        }
    }

    // 4. Trigger Installation
    fun installApk() {
        val file = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
            "JotterUpdate.apk"
        )

        if (file.exists()) {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(intent)
        }
    }
}
