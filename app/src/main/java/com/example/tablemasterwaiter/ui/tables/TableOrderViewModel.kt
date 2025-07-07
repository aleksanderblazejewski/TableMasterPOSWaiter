package com.example.tablemasterwaiter.ui.tables

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tablemasterwaiter.api.ApiClient
import com.example.tablemasterwaiter.model.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TableOrderViewModel : ViewModel() {

    private val _order = MutableStateFlow<Order?>(null)
    val order: StateFlow<Order?> = _order

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadOrder(tableId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = ApiClient.apiService.getOrders()
                if (response.isSuccessful) {
                    val orders = response.body().orEmpty()
                    _order.value = orders.find { it.TableId == tableId }
                } else {
                    _errorMessage.value = "Błąd ładowania zamówienia"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Błąd sieci: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateItemServed(index: Int, isServed: Boolean) {
        val current = _order.value ?: return
        val updatedItems = current.Items.toMutableList()
        updatedItems[index] = updatedItems[index].copy(IsServed = isServed)
        val updatedOrder = current.copy(Items = updatedItems)
        _order.value = updatedOrder

        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.updateOrder(updatedOrder.Id, updatedOrder)
                if (!response.isSuccessful) {
                    _errorMessage.value = "Błąd aktualizacji zamówienia"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Błąd sieci: ${e.message}"
            }
        }
    }
}
