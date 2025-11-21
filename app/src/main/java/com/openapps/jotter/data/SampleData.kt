package com.openapps.jotter.data

data class Note(
    val id: Int,
    val title: String,
    val content: String,
    val category: String = "Uncategorized"
)

val sampleNotes = listOf(
    Note(
        id = 1,
        title = "Grocery List",
        content = "Milk, Eggs, Bread, Butter, Cheese, Apples, Bananas, Coffee beans.",
        category = "Personal"
    ),
    Note(
        id = 2,
        title = "App Ideas",
        content = "1. A minimal note-taking app (Jotter!)\n2. Weather app with cat pictures\n3. Expense tracker that judges your spending habits.",
        category = "Ideas"
    ),
    Note(
        id = 3,
        title = "Meeting Notes",
        content = "Discuss Q4 roadmap. Key points: Performance optimization, New expressive design system, User feedback analysis. Remember to bring the projector adapter.",
        category = "Work"
    ),
    Note(
        id = 4,
        title = "",
        content = "Just a quick thought: why do we press harder on the remote when the batteries are dead?",
        category = "Random"
    ),
    Note(
        id = 5,
        title = "Project Jotter Specs",
        content = "Material 3 Expressive design.\n- Large rounded corners\n- Pastel colors\n- Smooth animations\n- Staggered grid layout\n- Edge-to-edge support",
        category = "Work"
    ),
    Note(
        id = 6,
        title = "Books to Read",
        content = "The Pragmatic Programmer, Clean Code, Project Hail Mary, Dune.",
        category = "Personal"
    ),
    Note(
        id = 7,
        title = "Code Snippet",
        content = "val layout = StaggeredGridCells.Fixed(2)",
        category = "Coding"
    ),
    Note(
        id = 8,
        title = "Long Rant",
        content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
        category = "Random"
    ),
    Note(
        id = 9,
        title = "Weekend Plans",
        content = "Hiking on Saturday morning. Movie night (maybe Sci-Fi?). Sunday brunch with family.",
        category = "Personal"
    ),
    Note(
        id = 10,
        title = "Budget",
        content = "Rent: $1200\nGroceries: $300\nUtilities: $150\nFun: $200\nSavings: $500",
        category = "Finance"
    )
)