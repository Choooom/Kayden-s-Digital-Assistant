package com.example.kaydensdigitalassistant

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.kaydensdigitalassistant.data.Products
import com.example.kaydensdigitalassistant.data.ProductsViewModel
import com.example.kaydensdigitalassistant.ui.theme.BlueEnd
import com.example.kaydensdigitalassistant.ui.theme.BlueStart
import com.example.kaydensdigitalassistant.ui.theme.ButtonGreen
import com.example.kaydensdigitalassistant.ui.theme.Orange
import com.example.kaydensdigitalassistant.ui.theme.Red
import com.example.kaydensdigitalassistant.ui.theme.dirtyWhite
import com.example.kaydensdigitalassistant.font_abeezee
import com.example.kaydensdigitalassistant.font_archivo_bold
import com.example.kaydensdigitalassistant.font_notosans_bold
import com.example.kaydensdigitalassistant.kanit_bold

@Composable
fun Inventory(navController: NavController) {
    val productsViewModel = LocalProductsViewModel.current
    val allProducts by productsViewModel.allProducts.observeAsState(initial = emptyList())
    val insets = WindowInsets.systemBars.asPaddingValues()

    var itemType by remember { mutableStateOf("Beer") }
    var searchQuery by remember { mutableStateOf("") }
    var isTypeMenuExpanded by remember { mutableStateOf(false) }

    val productTypes by productsViewModel.productTypes.collectAsState()

    val filteredProducts = allProducts.filter {
        (itemType == "All" || it.type == itemType) &&
                (searchQuery.isEmpty() || it.productName.contains(searchQuery, ignoreCase = true))
    }

    //---------

    //---------
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = insets.calculateTopPadding())
            .background(Brush.horizontalGradient(colors = listOf(BlueStart, BlueEnd))),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopBar(navController = navController, "INVENTORY")

        Row(
            modifier = Modifier
                .fillMaxWidth(0.98f)
                .height(70.dp)
                .padding(top = 20.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(Color.White),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "All Items >",
                color = Color.Gray,
                fontFamily = font_abeezee,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.padding(start = 15.dp).clickable { isTypeMenuExpanded = true }
            )
            Text(
                text = itemType,
                fontFamily = font_archivo_bold,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 5.dp)
            )

            DropdownMenu(
                expanded = isTypeMenuExpanded,
                onDismissRequest = { isTypeMenuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("All") },
                    onClick = {
                        itemType = "All"
                        isTypeMenuExpanded = false
                    }
                )
                productTypes.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = {
                            itemType = type
                            isTypeMenuExpanded = false
                        }
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth(0.98f)
                .fillMaxHeight(0.98f)
                .padding(top = 5.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(Color.White)
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search products...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(55.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = BlueStart,
                    unfocusedIndicatorColor = dirtyWhite,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                )
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                items(
                    items = filteredProducts,
                    key = { it.productId }
                ) { product ->
                    InventoryItem(
                        name = product.productName,
                        amount = product.normalPrice,
                        quantity = product.stock,
                        product = product
                    )
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = dirtyWhite
                    )
                }
            }
        }
    }
}

@Composable
fun InventorySection(filteredProducts: List<Products>) {
    val viewModel = LocalProductsViewModel.current
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .fillMaxHeight(0.9f)
            .padding(top = 10.dp)
    ) {
        itemsIndexed(filteredProducts) { index, product ->
            InventoryItem(
                name = product.productName,
                amount = product.normalPrice,
                quantity = product.stock,
                product = product
            )
            viewModel.fetchProductIconByName(product.productName)
        }
    }
}


