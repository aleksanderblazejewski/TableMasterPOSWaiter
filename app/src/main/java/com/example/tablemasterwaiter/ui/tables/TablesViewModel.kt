package com.example.tablemasterwaiter.ui.tables

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tablemasterwaiter.api.ApiClient
import com.example.tablemasterwaiter.model.LoggedInUser
import com.example.tablemasterwaiter.model.SessionManager
import com.example.tablemasterwaiter.model.Table
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TablesViewModel : ViewModel() {

    private val _tables = MutableStateFlow<List<Table>>(emptyList())
    val tables: StateFlow<List<Table>> = _tables

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _currentUser = MutableStateFlow<LoggedInUser?>(SessionManager.currentUser)
    val currentUser: StateFlow<LoggedInUser?> = _currentUser

    fun loadTables() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val tablesResponse = ApiClient.apiService.getTables()
                val groupsResponse = ApiClient.apiService.getTableGroups()

                if (tablesResponse.isSuccessful && groupsResponse.isSuccessful) {
                    val allTables = tablesResponse.body().orEmpty()
                    val allGroups = groupsResponse.body().orEmpty()
                    val currentUserId = _currentUser.value?.id
                    val assignedTableIds = allGroups
                        .filter { currentUserId != null && it.AssignedWaiterIds.contains(currentUserId) }
                        .flatMap { it.AssignedTableIds }
                        .toSet()
                    _tables.value = allTables.filter { it.Id in assignedTableIds }
                } else {
                    _errorMessage.value = "Błąd ładowania danych"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Błąd sieci: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
