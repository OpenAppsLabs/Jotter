package com.openappslabs.jotter.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun NoteActionDialog(
    onDismiss: () -> Unit,
    onArchiveConfirm: () -> Unit,
    onDeleteConfirm: () -> Unit // Note: Delete means move to trash in active screen context
) {
    val outerRadius = 25.dp
    val zeroPadding = PaddingValues(0.dp)

    // Helper shapes for the button stack
    val topButtonShape = RoundedCornerShape(
        topStart    = outerRadius,
        topEnd      = outerRadius,
        bottomStart = 4.dp,
        bottomEnd   = 4.dp
    )
    // NOTE: The 'middleButtonShape' is now the effective bottom button, so its shape must change.
    val bottomButtonShape = RoundedCornerShape( // Re-purposing this variable name for the final button
        topStart    = 4.dp,
        topEnd      = 4.dp,
        bottomStart = outerRadius,
        bottomEnd   = outerRadius
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {

                // ✨ NEW TOP ROW: Title and Close Icon
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 24.dp, end = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Move Note?",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    // Close Icon button
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .size(48.dp)
                            .clickable(onClick = onDismiss)
                            .padding(12.dp)
                    )
                }

                // Subtitle/Instructions
                Text(
                    text = "Choose how you want to move this note from your main list.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                )
            }

            // --- BUTTONS SECTION ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp), // Adjust bottom padding now that cancel button is gone
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. MOVE TO TRASH BUTTON (Destructive action - TOP shape)
                Button(
                    onClick        = onDeleteConfirm,
                    modifier       = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape          = topButtonShape, // Uses the TOP shape
                    colors         = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor   = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    elevation      = null,
                    contentPadding = zeroPadding
                ) {
                    Text(
                        text       = "Move to Trash",
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // 2. ARCHIVE BUTTON (Positive action for removal - BOTTOM shape)
                Button(
                    onClick        = onArchiveConfirm,
                    modifier       = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape          = bottomButtonShape, // Uses the BOTTOM shape
                    colors         = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor   = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    elevation      = null,
                    contentPadding = zeroPadding
                ) {
                    Text(
                        text       = "Archive Note",
                        fontWeight = FontWeight.SemiBold
                    )
                }
                // Removed Spacer and Cancel Button block completely
            }
        }
    }
}