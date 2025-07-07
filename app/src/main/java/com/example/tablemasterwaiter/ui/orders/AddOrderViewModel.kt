package com.example.tablemasterwaiter.ui.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tablemasterwaiter.api.ApiClient
import MenuItem
import com.example.tablemasterwaiter.model.Order
import com.example.tablemasterwaiter.model.OrderItem
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AddOrderViewModel : ViewModel() {

    private val _menuItems = MutableStateFlow<List<MenuItem>>(emptyList())
    val menuItems: StateFlow<List<MenuItem>> = _menuItems

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _selectedItems = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val selectedItems: StateFlow<Map<Int, Int>> = _selectedItems

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadMenu() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = ApiClient.apiService.getMenu()
                if (response.isSuccessful) {
                    _menuItems.value = response.body().orEmpty()
                } else {
                    _errorMessage.value = "Błąd pobierania menu"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Błąd sieci: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun increment(itemId: Int) {
        val map = _selectedItems.value.toMutableMap()
        map[itemId] = (map[itemId] ?: 0) + 1
        _selectedItems.value = map
    }

    fun decrement(itemId: Int) {
        val map = _selectedItems.value.toMutableMap()
        val current = map[itemId] ?: 0
        if (current > 0) {
            map[itemId] = current - 1
            _selectedItems.value = map
        }
    }

    fun buildOrder(tableId: Int): Order? {
        val selected = _selectedItems.value
        if (selected.isEmpty()) return null

        val items = _menuItems.value
            .filter { selected[it.Id] ?: 0 > 0 }
            .flatMap { item ->
                List(selected[item.Id] ?: 0) {
                    OrderItem(Name = item.Name, Price = item.Price, IsServed = false)
                }
            }

        return if (items.isNotEmpty()) {
            Order(TableId = tableId, Items = items, IsPaid = false)
        } else null
    }

    suspend fun postOrder(order: Order): Boolean {
        return try {
            val response = ApiClient.apiService.postOrder(order)
            response.isSuccessful
        } catch (e: Exception) {
            _errorMessage.value = "Błąd sieci: ${e.message}"
            false
        }
    }
}
