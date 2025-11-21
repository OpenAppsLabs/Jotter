package com.openapps.jotter.data

data class Note(
    val id: Int,
    val title: String,
    val content: String,
    val createdTime: Long = System.currentTimeMillis(),
    val updatedTime: Long = System.currentTimeMillis(),
    val deletedTime: Long? = null,
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val isTrashed: Boolean = false,
    val isLocked: Boolean = false,
    val category: String = "Uncategorized"
)

val now = System.currentTimeMillis()
const val ONE_HOUR = 3600000L
const val ONE_DAY = 86400000L

val sampleNotes = listOf(
    Note(
        id = 1,
        title = "Grocery List",
        content = "Milk, Eggs, Bread, Butter, Cheese, Apples, Bananas, Coffee beans.",
        createdTime = now - ONE_HOUR,
        updatedTime = now,
        deletedTime = null,
        isPinned = true,
        isArchived = false,
        isTrashed = false,
        isLocked = false,
        category = "Personal"
    ),
    Note(
        id = 2,
        title = "App Ideas",
        content = "1. A minimal note-taking app (Jotter!)\n2. Weather app with cat pictures\n3. Expense tracker that judges your spending habits.",
        createdTime = now - (ONE_DAY * 2),
        updatedTime = now - ONE_HOUR,
        deletedTime = null,
        isPinned = true,
        isArchived = false,
        isTrashed = false,
        isLocked = false,
        category = "Ideas"
    ),
    Note(
        id = 3,
        title = "Meeting Notes",
        content = "Discuss Q4 roadmap. Key points: Performance optimization, New expressive design system, User feedback analysis. Remember to bring the projector adapter.",
        createdTime = now - (ONE_DAY * 1),
        updatedTime = now - (ONE_DAY * 1),
        deletedTime = null,
        isPinned = false,
        isArchived = false,
        isTrashed = false,
        isLocked = false,
        category = "Work"
    ),
    Note(
        id = 4,
        title = "",
        content = "Just a quick thought: why do we press harder on the remote when the batteries are dead?",
        createdTime = now - (ONE_HOUR * 5),
        updatedTime = now - (ONE_HOUR * 5),
        deletedTime = null,
        isPinned = false,
        isArchived = false,
        isTrashed = false,
        isLocked = false,
        category = "Random"
    ),
    Note(
        id = 5,
        title = "Project Jotter Specs",
        content = "Material 3 Expressive design.\n- Large rounded corners\n- Pastel colors\n- Smooth animations\n- Staggered grid layout\n- Edge-to-edge support",
        createdTime = now - (ONE_DAY * 3),
        updatedTime = now - (ONE_DAY * 3),
        deletedTime = null,
        isPinned = false,
        isArchived = false,
        isTrashed = false,
        isLocked = false,
        category = "Work"
    ),
    Note(
        id = 6,
        title = "Books to Read",
        content = "The Pragmatic Programmer, Clean Code, Project Hail Mary, Dune.",
        createdTime = now - (ONE_DAY * 5),
        updatedTime = now - (ONE_DAY * 4),
        deletedTime = null,
        isPinned = false,
        isArchived = false,
        isTrashed = false,
        isLocked = false,
        category = "Personal"
    ),
    Note(
        id = 7,
        title = "Code Snippet",
        content = "val layout = StaggeredGridCells.Fixed(2)",
        createdTime = now - (ONE_DAY * 6),
        updatedTime = now - (ONE_DAY * 6),
        deletedTime = null,
        isPinned = false,
        isArchived = false,
        isTrashed = false,
        isLocked = false,
        category = "Coding"
    ),
    Note(
        id = 8,
        title = "Long Rant",
        content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
        createdTime = now - (ONE_DAY * 10),
        updatedTime = now - (ONE_DAY * 10),
        deletedTime = null,
        isPinned = false,
        isArchived = true,
        isTrashed = false,
        isLocked = false,
        category = "Random"
    ),
    Note(
        id = 9,
        title = "Weekend Plans",
        content = "Hiking on Saturday morning. Movie night (maybe Sci-Fi?). Sunday brunch with family.",
        createdTime = now - (ONE_DAY * 2),
        updatedTime = now - (ONE_DAY * 2),
        deletedTime = null,
        isPinned = false,
        isArchived = false,
        isTrashed = false,
        isLocked = false,
        category = "Personal"
    ),
    Note(
        id = 10,
        title = "Top Secret Budget",
        content = "Rent: $1200\nGroceries: $300\nUtilities: $150\nFun: $200\nSavings: $500",
        createdTime = now - (ONE_DAY * 7),
        updatedTime = now - (ONE_DAY * 7),
        deletedTime = null,
        isPinned = false,
        isArchived = false,
        isTrashed = false,
        isLocked = true,
        category = "Finance"
    )
)