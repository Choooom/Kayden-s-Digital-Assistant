package com.example.kaydensdigitalassistant

import android.graphics.drawable.Icon
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.kaydensdigitalassistant.data.InventoryItems
import com.example.kaydensdigitalassistant.data.ReceiptItem
import com.example.kaydensdigitalassistant.data.ReceiptViewModel
import com.example.kaydensdigitalassistant.ui.theme.BlueEnd
import com.example.kaydensdigitalassistant.ui.theme.BlueStart
import com.example.kaydensdigitalassistant.ui.theme.dirtyWhite

@Composable
fun ProductList(navController: NavController, onClose : () -> Unit){
    val viewModel = LocalReceiptViewModel.current

    var selectedCategory by remember { mutableStateOf(mutableStateListOf(true, false, false, false)) }
    var searchQuery by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
                .background(Color.White)
        ){
            Row(modifier = Modifier.fillMaxWidth()){
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Close",
                    modifier = Modifier
                        .padding(start = 10.dp, top = 10.dp)
                        .clickable {
                            onClose()
                        }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ){
                Text(
                    text = "Beers",
                    fontSize = 18.sp,
                    modifier = Modifier
                        .clickable {
                            selectedCategory[0] = true
                            selectedCategory[1] = false
                            selectedCategory[2] = false
                            selectedCategory[3] = false
                        },
                    fontFamily = if(selectedCategory[0] == true) font_notosans_bold else font_notosans_regular,
                    style = TextStyle(
                        textDecoration = if(selectedCategory[0] == true) TextDecoration.Underline else TextDecoration.None)
                )
                Text(
                    text = "Softdrinks",
                    fontSize = 18.sp,
                    modifier = Modifier
                        .clickable {
                            selectedCategory[0] = false
                            selectedCategory[1] = true
                            selectedCategory[2] = false
                            selectedCategory[3] = false
                                   },
                    fontFamily = if(selectedCategory[1] == true) font_notosans_bold else font_notosans_regular,
                    style = TextStyle(textDecoration = if(selectedCategory[1] == true) TextDecoration.Underline else TextDecoration.None)
                )
                Text(
                    text = "Energy Drinks",
                    fontSize = 18.sp,
                    modifier = Modifier
                        .clickable {
                            selectedCategory[0] = false
                            selectedCategory[1] = false
                            selectedCategory[2] = true
                            selectedCategory[3] = false
                                   },
                    fontFamily = if(selectedCategory[2] == true) font_notosans_bold else font_notosans_regular,
                    style = TextStyle(textDecoration = if(selectedCategory[2] == true) TextDecoration.Underline else TextDecoration.None)
                )
                Text(
                    text = "ETC",
                    fontSize = 18.sp,
                    modifier = Modifier
                        .clickable {
                            selectedCategory[0] = false
                            selectedCategory[1] = false
                            selectedCategory[2] = false
                            selectedCategory[3] = true
                                   },
                    fontFamily = if(selectedCategory[3] == true) font_notosans_bold else font_notosans_regular,
                    style = TextStyle(textDecoration = if(selectedCategory[3] == true) TextDecoration.Underline else TextDecoration.None)
                )
            }
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 7.dp)
                .height(5.dp)
                .background(
                    Brush.linearGradient(colors = listOf(BlueStart, BlueEnd))
                )
            )

            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, start = 10.dp, end = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically)
            {
                Text(
                    text = "PRODUCTS",
                    fontFamily = font_notosans_bold,
                    fontSize = 20.sp
                )
                SearchBar(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    onSearch = {

                    }
                )
            }
            Column(modifier = Modifier.fillMaxSize()){
                if(selectedCategory[0] == true){
                    ProductColumn("Beer")
                }else if(selectedCategory[1] == true){
                    ProductColumn("Softdrink")
                }else if(selectedCategory[2] == true){
                    ProductColumn("Energy-Drink")
                }else if(selectedCategory[3] == true){
                    ProductColumn("BeerFlavored")
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    BasicTextField(
        modifier = modifier
            .fillMaxWidth(0.7f)
            .height(35.dp)
            .border(1.dp, Color.Black, RoundedCornerShape(20.dp))
            .padding(horizontal = 8.dp),
        value = value,
        onValueChange = onValueChange,
        textStyle = TextStyle(
            color = Color.Black,
            fontFamily = font_archivo_light,
            fontSize = 14.sp
        ),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = "Search Product",
                            color = Color.Gray,
                            fontFamily = font_archivo_light,
                            fontSize = 14.sp
                        )
                    }
                    innerTextField()
                }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch()
                defaultKeyboardAction(ImeAction.Search)
            }
        )
    )
}

