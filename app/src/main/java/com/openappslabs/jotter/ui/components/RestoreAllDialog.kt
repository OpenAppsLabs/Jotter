package com.openappslabs.jotter.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
fun RestoreAllDialog(
    noteCount: Int,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val outerRadius = 25.dp
    val zeroPadding = PaddingValues(0.dp)

    val topButtonShape = RoundedCornerShape(
        topStart    = outerRadius,
        topEnd      = outerRadius,
        bottomStart = 4.dp,
        bottomEnd   = 4.dp
    )
    val bottomButtonShape = RoundedCornerShape(
        topStart    = 4.dp,
        topEnd      = 4.dp,
        bottomStart = outerRadius,
        bottomEnd   = outerRadius
    )

    // Dynamic text based on the number of notes
    val noteCountText = if (noteCount == 1) "this note" else "$noteCount notes"

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
            Column(
                modifier = Modifier
                    .padding(start = 24.dp, end = 24.dp, top = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text      = "Restore All Notes?",
                    style     = MaterialTheme.typography.headlineSmall,
                    fontWeight= FontWeight.Bold,
                    color     = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text      = "Are you sure you want to restore all $noteCountText to your active list?",
                    style     = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color     = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick        = onConfirm,
                    modifier       = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape          = topButtonShape,
                    colors         = ButtonDefaults.buttonColors(
                        // Use primary for restoration (positive action)
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor   = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    elevation      = null,
                    contentPadding = zeroPadding
                ) {
                    Text(
                        text       = "Restore All",
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick        = onDismiss,
                    modifier       = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape          = bottomButtonShape,
                    colors         = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        contentColor   = MaterialTheme.colorScheme.onSurface
                    ),
                    elevation      = null,
                    contentPadding = zeroPadding
                ) {
                    Text(
                        text       = "Cancel",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}