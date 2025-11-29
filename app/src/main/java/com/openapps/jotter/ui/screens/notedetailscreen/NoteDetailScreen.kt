package com.openapps.jotter.ui.screens.notedetailscreen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openapps.jotter.ui.components.CategorySheet
import com.openapps.jotter.ui.components.DeleteNoteDialog
import com.openapps.jotter.ui.components.DiscardChangesDialog
import com.openapps.jotter.ui.components.EditViewButton
import com.openapps.jotter.ui.components.NoteActionDialog
import com.openapps.jotter.ui.components.PinLockBar
import com.openapps.jotter.ui.components.RestoreNoteDialog
import com.openapps.jotter.ui.theme.rememberJotterHaptics
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onManageCategoryClick: () -> Unit = {},
    onNavigateToArchive: () -> Unit,
    onNavigateToTrash: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: NoteDetailViewModel = hiltViewModel()
) {
    val haptics = rememberJotterHaptics()

    // 1. VIEWMODEL STATE
    val uiState by viewModel.uiState.collectAsState()
    val userPrefs by viewModel.userPreferences.collectAsState()

    // Dialog & Local UI State
    var showDiscardDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showCategorySheet by remember { mutableStateOf(false) }
    var showRestoreNoteDialog by remember { mutableStateOf(false) }
    var pendingDiscard by remember { mutableStateOf(false) }

    // Controls visibility of the combined Archive/Trash dialog
    var showNoteActionDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // Helper for categories (FIXED: Now observing live database data)
    val availableCategories by viewModel.availableCategories.collectAsState()

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0

    // 2. VIEW MODE LOGIC
    // We default to Edit mode if it's a new note (not persisted), otherwise View mode
    var isViewMode by remember(uiState.isNotePersisted, userPrefs.defaultOpenInEdit) {
        val initialViewMode = if (uiState.isNotePersisted) {
            !userPrefs.defaultOpenInEdit
        } else {
            false
        }
        mutableStateOf(initialViewMode)
    }

    // Check for modifications to enable Save button
    // Updated to check isModified from ViewModel
    val isSaveEnabled = !isViewMode && uiState.isModified && (uiState.title.isNotBlank() || uiState.content.isNotBlank())

    val dateString = remember(uiState.lastEdited) {
        SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date(uiState.createdTime))
    }

    LaunchedEffect(isImeVisible) {
        if (!isImeVisible && pendingDiscard) {
            pendingDiscard = false
            showDiscardDialog = true
        }
    }

    fun handleBack() {
        // Use isModified to determine if we need to prompt for discard
        if (!isViewMode && uiState.isModified) {
            if (isImeVisible) {
                pendingDiscard = true
                keyboardController?.hide()
            } else {
                showDiscardDialog = true
            }
        } else {
            if (!isViewMode && uiState.isNotePersisted) {
                isViewMode = true
            } else {
                onBackClick()
            }
        }
    }

    BackHandler(enabled = true) {
        handleBack()
    }

    val isArchivedOrTrashed = uiState.isArchived || uiState.isTrashed

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    if (uiState.isNotePersisted && !isArchivedOrTrashed) {
                        EditViewButton(
                            isEditing = !isViewMode,
                            onToggle = { isViewMode = !isViewMode }
                        )
                    } else {
                        Text(
                            text = "",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                navigationIcon = {
                    Surface(
                        onClick = { handleBack() },
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            // Only show Close (X) if in Edit Mode AND Modified
                            val showCloseIcon = !isViewMode && uiState.isModified
                            Icon(
                                imageVector = if (showCloseIcon) Icons.Default.Close else Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = if (showCloseIcon) "Close" else "Back",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                },
                actions = {
                    if (isArchivedOrTrashed) {
                        // Show Restore Button (Active for Archived/Trashed notes)
                        Surface(
                            onClick = { showRestoreNoteDialog = true },
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            enabled = true,
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Restore,
                                    contentDescription = "Restore/Unarchive",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    } else if (isViewMode) {
                        // SHOW ACTIONS MENU: Use MoreVert icon
                        Surface(
                            onClick = { showNoteActionDialog = true }, // Trigger the action dialog
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Actions",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    } else {
                        // Show SAVE BUTTON
                        Surface(
                            onClick = {
                                haptics.success() // Success feedback on save
                                viewModel.saveNote()
                                isViewMode = true
                                keyboardController?.hide()
                            },
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            enabled = isSaveEnabled,
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Done,
                                    contentDescription = "Save",
                                    tint = if (isSaveEnabled) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        },
        bottomBar = {
            // PinLockBar is hidden if archived/trashed
            val showBottomBar = isViewMode && uiState.isNotePersisted && !isArchivedOrTrashed

            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    PinLockBar(
                        isPinned = uiState.isPinned,
                        isLocked = uiState.isLocked,
                        onTogglePin = { viewModel.togglePin() },
                        onToggleLock = { viewModel.toggleLock() }
                    )
                }
            }
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Category Chip (Placeholder Logic)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (uiState.category.isBlank()) {
                                MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.5f)
                            } else {
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                            }
                        )
                        .clickable {
                            if (!isViewMode && !isArchivedOrTrashed) {
                                if (isImeVisible) {
                                    focusManager.clearFocus()
                                    keyboardController?.hide()
                                    scope.launch {
                                        delay(200)
                                        showCategorySheet = true
                                    }
                                } else {
                                    haptics.click()
                                    showCategorySheet = true
                                }
                            }
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (uiState.category.isBlank()) "UNCATEGORIZED" else uiState.category.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.category.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f) else MaterialTheme.colorScheme.primary
                    )
                }

                // 2. Insert Flexible Spacer to push right content to the edge
                Spacer(modifier = Modifier.weight(1f))


                Row(verticalAlignment = Alignment.CenterVertically) {
//                    if (uiState.isPinned) {
//                        Icon(
//                            imageVector = Icons.Default.PushPin,
//                            contentDescription = "Pinned",
//                            tint = MaterialTheme.colorScheme.secondary,
//                            modifier = Modifier.size(14.dp)
//                        )
//                        Spacer(modifier = Modifier.width(8.dp))
//                    }
//                    if (uiState.isLocked) {
//                        Icon(
//                            imageVector = Icons.Default.Lock,
//                            contentDescription = "Locked",
//                            tint = MaterialTheme.colorScheme.error,
//                            modifier = Modifier.size(14.dp)
//                        )
//                        Spacer(modifier = Modifier.width(8.dp))
//                    }
                    // Show Archive/Trash Icon (Optional Metadata)
                    if (uiState.isArchived) {
                        Icon(
                            imageVector = Icons.Default.Archive,
                            contentDescription = "Archived",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    if (uiState.isTrashed) {
                        // Icon used for metadata display when note is trashed
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Trashed",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    if (uiState.isNotePersisted) {
                        Text(
                            text = "Created at $dateString",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- TITLE ---
            BasicTextField(
                value = uiState.title,
                onValueChange = { viewModel.updateTitle(it) },
                // Read-only if in View Mode OR if Archived/Trashed
                readOnly = isViewMode || isArchivedOrTrashed,
                textStyle = TextStyle(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words
                ),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    Box {
                        if (uiState.title.isEmpty() && !isViewMode && !isArchivedOrTrashed) {
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
                value = uiState.content,
                onValueChange = { viewModel.updateContent(it) },
                // Read-only if in View Mode OR if Archived/Trashed
                readOnly = isViewMode || isArchivedOrTrashed,
                textStyle = TextStyle(
                    fontSize = 18.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences
                ),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    Box {
                        if (uiState.content.isEmpty() && !isViewMode && !isArchivedOrTrashed) {
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

    // Category Sheet
    if (showCategorySheet) {
        CategorySheet(
            categories = availableCategories,
            selectedCategory = uiState.category,
            onCategorySelect = { newCategory ->
                haptics.tick()
                viewModel.updateCategory(newCategory)
                isViewMode = false
            },
            onManageCategoriesClick = onManageCategoryClick,
            onDismiss = { showCategorySheet = false }
        )
    }

    // Discard Logic
    if (showDiscardDialog) {
        DiscardChangesDialog(
            onDismiss = { showDiscardDialog = false },
            onConfirm = {
                showDiscardDialog = false
                if (uiState.isNotePersisted) {
                    // Existing Note: Undo changes and stay on screen in View Mode
                    viewModel.undoChanges()
                    isViewMode = true
                } else {
                    // New Note: Exit screen
                    onBackClick()
                }
            }
        )
    }

    // Delete Logic (OLD - Only used for compatibility with the dialog)
    if (showDeleteDialog) {
        DeleteNoteDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                haptics.heavy() // Destructive action feedback
                showDiscardDialog = false
                viewModel.deleteNote()
                onBackClick()
            }
        )
    }

    // ✨ NEW ACTION DIALOG CALL
    if (showNoteActionDialog) { // Assuming you added this state variable
        NoteActionDialog(
            onDismiss = { showNoteActionDialog = false },
            onArchiveConfirm = {
                haptics.tick() // Archive feedback
                showNoteActionDialog = false
                viewModel.archiveNote()
                onBackClick() // FIX 1: Go back to the previous list (Home/Archive)
            },
            onDeleteConfirm = {
                haptics.heavy() // Trash/Delete feedback
                showNoteActionDialog = false
                viewModel.deleteNote()
                onBackClick() // FIX 2: Go back to the previous list (Home/Trash)
            }
        )
    }

    // Restore Logic
    if (showRestoreNoteDialog) {
        RestoreNoteDialog(
            onDismiss = { showRestoreNoteDialog = false },
            onConfirm = {
                haptics.success() // Restore feedback
                showRestoreNoteDialog = false
                viewModel.restoreNote()
                onBackClick() // ✨ FIX 3: Go back to the previous list (Archive/Trash)
            }
        )
    }
}
