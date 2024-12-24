package com.myjar.jarassignment.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.myjar.jarassignment.R
import com.myjar.jarassignment.data.model.ComputerItem
import com.myjar.jarassignment.data.model.ItemData
import com.myjar.jarassignment.ui.vm.JarViewModel

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    viewModel: JarViewModel,
) {
    val navController = rememberNavController()
    val navigate = remember { mutableStateOf("") }

    NavHost(modifier = modifier, navController = navController, startDestination = "item_list") {
        composable("item_list") {
            ItemListScreen(
                viewModel = viewModel,
                onNavigateToDetail = { selectedItem -> navigate.value = selectedItem },
                navigate = navigate,
                navController = navController
            )
        }
        composable("item_detail/{itemId}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")
            ItemDetailScreen(itemId = itemId)
        }
    }
}

@Composable
fun ItemListScreen(
    viewModel: JarViewModel,
    onNavigateToDetail: (String) -> Unit,
    navigate: MutableState<String>,
    navController: NavHostController
) {
    val items = viewModel.filteredList.collectAsState()
    val searchTerm = viewModel.searchTerm.collectAsState()
    val isLoading = viewModel.isSearching.collectAsState()

    LaunchedEffect(navigate.value) {
        if (navigate.value.isNotBlank()) {
            val currRoute = navController.currentDestination?.route.orEmpty()
            if (!currRoute.contains("item_detail")) {
                navController.navigate("item_detail/${navigate.value}")
                navigate.value = ""
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            value = searchTerm.value,
            onValueChange = viewModel::updateSearchTerm
        )
        if (isLoading.value) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                items(items.value) { item ->
                    ItemCard(
                        item = item,
                        onClick = { onNavigateToDetail(item.id) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun ItemCard(item: ComputerItem, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Text(text = item.name, fontWeight = FontWeight.Bold, color = Color.Black)
        item.data?.let { itemData: ItemData ->
            itemData.color?.let { color ->
                Text(text = stringResource(R.string.color, color), fontWeight = FontWeight.Normal, color = Color.Black)
            }
            itemData.capacity?.let { capacity->
                Text(text = stringResource(R.string.capacity, capacity), fontWeight = FontWeight.Normal, color = Color.Black)
            }
            itemData.capacityGB?.let { capacityGB ->
                Text(text = stringResource(R.string.capacitygb, capacityGB), fontWeight = FontWeight.Normal, color = Color.Black)
            }
            itemData.price?.let { price->
                Text(text = stringResource(R.string.price, price), fontWeight = FontWeight.Normal, color = Color.Black)
            }
            itemData.screenSize?.let { screenSize ->
                Text(text = stringResource(R.string.screensize, screenSize), fontWeight = FontWeight.Normal, color = Color.Black)
            }
            itemData.description?.let { description->
                Text(text = stringResource(R.string.description, description), fontWeight = FontWeight.Normal, color = Color.Black)
            }
            itemData.generation?.let { generation->
                Text(text = stringResource(R.string.generation, generation), fontWeight = FontWeight.Normal, color = Color.Black)
            }
            itemData.cpuModel?.let { cpuModel->
                Text(text = stringResource(R.string.cpumodel, cpuModel), fontWeight = FontWeight.Normal, color = Color.Black)
            }
            itemData.hardDiskSize?.let {hardDiskSize->
                Text(text = stringResource(R.string.harddisksize, hardDiskSize), fontWeight = FontWeight.Normal, color = Color.Black)
            }
        }
    }
}

@Composable
fun ItemDetailScreen(itemId: String?) {
    // Fetch the item details based on the itemId
    // Here, you can fetch it from the ViewModel or repository
    Text(
        text = stringResource(R.string.item_details_for_id, itemId.toString()),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    )
}
