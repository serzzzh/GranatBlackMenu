package ru.granatblack.menu

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap

typealias CartState = SnapshotStateMap<String, CartEntry>

fun cartKey(item: MenuItem, category: String) = "$category|${item.name}"

fun CartState.add(item: MenuItem, category: String) {
    val key = cartKey(item, category)
    val existing = this[key]
    this[key] = if (existing != null) {
        existing.copy(quantity = existing.quantity + 1)
    } else {
        CartEntry(item, category, 1)
    }
}

fun CartState.remove(item: MenuItem, category: String) {
    val key = cartKey(item, category)
    val existing = this[key] ?: return
    if (existing.quantity <= 1) {
        remove(key)
    } else {
        this[key] = existing.copy(quantity = existing.quantity - 1)
    }
}

fun CartState.quantity(item: MenuItem, category: String): Int {
    return this[cartKey(item, category)]?.quantity ?: 0
}

fun CartState.entries(): List<CartEntry> = values.sortedBy { it.category + it.item.name }

fun CartState.totalItems(): Int = values.sumOf { it.quantity }

fun CartState.totalPrice(): Int = values.sumOf { it.item.price * it.quantity }

fun CartState.clearCart() {
    clear()
}
