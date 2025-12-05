package com.openappslabs.jotter.navigation

object AppRoutes {
    // --- Primary Screens ---
    const val HOME = "home"
    const val SETTINGS = "settings"

    // --- Note Management Screens ---
    const val ARCHIVE = "archive"
    const val TRASH = "trash"

    // --- Utility Screens ---
    const val ADD_CATEGORY = "add_category"
    const val BACKUP_RESTORE = "backup_restore"
    const val PRIVACY_POLICY = "privacy_policy"
    const val ABOUT = "about"

    // --- Note Detail/Edit Screen (Updated Name and Structure) ---
    // Key used to pass the note's ID
    const val NOTE_ID_KEY = "noteId"

    // Base route name for the detail screen
    const val NOTE_DETAIL = "note_detail"

    // Helper property to get the route with the argument placeholder (for NavHost definition)
    const val NOTE_DETAIL_ROUTE_WITH_ARGS = "$NOTE_DETAIL/{$NOTE_ID_KEY}"
}