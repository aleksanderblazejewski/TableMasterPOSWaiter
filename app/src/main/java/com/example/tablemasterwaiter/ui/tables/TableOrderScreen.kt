package com.example.tablemasterwaiter.ui.tables

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableOrderScreen(
    tableId: Int,
    navController: NavController,
    viewModel: TableOrderViewModel = viewModel()
) {
    val context = LocalContext.current
    val order by viewModel.order.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(tableId) {
        viewModel.loadOrder(tableId)
    }

    errorMessage?.let {
        LaunchedEffect(it) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Zamówienie - Stolik $tableId") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Powrót")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                order == null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Brak zamówienia dla stolika $tableId")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            navController.navigate("addOrder/$tableId")
                        }) {
                            Text("Dodaj zamówienie")
                        }
                    }
                }

                else -> {
                    LazyColumn(modifier = Modifier.padding(16.dp)) {
                        itemsIndexed(order!!.Items) { index, item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = item.Name)
                                        Text(text = "${item.Price} zł")
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Zaserwowane")
                                        Checkbox(
                                            checked = item.IsServed,
                                            onCheckedChange = { checked ->
                                                viewModel.updateItemServed(index, checked)
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (order!!.IsPaid) "Zamówienie opłacone"
                                else "Zamówienie nieopłacone",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
