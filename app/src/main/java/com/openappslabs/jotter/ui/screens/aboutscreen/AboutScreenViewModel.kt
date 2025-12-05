package com.openappslabs.jotter.ui.screens.aboutscreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openappslabs.jotter.BuildConfig
import com.openappslabs.jotter.data.model.GithubRelease
import com.openappslabs.jotter.data.model.UpdateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AboutScreenViewModel @Inject constructor(
    private val updateManager: UpdateManager
) : ViewModel() {

    // State to control the UI
    var updateStatus by mutableStateOf<UpdateStatus>(UpdateStatus.Idle)
    
    // Keep track of the download job to cancel it
    private var downloadJob: Job? = null
    private var currentDownloadId: Long? = null

    fun checkForUpdate() {
        viewModelScope.launch {
            updateStatus = UpdateStatus.Checking

            // Record start time
            val startTime = System.currentTimeMillis()

            // Perform the actual check
            val release = updateManager.getLatestRelease()

            // Calculate how much time has passed
            val elapsedTime = System.currentTimeMillis() - startTime

            // If less than 2000ms passed, wait the remaining time
            if (elapsedTime < 2000) {
                delay(2000 - elapsedTime)
            }

            // Now update the UI with the result
            if (release != null) {
                val latestVersion = release.tagName.removePrefix("v")
                val currentVersion = BuildConfig.VERSION_NAME

                if (latestVersion != currentVersion) {
                    updateStatus = UpdateStatus.Available(release)
                } else {
                    updateStatus = UpdateStatus.NoUpdate
                }
            } else {
                updateStatus = UpdateStatus.Error
            }
        }
    }

    fun downloadUpdate(url: String) {
        // Cancel any existing download first
        cancelDownload()

        downloadJob = viewModelScope.launch {
            // 1. Set initial state
            updateStatus = UpdateStatus.Downloading(0)

            // 2. Start download and get the ID
            val downloadId = updateManager.downloadApk(url)
            currentDownloadId = downloadId

            // 3. Track progress
            updateManager.trackDownloadProgress(downloadId).collect { progress ->
                updateStatus = UpdateStatus.Downloading(progress)
            }
        }
    }

    fun installUpdate() {
        updateManager.installApk()
    }

    // Reset state (e.g. after closing a dialog)
    fun resetState() {
        // If we are resetting while downloading, we should probably cancel it?
        // But 'resetState' is usually called when closing the dialog.
        // If it's a background download, we might want to keep it.
        // However, the UI implies "Dismiss" or "Cancel".
        // For now, let's ensure we clear the state.
        updateStatus = UpdateStatus.Idle
    }

    fun cancelDownload() {
        // Cancel the coroutine collecting progress
        downloadJob?.cancel()
        downloadJob = null
        
        // Cancel the actual download in DownloadManager
        currentDownloadId?.let { id ->
            updateManager.cancelDownload(id)
        }
        currentDownloadId = null

        updateStatus = UpdateStatus.Idle
    }
}

// The states the UI needs to handle
sealed class UpdateStatus {
    object Idle : UpdateStatus()
    object Checking : UpdateStatus()
    object NoUpdate : UpdateStatus()
    object Error : UpdateStatus()

    // MUST be a data class with 'val progress: Int'
    data class Downloading(val progress: Int) : UpdateStatus()

    data class Available(val release: GithubRelease) : UpdateStatus()
}
