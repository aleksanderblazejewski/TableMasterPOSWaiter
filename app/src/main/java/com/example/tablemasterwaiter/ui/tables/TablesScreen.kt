package com.example.tablemasterwaiter.ui.tables

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tablemasterwaiter.model.SessionManager
import com.example.tablemasterwaiter.model.Table

@Composable
fun TablesScreen(
    navController: NavController,
    viewModel: TablesViewModel = viewModel()
) {
    val context = LocalContext.current
    val tables by viewModel.tables.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val currentUser = SessionManager.currentUser

    LaunchedEffect(Unit) {
        viewModel.loadTables()
    }

    errorMessage?.let { message ->
        LaunchedEffect(message) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            val currentUser by viewModel.currentUser.collectAsState()

            Text(
                text = "Zalogowany: ${
                    currentUser?.let { "${it.firstName} ${it.lastName}" } ?: "brak danych"
                }",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn {
                items(tables, key = { it.Id }) { table ->
                    TableCard(table = table, navController = navController)
                }
            }
        }
    }
}

@Composable
fun TableCard(table: Table, navController: NavController) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                navController.navigate("tableOrder/${table.Id}")
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Stolik ${table.Id}",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