@Composable
fun InventoryItem(
    name: String,
    amount: Double,
    quantity: Double,
    product: Products,
    viewModel: ProductsViewModel = LocalProductsViewModel.current
) {

    val backgroundColor = when {
        quantity >= 50 -> ButtonGreen
        quantity < 50 && amount >= 10 -> Orange
        else -> Red
    }

    val inventory = LocalReceiptViewModel.current
    val inventoryItems = inventory.productList

    val userRoleViewModel = LocalUserRoleViewModel.current
    val isAdmin by userRoleViewModel.isAdmin.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val viewModel = LocalProductsViewModel.current

    val icon = viewModel.productIcons[name]

    LaunchedEffect(name) {
        viewModel.fetchProductIconByName(name)
    }

    var isEmpty by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(125.dp)
                .padding(10.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(Color.White),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .width(80.dp)
                        .fillMaxHeight()
                        .drawBehind {
                            val borderSize = 1.dp.toPx()
                            drawLine(
                                color = dirtyWhite,
                                start = Offset(size.width - borderSize / 2, 0f),
                                end = Offset(size.width - borderSize / 2, size.height),
                                strokeWidth = borderSize
                            )
                        },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (icon != null) {
                        Image(
                            bitmap = icon.asImageBitmap(),
                            contentDescription = name,
                            modifier = Modifier
                                .size(65.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            colorFilter = if (name == "") {
                                ColorFilter.tint(Color.White)
                            } else {
                                null
                            },
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(65.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Loading...",
                                color = Color.DarkGray,
                                fontSize = 12.sp
                            )
                        }
                    }

                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.65f)
                            .padding(start = 5.dp, top = 15.dp),
                        horizontalAlignment = Alignment.Start,
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.Bottom,

                            ) {
                            Text(
                                name,
                                style = TextStyle(
                                    lineHeight = 15.sp
                                ),
                                fontFamily = font_notosans_bold,
                                fontSize = 10.sp
                            )
                        }
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(dirtyWhite)
                                .height(1.dp)
                        )
                        Spacer(modifier = Modifier.fillMaxHeight(0.55f))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(0.97f)
                                .clip(RoundedCornerShape(3.dp))
                                .background(dirtyWhite),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.Bottom,
                        ) {
                            Text(
                                text = "Stocks Available: $quantity Units",
                                fontFamily = font_notosans_bold,
                                fontSize = 7.sp
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.8f)
                    ){
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .fillMaxHeight(0.2f),
                            verticalAlignment = Alignment.Top
                        ){
                            Text(text = "Status: " + if(quantity > 0) "Available" else "Not Available", fontFamily = font_notosans_bold, fontSize = 5.sp, color = Color.Gray)
                            Canvas(modifier = Modifier.size(15.dp).padding(top = 7.dp)) {
                                drawCircle(
                                    color = backgroundColor,
                                    radius = size.minDimension / 2
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .fillMaxHeight(0.69f),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            var text = if(quantity > 0) "IN STOCK" else "OUT OF STOCK"
                            Text(text = text, fontFamily = font_archivo_bold, fontSize = 11.sp)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .fillMaxHeight()
                                .drawBehind {
                                    val borderWidth = 1.dp.toPx()
                                    drawLine(
                                        color = dirtyWhite,
                                        start = Offset(0f, 0f),
                                        end = Offset(0f, size.height),
                                        strokeWidth = borderWidth
                                    )
                                },
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.Top
                        ){
                            Text(text = "Price: ₱$amount", fontFamily = font_archivo_bold, fontSize = 7.sp,
                                modifier = Modifier.padding(start = 2.dp))
                        }
                    }
                    if (isAdmin) {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight().fillMaxWidth(), horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.Center
                        ) {
                            IconButton(onClick = { showEditDialog = true }) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    modifier = Modifier.size(20.dp),
                                    tint = BlueStart
                                )
                            }
                            IconButton(onClick = { showDeleteDialog = true }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    modifier = Modifier.size(20.dp),
                                    tint = Red
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier
                .height(125.dp)
                .width(14.dp)
                .padding(start = 7.dp, top = 10.dp, bottom = 10.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(backgroundColor)
        )

        if (showEditDialog) {
            EditProductDialog(
                product = product,
                onDismiss = { showEditDialog = false },
                viewModel = viewModel
            )
        }

        if (showDeleteDialog) {
            DeleteProductDialog(
                product = product,
                onDismiss = { showDeleteDialog = false },
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun EditProductDialog(
    product: Products,
    onDismiss: () -> Unit,
    viewModel: ProductsViewModel
) {
    var name by remember { mutableStateOf(product.productName) }
    var type by remember { mutableStateOf(product.type) }
    var normalPrice by remember { mutableStateOf(product.normalPrice.toString()) }
    var discountedPrice by remember { mutableStateOf(product.discountedPrice.toString()) }
    var stock by remember { mutableStateOf(product.stock.toString()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Edit Product Details",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Product Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text("Type") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = normalPrice,
                    onValueChange = { normalPrice = it },
                    label = { Text("Normal Price") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = discountedPrice,
                    onValueChange = { discountedPrice = it },
                    label = { Text("Discounted Price") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = { Text("Stock") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val updatedProduct = product.copy(
                            productName = name,
                            type = type,
                            normalPrice = normalPrice.toDoubleOrNull() ?: 0.0,
                            discountedPrice = discountedPrice.toDoubleOrNull() ?: 0.0,
                            stock = stock.toDoubleOrNull() ?: 0.0
                        )
                        viewModel.updateProduct(updatedProduct)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Changes")
                }
            }
        }
    }
}

@Composable
fun DeleteProductDialog(
    product: Products,
    onDismiss: () -> Unit,
    viewModel: ProductsViewModel
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Delete Product",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Are you sure you want to delete ${product.productName}?",
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            viewModel.deleteProduct(product)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}
