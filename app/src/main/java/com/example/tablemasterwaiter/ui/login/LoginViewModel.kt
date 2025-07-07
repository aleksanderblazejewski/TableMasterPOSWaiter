package com.example.tablemasterwaiter.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tablemasterwaiter.api.ApiClient
import com.example.tablemasterwaiter.model.LoggedInUser
import com.example.tablemasterwaiter.model.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest

sealed class LoginResult {
    object Success : LoginResult()
    data class Error(val message: String) : LoginResult()
    object Loading : LoginResult()
    object Empty : LoginResult()
}

class LoginViewModel : ViewModel() {

    private val _loginState = MutableStateFlow<LoginResult>(LoginResult.Empty)
    val loginState: StateFlow<LoginResult> = _loginState

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginResult.Loading

            try {
                val response = ApiClient.apiService.getWaiters()
                if (response.isSuccessful) {
                    val waiters = response.body().orEmpty()
                    val hashed = hashPassword(password)

                    val matched = waiters.find {
                        it.Login == username && it.Password == hashed
                    }

                    if (matched != null) {
                        SessionManager.currentUser = LoggedInUser(
                            id = matched.Id,
                            firstName = matched.FirstName,
                            lastName = matched.LastName
                        )
                        _loginState.value = LoginResult.Success
                    } else {
                        _loginState.value = LoginResult.Error("Niepoprawny login lub hasło")
                    }
                } else {
                    _loginState.value = LoginResult.Error("Błąd pobierania danych")
                }
            } catch (e: Exception) {
                _loginState.value = LoginResult.Error("Błąd połączenia z serwerem")
            }
        }
    }

    private fun hashPassword(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}
