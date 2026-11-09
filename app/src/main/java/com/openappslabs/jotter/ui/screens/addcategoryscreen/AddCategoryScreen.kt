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

package com.openappslabs.jotter.ui.screens.addcategoryscreen

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.openappslabs.jotter.ui.components.ConfirmationDialog
import com.openappslabs.jotter.ui.theme.rememberJotterHaptics

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryScreen(
    onBackClick: () -> Unit,
    viewModel: AddCategoryScreenViewModel = hiltViewModel()
) {
    val haptics = rememberJotterHaptics()
    var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }
    var editingCategoryName by remember { mutableStateOf<String?>(null) }
    var categoryToDelete by remember { mutableStateOf<String?>(null) }
    var lastToastTime by remember { mutableLongStateOf(0L) }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val density = LocalDensity.current
    val isImeVisible = WindowInsets.ime.getBottom(density) > 0
    val categories by viewModel.categories.collectAsStateWithLifecycle()

    val isActionEnabled = if (editingCategoryName != null) {
        textFieldValue.text.trim() != editingCategoryName && textFieldValue.text.isNotBlank()
    } else {
        textFieldValue.text.isNotBlank()
    }

    val resetInput = remember {
        {
            focusManager.clearFocus()
            textFieldValue = TextFieldValue(text = "", selection = TextRange.Zero)
            editingCategoryName = null
        }
    }

    val performAction = remember(isActionEnabled, textFieldValue, categories, editingCategoryName, lastToastTime) {
        {
            if (isActionEnabled) {
                val trimmed = textFieldValue.text.trim()
                if (trimmed.isNotBlank()) {
                    val isDuplicate = categories.any { it.equals(trimmed, ignoreCase = true) }
                    if (isDuplicate && trimmed != editingCategoryName) {
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastToastTime > 2000) {
                            Toast.makeText(context, "Category \"$trimmed\" already exists", Toast.LENGTH_SHORT).show()
                            lastToastTime = currentTime
                        }
                    } else {
                        haptics.success()
                        if (editingCategoryName != null) {
                            viewModel.updateCategory(editingCategoryName!!, trimmed)
                        } else {
                            viewModel.addCategory(trimmed)
                        }
                        resetInput()
                    }
                }
            }
        }
    }

    val transparentColors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        cursorColor = MaterialTheme.colorScheme.onSurface,
        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    )

    LaunchedEffect(isImeVisible) {
        if (!isImeVisible) {
            if (textFieldValue.text.isBlank()) editingCategoryName = null
        }
    }

    // UX FIX: Handle keyboard close first before reset/back
    BackHandler(enabled = isImeVisible || editingCategoryName != null || textFieldValue.text.isNotEmpty()) {
        if (isImeVisible) {
            focusManager.clearFocus()
        } else {
            resetInput()
        }
    }

    categoryToDelete?.let { categoryName ->
        ConfirmationDialog(
            icon = Icons.Default.Delete,
            title = "Delete Category?",
            message = "Are you sure you want to delete \"$categoryName\"? Notes in this category will become uncategorized.",
            confirmText = "Delete",
            isDestructive = true,
            onDismiss = { categoryToDelete = null },
            onConfirm = {
                viewModel.removeCategory(categoryName)
                categoryToDelete = null
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Manage Categories",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    Surface(
                        onClick = {
                            haptics.click()
                            onBackClick()
                        },
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                }
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(
                    topStart = 12.dp,
                    topEnd = 12.dp,
                    bottomStart = 0.dp,
                    bottomEnd = 0.dp
                ))
            ,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "Create categories to organize your notes.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceContainerHigh,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(start = 0.dp, end = 12.dp)
                    ) {
                        TextField(
                            value = textFieldValue,
                            onValueChange = { newValue ->
                                val filteredText = newValue.text.filter { !it.isWhitespace() }
                                if (filteredText.length <= 15) {
                                    textFieldValue = newValue.copy(text = filteredText)
                                }
                            },
                            placeholder = { Text(if (editingCategoryName == null) "Category name" else "Update name") },
                            textStyle = MaterialTheme.typography.bodyLarge,
                            colors = transparentColors,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.None,
                                autoCorrectEnabled = false,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(onDone = { performAction() }),
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(focusRequester),
                            trailingIcon = null
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                onClick = {
                                    haptics.click()
                                    if (editingCategoryName == null) {
                                        textFieldValue = TextFieldValue("", selection = TextRange.Zero)
                                    } else {
                                        resetInput()
                                    }
                                },
                                modifier = Modifier.size(40.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Close, "Clear", modifier = Modifier.size(18.dp))
                                }
                            }

                            Surface(
                                onClick = { if (isActionEnabled) performAction() },
                                shape = RoundedCornerShape(8.dp),
                                color = if (editingCategoryName != null) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier
                                    .size(40.dp)
                                    .alpha(if (isActionEnabled) 1f else 0.5f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Crossfade(targetState = editingCategoryName != null, label = "ButtonIcon") { isEditing ->
                                        Icon(
                                            imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Add,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "EXISTING CATEGORIES",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp,
                    )
                    Text(
                        text = "${textFieldValue.text.length}/15",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    )
                }
            }

            items(categories, key = { it }) { category ->
                CategoryChip(
                    modifier = Modifier.animateItem(
                        fadeInSpec = tween(300),
                        fadeOutSpec = tween(300)
                    ),
                    text = category,
                    enabled = editingCategoryName == null,
                    onEdit = {
                        haptics.tick()
                        textFieldValue = TextFieldValue(text = category, selection = TextRange(category.length))
                        editingCategoryName = category
                        focusRequester.requestFocus()
                    },
                    onDelete = {
                        haptics.click()
                        categoryToDelete = category
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

@Composable
fun CategoryChip(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val alpha = if (enabled) 1f else 0.38f

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = alpha)),
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)
            )

            Surface(
                onClick = { if (enabled) onEdit() },
                shape = RoundedCornerShape(8.dp),
                color = if (enabled) MaterialTheme.colorScheme.surfaceContainerHighest
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Edit, "Edit", modifier = Modifier.size(18.dp))
                }
            }

            Surface(
                onClick = { if (enabled) onDelete() },
                shape = RoundedCornerShape(8.dp),
                color = if (enabled) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                contentColor = if (enabled) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Delete, "Remove", modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}