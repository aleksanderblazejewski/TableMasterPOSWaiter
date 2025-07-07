package com.example.tablemasterwaiter.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.tablemasterwaiter.ui.orders.AddOrderScreen
import com.example.tablemasterwaiter.ui.tables.TablesScreen
import com.example.tablemasterwaiter.ui.tables.TableOrderScreen

@Composable
fun MainComposeHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "tables",
        modifier = modifier
    ) {
        composable("tables") {
            TablesScreen(navController = navController)
        }

        composable(
            route = "tableOrder/{tableId}",
            arguments = listOf(navArgument("tableId") { type = NavType.IntType })
        ) { backStackEntry ->
            val tableId = backStackEntry.arguments?.getInt("tableId") ?: -1
            TableOrderScreen(tableId = tableId, navController = navController)
        }

        composable(
            "addOrder/{tableId}",
            arguments = listOf(navArgument("tableId") { type = NavType.IntType })
        ) { backStackEntry ->
            val tableId = backStackEntry.arguments?.getInt("tableId") ?: -1
            AddOrderScreen(tableId = tableId, navController = navController)
        }
    }
}
