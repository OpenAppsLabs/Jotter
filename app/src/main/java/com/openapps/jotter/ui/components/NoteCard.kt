package com.openapps.jotter.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun NoteCard(
    title: String,
    content: String,
    isGridView: Boolean, // New parameter to decide shape
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Define the shape modifier based on the view mode
    val sizeModifier = if (isGridView) {
        // SQUARE: Forces 1:1 aspect ratio
        Modifier.aspectRatio(1f)
    } else {
        // RECTANGLE: Forces a fixed height for uniformity in List view
        Modifier.fillMaxWidth().height(120.dp)
    }

    Card(
        modifier = modifier
            .then(sizeModifier) // Apply the calculated size
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize() // Fill the fixed size card
        ) {
            if (title.isNotEmpty()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1, // Limit title to 1 line for uniformity
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (content.isNotEmpty()) {
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    // Let text fill remaining space but cut off if too long
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}