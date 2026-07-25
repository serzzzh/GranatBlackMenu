package ru.granatblack.menu

data class MenuData(
    val restaurant: String,
    val description: String,
    val categories: List<MenuCategory>
)

data class MenuCategory(
    val name: String,
    val items: List<MenuItem>
)

data class MenuItem(
    val name: String,
    val price: Int,
    val weight: String? = null,
    val image: String? = null
)

data class CartEntry(
    val item: MenuItem,
    val category: String,
    val quantity: Int
)