@Composable
fun ProductColumn(type: String) {
    val receiptViewModel = LocalReceiptViewModel.current
    val filteredList = receiptViewModel.productList.filter { it.type == type }
    val pairedList = filteredList.chunked(2) // Split into pairs

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(pairedList.size) { index ->
            val pair = pairedList[index]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(vertical = 8.dp)
            ) {
                // First item with weight(1f)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    InventoryItem(
                        name = pair[0].name,
                        type = pair[0].type,
                        price = pair[0].price,
                        index = index
                    )
                }

                // Second item or an empty Box if no second item in the pair
                if (pair.size > 1) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                    ) {
                        InventoryItem(
                            name = pair[1].name,
                            type = pair[1].type,
                            price = pair[1].price,
                            index = index
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f)) // Empty space for odd items
                }
            }
        }
    }
}


@Composable
fun InventoryItem(name: String, type: String, price:Double, index: Int){
    val viewModel = LocalReceiptViewModel.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(dirtyWhite)
    ){
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Row(
                modifier = Modifier
                    .fillMaxHeight(0.7f)
                    .fillMaxWidth(0.8f)
                    .padding(top = 5.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                Image(
                    painter = painterResource(
                        when (name) {
                            "Red Horse 1000 ML (Mucho)" -> R.drawable.mucho
                            "Red Horse 500 ML" -> R.drawable.redhorse_500
                            "Red Horse 330 ML (Stallion)" -> R.drawable.stallion
                            "Pale Pilsen 1000 ML (Grande)" -> R.drawable.grande
                            "Pale Pilsen 320 ML" -> R.drawable.pilsen_small
                            "San Mig Light 330 ML" -> R.drawable.sanmig_light
                            "San Mig Apple 330 ML" -> R.drawable.sanmig_apple
                            "RC Original Small 240 ML" -> R.drawable.rc_small
                            "RC Orange Small 240 ML" -> R.drawable.orange_small
                            "RC Lemon Small 240 ML" -> R.drawable.lemon_small
                            "RC Root Beer Small 240 ML" -> R.drawable.rootbeer_small
                            "RC Mega Original 800 ML" -> R.drawable.rc_mega
                            "RC Mega Orange 800 ML" -> R.drawable.orange_mega
                            "RC Mega Lemon 800 ML" -> R.drawable.lemon_mega
                            "Cobra Original (Yellow) 240 ML" -> R.drawable.cobra_yellow
                            "Cobra Citrus (Green) 240 ML" -> R.drawable.cobra_green
                            else -> R.drawable.plus
                        }),
                    contentDescription = name,
                    modifier = Modifier
                        .size(80.dp)
                        .fillMaxWidth()
                        .clickable {
                            viewModel.addItem(name, type)
                            viewModel.showProductList = false
                        }
                )
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ){

                Text(
                    text = name,
                    fontFamily = font_notosans_bold,
                    fontSize = 7.sp
                )

                Text(
                    text = "Price: $price",
                    fontFamily = font_notosans_bold,
                    fontSize = 7.sp,
                    modifier = Modifier.padding(start = 5.dp)
                )
            }
        }
    }
}