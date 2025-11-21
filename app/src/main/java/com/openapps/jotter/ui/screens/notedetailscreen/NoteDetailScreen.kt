package com.openapps.jotter.ui.screens.notedetailscreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openapps.jotter.ui.components.DeleteNoteDialog
import com.openapps.jotter.ui.components.DiscardChangesDialog
import com.openapps.jotter.ui.components.Header
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NoteDetailScreen(
    modifier: Modifier = Modifier,
    noteId: Int? = null,
    initialTitle: String = "",
    initialContent: String = "",
    category: String = "Uncategorized",
    lastEdited: Long = System.currentTimeMillis(),
    onBackClick: () -> Unit,
    onSave: (title: String, content: String) -> Unit,
) {
    // 1. STATE
    var title by remember(noteId) { mutableStateOf(initialTitle) }
    var content by remember(noteId) { mutableStateOf(initialContent) }

    // ✨ STATE FIX: Track if the note exists (is previously saved or opened from existing)
    // This ensures the "Edit/View" button stays visible after saving a new note.
    var isNotePersisted by remember(noteId) { mutableStateOf(noteId != null) }

    // Dialog & Keyboard State Logic
    var showDiscardDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var pendingDiscard by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0

    val isEditing = noteId != null // Valid for initial load only

    val isContentModified = (title.trim() != initialTitle.trim()) || (content.trim() != initialContent.trim())
    val isSaveEnabled = isContentModified && (title.isNotBlank() || content.isNotBlank())

    // 2. VIEW/EDIT MODE
    var isViewMode by remember { mutableStateOf(isEditing) }
    // Force view mode to false initially if it's a new note
    if (!isEditing && !isContentModified) isViewMode = false

    val dateString = remember(lastEdited) {
        SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(lastEdited))
    }

    // WATCHER: If keyboard closes and we were waiting, show dialog
    LaunchedEffect(isImeVisible) {
        if (!isImeVisible && pendingDiscard) {
            pendingDiscard = false
            showDiscardDialog = true
        }
    }

    // Helper to handle back navigation logic safely
    fun handleBack() {
        if (isSaveEnabled) {
            if (isImeVisible) {
                pendingDiscard = true
                keyboardController?.hide()
            } else {
                showDiscardDialog = true
            }
        } else {
            onBackClick()
        }
    }

    // Intercept System Back Button
    BackHandler(enabled = isSaveEnabled) {
        handleBack()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            Header(
                title = "",
                onBackClick = { handleBack() },
                isEditing = !isViewMode,
                // ✨ LOGIC FIX: Show toggle if note is persisted, regardless of current mode
                onToggleEditView = if (isNotePersisted) { { isViewMode = !isViewMode } } else null,

                // Save Action (Edit Mode)
                onSaveClick = {
                    if (isSaveEnabled) {
                        onSave(title.trim(), content.trim())
                        // ✨ Mark as persisted so the UI knows it's no longer "new"
                        isNotePersisted = true
                        isViewMode = true
                        keyboardController?.hide()
                    }
                },
                isSaveEnabled = isSaveEnabled,

                // Delete Action (View Mode Only)
                onDeleteClick = if (isViewMode) {
                    { showDeleteDialog = true }
                } else {
                    null
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .imePadding()
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // --- METADATA ROW ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = category.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = dateString,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- TITLE ---
            BasicTextField(
                value = title,
                onValueChange = { title = it },
                readOnly = isViewMode,
                textStyle = TextStyle(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    Box {
                        if (title.isEmpty() && !isViewMode) {
                            Text(
                                text = "Untitled",
                                style = TextStyle(
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                )
                            )
                        }
                        innerTextField()
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- BODY ---
            BasicTextField(
                value = content,
                onValueChange = { content = it },
                readOnly = isViewMode,
                textStyle = TextStyle(
                    fontSize = 18.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    Box {
                        if (content.isEmpty() && !isViewMode) {
                            Text(
                                text = "Start typing...",
                                style = TextStyle(
                                    fontSize = 18.sp,
                                    lineHeight = 28.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                )
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
    }

    // Discard Logic
    if (showDiscardDialog) {
        DiscardChangesDialog(
            onDismiss = { showDiscardDialog = false },
            onConfirm = {
                showDiscardDialog = false
                if (isNotePersisted) {
                    // Existing (or Saved) Note -> Revert & View Mode
                    title = initialTitle
                    content = initialContent
                    isViewMode = true
                } else {
                    // Never Saved -> Exit
                    onBackClick()
                }
            }
        )
    }

    // Delete Logic
    if (showDeleteDialog) {
        DeleteNoteDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                showDeleteDialog = false
                onBackClick()
            }
        )
    }
}