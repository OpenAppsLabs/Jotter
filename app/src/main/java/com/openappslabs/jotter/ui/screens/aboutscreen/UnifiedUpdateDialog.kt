package com.openappslabs.jotter.ui.screens.aboutscreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.openappslabs.jotter.data.model.GithubRelease
import com.openappslabs.jotter.ui.components.LoadingIndicator
import com.openappslabs.jotter.ui.components.ProgressIndicator

@Composable
fun UnifiedUpdateDialog(
    updateStatus: UpdateStatus,
    onDownloadClick: (String) -> Unit,
    onInstallClick: () -> Unit,
    onGithubClick: () -> Unit,
    onCancelDownload: () -> Unit,
    onDismiss: () -> Unit
) {
    val isDownloading = updateStatus is UpdateStatus.Downloading && updateStatus.progress < 100
    val isChecking = updateStatus is UpdateStatus.Checking
    val canDismiss = !isChecking && !isDownloading

    Dialog(
        onDismissRequest = { if (canDismiss) onDismiss() },
        properties = DialogProperties(dismissOnBackPress = canDismiss, dismissOnClickOutside = canDismiss)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .animateContentSize(animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            // Content area with animation and fixed height
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp) // Fixed height set here
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = updateStatus,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                    },
                    contentKey = { status -> status::class }, // Key by class type to avoid blinking on progress update
                    label = "ContentAnimation"
                ) { status ->
                    when (status) {
                        is UpdateStatus.Checking -> CheckingState()
                        is UpdateStatus.Available -> AvailableState(status.release, onDownloadClick, onGithubClick)
                        is UpdateStatus.Downloading -> DownloadingState(status.progress, onInstallClick, onCancelDownload, onHide = onDismiss)
                        is UpdateStatus.NoUpdate -> InfoState(
                            icon = Icons.Rounded.CheckCircle,
                            title = "You're up to date!",
                            message = "Jotter is running the latest version.",
                            onButtonClick = onDismiss
                        )
                        is UpdateStatus.Error -> InfoState(
                            icon = Icons.Rounded.Warning,
                            title = "Update Failed",
                            message = "Could not check for updates. Please check your connection.",
                            isError = true,
                            onButtonClick = onDismiss
                        )
                        else -> Spacer(Modifier.height(200.dp))
                    }
                }
            }
        }
    }
}

// --- STATE COMPONENTS ---

@Composable
private fun StateColumn(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        content()
    }
}

@Composable
private fun CheckingState() {
    StateColumn {
        Spacer(Modifier.height(32.dp))
        LoadingIndicator(modifier = Modifier.size(48.dp))
        Text("Checking for update", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun AvailableState(
    release: GithubRelease,
    onDownload: (String) -> Unit,
    onGithub: () -> Unit
) {
    StateColumn {
        // Changed to Row for horizontal alignment
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Update Available",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                fontWeight = FontWeight.Bold
            )
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = release.tagName,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        ActionButton(
            text = "Download & Install",
            icon = Icons.Rounded.CloudDownload,
            onClick = {
                val url = release.assets.find { it.name.endsWith(".apk") }?.downloadUrl
                if (url != null) onDownload(url)
            }
        )
        SecondaryButton(
            text = "View on GitHub",
            icon = Icons.AutoMirrored.Rounded.OpenInNew,
            onClick = onGithub
        )
    }
}

@Composable
private fun DownloadingState(
    progress: Int,
    onInstall: () -> Unit,
    onCancel: () -> Unit,
    onHide: () -> Unit
) {
    val isComplete = progress >= 100

    StateColumn {
        if (isComplete) {
            // Compact Completion State
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Icon(
                    Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Download Complete",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            ActionButton(text = "Update Now", icon = Icons.Rounded.SystemUpdate, onClick = onInstall)
            SecondaryButton(text = "Cancel", onClick = onCancel)
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Downloading...", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                Text("$progress%", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            ProgressIndicator(progress = progress / 100f, modifier = Modifier.fillMaxWidth().height(10.dp))
            FilledTonalButton(
                onClick = onHide,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Hide")
            }
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Cancel Download")
            }
        }
    }
}

@Composable
private fun InfoState(
    icon: ImageVector,
    title: String,
    message: String,
    isError: Boolean = false,
    onButtonClick: () -> Unit
) {
    StateColumn {
        Header(icon = icon, title = title, isError = isError)
        Text(message, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        ActionButton(text = "Close", onClick = onButtonClick)
    }
}

// --- COMMON BUILDING BLOCKS ---

@Composable
private fun Header(icon: ImageVector, title: String, isError: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Icon(
            icon,
            null,
            modifier = Modifier.size(48.dp),
            tint = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        )
        Text(
            title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ActionButton(text: String, icon: ImageVector? = null, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        if (icon != null) {
            Icon(icon, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text)
    }
}

@Composable
private fun SecondaryButton(text: String, icon: ImageVector? = null, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        if (icon != null) {
            Icon(icon, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text)
    }
}
