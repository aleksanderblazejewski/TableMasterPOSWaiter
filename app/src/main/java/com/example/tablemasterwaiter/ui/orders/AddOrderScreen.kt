package com.example.tablemasterwaiter.ui.orders

import MenuItem
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tablemasterwaiter.api.ApiClient
import com.example.tablemasterwaiter.model.Order
import com.example.tablemasterwaiter.model.OrderItem
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Remove

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrderScreen(
    tableId: Int,
    navController: NavController,
    viewModel: AddOrderViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val menuItems by viewModel.menuItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedItems by viewModel.selectedItems.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadMenu()
    }

    errorMessage?.let {
        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val grouped = menuItems.groupBy { it.Category }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dodaj zamówienie") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Wstecz")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    val order = viewModel.buildOrder(tableId)
                    if (order == null) {
                        Toast.makeText(context, "Nie wybrano żadnych pozycji", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    scope.launch {
                        val success = viewModel.postOrder(order)
                        if (success) {
                            navController.popBackStack()
                        } else {
                            Toast.makeText(context, "Błąd zapisu zamówienia", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Zapisz zamówienie")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            grouped.forEach { (category, categoryItems) ->
                item {
                    var expanded by remember { mutableStateOf(true) }

                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expanded = !expanded }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(category, style = MaterialTheme.typography.titleMedium)
                            Icon(
                                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null
                            )
                        }

                        if (expanded) {
                            categoryItems.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp, horizontal = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(item.Name)
                                        Text("${item.Price} zł", style = MaterialTheme.typography.bodySmall)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(onClick = { viewModel.decrement(item.Id) }) {
                                            Icon(Icons.Default.Remove, contentDescription = "Usuń")
                                        }
                                        Text("${selectedItems[item.Id] ?: 0}")
                                        IconButton(onClick = { viewModel.increment(item.Id) }) {
                                            Icon(Icons.Default.Add, contentDescription = "Dodaj")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
