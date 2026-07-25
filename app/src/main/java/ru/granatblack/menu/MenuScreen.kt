package ru.granatblack.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage

private val Background = Color(0xFF121212)
private val Surface = Color(0xFF1E1E1E)
private val Accent = Color(0xFFC62828)
private val AccentLight = Color(0xFFE53935)
private val TextPrimary = Color(0xFFF5F5F5)
private val TextSecondary = Color(0xFFB0B0B0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(menuData: MenuData) {
    val cart = remember { mutableStateMapOf<String, CartEntry>() }
    var selectedCategory by remember { mutableIntStateOf(0) }
    var showCart by remember { mutableStateOf(false) }

    val gradient = Brush.verticalGradient(listOf(Color(0xFF1A0A0A), Background))

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = menuData.restaurant,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                        Text(
                            text = menuData.description,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A0A0A),
                    titleContentColor = TextPrimary
                )
            )
        },
        bottomBar = {
            if (cart.totalItems() > 0) {
                CartBar(
                    totalItems = cart.totalItems(),
                    totalPrice = cart.totalPrice(),
                    onClick = { showCart = true }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(padding)
        ) {
            CategoryRow(
                categories = menuData.categories,
                selectedIndex = selectedCategory,
                onSelect = { selectedCategory = it }
            )

            val category = menuData.categories.getOrNull(selectedCategory)
            if (category != null) {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(category.items, key = { it.name }) { item ->
                        MenuItemCard(
                            item = item,
                            quantity = cart.quantity(item, category.name),
                            onAdd = { cart.add(item, category.name) },
                            onRemove = { cart.remove(item, category.name) }
                        )
                    }
                }
            }
        }
    }

    if (showCart) {
        ModalBottomSheet(
            onDismissRequest = { showCart = false },
            containerColor = Surface,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            CartSheet(
                cart = cart,
                onClose = { showCart = false }
            )
        }
    }
}

@Composable
private fun CategoryRow(
    categories: List<MenuCategory>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(categories) { index, category ->
            FilterChip(
                selected = index == selectedIndex,
                onClick = { onSelect(index) },
                label = {
                    Text(
                        text = category.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Accent,
                    selectedLabelColor = Color.White,
                    containerColor = Surface,
                    labelColor = TextSecondary
                )
            )
        }
    }
}

@Composable
private fun MenuItemCard(
    item: MenuItem,
    quantity: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(112.dp)
        ) {
            DishImage(
                imageUrl = item.image,
                contentDescription = item.name,
                modifier = Modifier
                    .width(112.dp)
                    .fillMaxHeight()
            )

            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!item.weight.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.weight,
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${item.price} ₽",
                        color = AccentLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }

                if (quantity > 0) {
                    QuantityControl(quantity, onRemove, onAdd)
                } else {
                    IconButton(
                        onClick = onAdd,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Accent)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Добавить", tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun DishImage(
    imageUrl: String?,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    if (!imageUrl.isNullOrBlank()) {
        AsyncImage(
            model = imageUrl,
            contentDescription = contentDescription,
            modifier = modifier.clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.ic_dish_placeholder),
            error = painterResource(R.drawable.ic_dish_placeholder)
        )
    } else {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                .background(Color(0xFF2A2A2A)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Restaurant,
                contentDescription = contentDescription,
                tint = Accent,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Composable
private fun QuantityControl(quantity: Int, onRemove: () -> Unit, onAdd: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF2A2A2A))
        ) {
            Icon(Icons.Default.Remove, contentDescription = "Убрать", tint = TextPrimary)
        }
        Text(
            text = quantity.toString(),
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        IconButton(
            onClick = onAdd,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Accent)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Добавить", tint = Color.White)
        }
    }
}

@Composable
private fun CartBar(totalItems: Int, totalPrice: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Accent),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(12.dp))
                Text("$totalItems поз.", color = Color.White, fontWeight = FontWeight.Medium)
            }
            Text("$totalPrice ₽", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}

@Composable
private fun CartSheet(cart: CartState, onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Корзина",
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (cart.entries().isEmpty()) {
            Text("Корзина пуста", color = TextSecondary)
        } else {
            cart.entries().forEach { entry ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(entry.item.name, color = TextPrimary, fontWeight = FontWeight.Medium)
                        Text(
                            "${entry.quantity} × ${entry.item.price} ₽",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                    Text(
                        "${entry.item.price * entry.quantity} ₽",
                        color = AccentLight,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Accent)
                    .clickable {
                        cart.clearCart()
                        onClose()
                    }
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Очистить корзину", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
