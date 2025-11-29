package com.openapps.jotter.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openapps.jotter.ui.theme.rememberJotterHaptics

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteCard(
    title: String,
    content: String,
    date: String,
    category: String,
    isPinned: Boolean,
    isLocked: Boolean,
    isGridView: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = rememberJotterHaptics()

    val sizeModifier = if (isGridView) {
        Modifier.aspectRatio(1f)
    } else {
        Modifier.fillMaxWidth().height(140.dp)
    }

    // For List view, we show "Locked Note" text.
    val displayContent = if (isLocked && !isGridView) "Locked Note" else content

    val contentColor = if (isLocked) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    else MaterialTheme.colorScheme.onSurfaceVariant

    // --- CATEGORY DISPLAY LOGIC ---
    val isCategoryBlank = category.isBlank()
    val categoryText = if (isCategoryBlank) "UNCATEGORIZED" else category.uppercase()

    // Choose appropriate colors for the footer chip
    val chipContainerColor = if (isCategoryBlank) MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.5f)
    else MaterialTheme.colorScheme.surfaceContainerHigh

    val chipContentColor = if (isCategoryBlank) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
    else MaterialTheme.colorScheme.onSurfaceVariant
    // --- END CATEGORY DISPLAY LOGIC ---


    Card(
        modifier = modifier.then(sizeModifier),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        onClick = {
            haptics.tick()
            onClick()
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // --- TOP SECTION ---
            Column(modifier = Modifier.weight(1f)) { // Added weight to push footer down
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = if (title.isEmpty()) "Untitled" else title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    // Icons Block
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Pin always shows top-right if active
                        if (isPinned) {
                            Icon(
                                imageVector = Icons.Default.PushPin,
                                contentDescription = "Pinned",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Spacer only if we have both icons in this row
                        if (isPinned && (isLocked && !isGridView)) {
                            Spacer(modifier = Modifier.width(12.dp))
                        }

                        // Lock only shows top-right in LIST view
                        if (isLocked && !isGridView) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // --- CONTENT SECTION ---
                if (isLocked && isGridView) {
                    // ✨ GRID VIEW LOCKED: Centered Large Lock Icon
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 20.dp), // ✨ FIX: Push icon up to balance the Title
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                } else {
                    // Standard Text (or "Locked Note" for List view)
                    Text(
                        text = displayContent,
                        style = MaterialTheme.typography.bodyMedium,
                        color = contentColor,
                        maxLines = if (isGridView) 4 else 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 20.sp
                    )
                }
            }

            // --- FOOTER SECTION ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = date,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )

                // ✨ Category Chip (Shows UNCATEGORIZED if category is blank)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(chipContainerColor) // Use dynamic background color
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = categoryText, // Use the pre-computed text (UNCATEGORIZED or Tag name)
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = chipContentColor, // Use the dynamic content color
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
