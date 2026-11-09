/*
 * Copyright (c) 2026 Open Apps Labs
 *
 * This file is part of Jotter
 *
 * Jotter is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Jotter is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Jotter.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package com.openappslabs.jotter.ui.screens.notedetailscreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.openappslabs.jotter.ui.components.CategoryItems
import com.openappslabs.jotter.ui.components.CategorySheet
import com.openappslabs.jotter.ui.components.ConfirmationDialog
import com.openappslabs.jotter.ui.components.TwoOptionButton
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Visibility
import com.openappslabs.jotter.ui.components.NoteActionSheet
import com.openappslabs.jotter.ui.theme.rememberJotterHaptics
import com.openappslabs.jotter.utils.NoteUtils
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
    viewModel: NoteDetailViewModel = hiltViewModel()
) {
    val haptics = rememberJotterHaptics()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val userPrefs by viewModel.userPreferences.collectAsStateWithLifecycle()
    var showDiscardDialog by remember { mutableStateOf(false) }
    var showCategorySheet by remember { mutableStateOf(false) }
    var showRestoreNoteDialog by remember { mutableStateOf(false) }
    var pendingDiscard by remember { mutableStateOf(false) }
    var showNoteActionSheet by remember { mutableStateOf(false) }
    val noteActionSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val availableCategories by viewModel.availableCategories.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    val titleFocusRequester = remember { FocusRequester() }
    val contentFocusRequester = remember { FocusRequester() }


    var isViewMode by remember(uiState.isNotePersisted, userPrefs.defaultOpenInEdit) {
        val initialViewMode = if (uiState.isNotePersisted) {
            !userPrefs.defaultOpenInEdit
        } else {
            false
        }
        mutableStateOf(initialViewMode)
    }

    val isSaveEnabled = !isViewMode && uiState.isModified && (uiState.title.isNotBlank() || uiState.content.isNotBlank())

    val locale = Locale.getDefault()
    val dateString = remember(uiState.createdTime, userPrefs.is24HourFormat, userPrefs.dateFormat, locale) {
        val datePattern = if (userPrefs.dateFormat.contains("/")) "${userPrefs.dateFormat}/yyyy"
        else "${userPrefs.dateFormat} yyyy"
        val timePattern = if (userPrefs.is24HourFormat) "HH:mm" else "hh:mm a"
        val pattern = "$datePattern, $timePattern"
        SimpleDateFormat(pattern, locale).format(Date(uiState.createdTime))
    }

    val modifiedDateString = remember(uiState.lastEdited, userPrefs.is24HourFormat, userPrefs.dateFormat, locale) {
        val datePattern = if (userPrefs.dateFormat.contains("/")) "${userPrefs.dateFormat}/yyyy"
        else "${userPrefs.dateFormat} yyyy"
        val timePattern = if (userPrefs.is24HourFormat) "HH:mm" else "hh:mm a"
        val pattern = "$datePattern, $timePattern"
        SimpleDateFormat(pattern, locale).format(Date(uiState.lastEdited))
    }

    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val primaryColor = MaterialTheme.colorScheme.primary

    val titleStyle = remember(onSurfaceColor) {
        TextStyle(
            fontSize = 24.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.Bold,
            color = onSurfaceColor,
            platformStyle = PlatformTextStyle(includeFontPadding = false),
            lineHeightStyle = LineHeightStyle(
                alignment = LineHeightStyle.Alignment.Center,
                trim = LineHeightStyle.Trim.Both
            )
        )
    }

    val contentStyle = remember(onSurfaceColor) {
        TextStyle(
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Normal,
            color = onSurfaceColor.copy(alpha = 0.95f),
            platformStyle = PlatformTextStyle(includeFontPadding = false),
            lineHeightStyle = LineHeightStyle(
                alignment = LineHeightStyle.Alignment.Center,
                trim = LineHeightStyle.Trim.Both
            )
        )
    }

    val cursorBrush = remember(primaryColor) {
        SolidColor(primaryColor)
    }

    LaunchedEffect(uiState.isNotePersisted) {
        if (!uiState.isNotePersisted) {
            delay(200)
            titleFocusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    LaunchedEffect(isImeVisible) {
        if (!isImeVisible && pendingDiscard) {
            pendingDiscard = false
            showDiscardDialog = true
        }
    }

    fun handleBack() {
        if (uiState.isNotePersisted && !isViewMode && uiState.isModified) {
            if (isImeVisible) {
                pendingDiscard = true
                keyboardController?.hide()
            } else {
                showDiscardDialog = true
            }
        } else {
            keyboardController?.hide()
            if (isImeVisible) {
                scope.launch {
                    delay(120)
                    onBackClick()
                }
            } else {
                onBackClick()
            }
        }
    }

    val shouldInterceptBack = (uiState.isNotePersisted && !isViewMode && uiState.isModified) || 
                              (!uiState.isNotePersisted && isImeVisible)
    BackHandler(enabled = shouldInterceptBack) {
        handleBack()
    }

    val isTrashed = uiState.isTrashed

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    if (uiState.isNotePersisted && !isTrashed) {
                        TwoOptionButton(
                            isSecondSelected = isViewMode,
                            onToggle = {
                                haptics.tick()
                                isViewMode = !isViewMode
                            },
                            firstIcon = Icons.Default.Create,
                            secondIcon = Icons.Default.Visibility,
                            firstDescription = "Edit Mode",
                            secondDescription = "View Mode",
                            iconButtonSize = 48.dp
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
                        onClick = {
                            haptics.click()
                            handleBack()
                        },
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            val showCloseIcon = !isViewMode && uiState.isModified
                            Icon(
                                imageVector = if (showCloseIcon) Icons.Default.Close else Icons.Default.ChevronLeft,
                                contentDescription = if (showCloseIcon) "Close" else "Back",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                },
                actions = {
                    if (isTrashed) {
                        Surface(
                            onClick = {
                                haptics.click()
                                showRestoreNoteDialog = true
                            },
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
                        Surface(
                            onClick = {
                                haptics.click()
                                showNoteActionSheet = true
                            },
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
                        Surface(
                            onClick = {
                                haptics.success()
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
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        if (!isTrashed && !isViewMode) {
                            contentFocusRequester.requestFocus()
                        }
                    }
                    .padding(horizontal = 16.dp)
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                BasicTextField(
                    value = uiState.title,
                    onValueChange = { viewModel.updateTitle(it) },
                    readOnly = isViewMode || isTrashed,
                    textStyle = titleStyle,
                    cursorBrush = cursorBrush,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { contentFocusRequester.requestFocus() }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(titleFocusRequester),
                    decorationBox = { innerTextField ->
                        Box {
                            if (uiState.title.isEmpty() && !isViewMode && !isTrashed) {
                                Text(
                                    text = "Untitled",
                                    style = titleStyle.copy(color = onSurfaceColor.copy(alpha = 0.3f))
                                )
                            }
                            innerTextField()
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                BasicTextField(
                    value = uiState.content,
                    onValueChange = { viewModel.updateContent(it) },
                    readOnly = isViewMode || isTrashed,
                    textStyle = contentStyle,
                    cursorBrush = cursorBrush,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(contentFocusRequester),
                    decorationBox = { innerTextField ->
                        Box {
                            if (uiState.content.isEmpty() && !isViewMode && !isTrashed) {
                                Text(
                                    text = "Start typing...",
                                    style = contentStyle.copy(color = onSurfaceColor.copy(alpha = 0.3f))
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showCategorySheet) {
        CategorySheet(
            categories = CategoryItems(availableCategories),
            selectedCategory = uiState.category,
            onCategorySelect = { newCategory ->
                haptics.tick()
                viewModel.updateCategory(newCategory)
                isViewMode = false
            },
            onManageCategoriesClick = {
                haptics.click()
                onManageCategoryClick()
            },
            onDismiss = { showCategorySheet = false }
        )
    }

    if (showDiscardDialog) {
        ConfirmationDialog(
            icon = Icons.Default.Close,
            title = "Discard changes?",
            message = "You have unsaved changes. If you discard them now, your changes will be lost.",
            confirmText = "Discard",
            isDestructive = true,
            onDismiss = { showDiscardDialog = false },
            onConfirm = {
                showDiscardDialog = false
                keyboardController?.hide()
                if (uiState.isNotePersisted) {
                    viewModel.undoChanges()
                    isViewMode = true
                } else {
                    scope.launch {
                        delay(120)
                        onBackClick()
                    }
                }
            }
        )
    }


    if (showNoteActionSheet) {
        NoteActionSheet(
            sheetState = noteActionSheetState,
            scope = scope,
            createdDate = dateString,
            modifiedDate = modifiedDateString,
            wordCount = NoteUtils.countWords(uiState.content),
            charCount = NoteUtils.countCharacters(uiState.content),
            category = uiState.category,
            isPinned = uiState.isPinned,
            isLocked = uiState.isLocked,
            onPinClick = {
                haptics.tick()
                viewModel.togglePin()
            },
            onLockClick = {
                haptics.tick()
                if (userPrefs.isBiometricEnabled) {
                    viewModel.toggleLock()
                } else {
                    scope.launch {
                        if (snackbarHostState.currentSnackbarData == null) {
                            snackbarHostState.showSnackbar(
                                message = "Enable Note Lock in Settings to use this feature",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                }
            },
            onShareClick = {
                haptics.click()
                val textToShare = NoteUtils.formatNote(uiState.title, uiState.content)
                NoteUtils.shareNote(context, textToShare)
                showNoteActionSheet = false
            },
            onDeleteClick = {
                haptics.heavy()
                showNoteActionSheet = false
                viewModel.deleteNote()
                onBackClick()
            },
            onCategoryClick = {
                haptics.click()
                showCategorySheet = true
            },
            onDismissRequest = { showNoteActionSheet = false }
        )
    }

    if (showRestoreNoteDialog) {
        ConfirmationDialog(
            icon = Icons.Default.Restore,
            title = "Restore Note?",
            message = "Are you sure you want to restore this note to your main list?",
            confirmText = "Restore",
            isDestructive = false,
            onDismiss = { showRestoreNoteDialog = false },
            onConfirm = {
                showRestoreNoteDialog = false
                viewModel.restoreNote()
                onBackClick()
            }
        )
    }
}