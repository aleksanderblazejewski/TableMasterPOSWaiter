package com.example.tablemasterwaiter.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.tablemasterwaiter.R
import com.example.tablemasterwaiter.ui.main.MainActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val usernameInput = findViewById<EditText>(R.id.username_input)
        val passwordInput = findViewById<EditText>(R.id.password_input)
        val loginButton = findViewById<Button>(R.id.login_button)

        loginButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                // Tryb testowy bez API
                if (username == "login" && password == "password") {
                    goToMainActivity()
                } else {
                    loginViewModel.login(username, password)
                }
            } else {
                Toast.makeText(this, "Uzupełnij wszystkie pola", Toast.LENGTH_SHORT).show()
            }
        }

        observeLoginState()
    }

    private fun observeLoginState() {
        lifecycleScope.launch {
            loginViewModel.loginState.collectLatest { result ->
                when (result) {
                    is LoginResult.Success -> goToMainActivity()
                    is LoginResult.Error -> {
                        Toast.makeText(this@LoginActivity, result.message, Toast.LENGTH_SHORT).show()
                    }
                    is LoginResult.Loading -> {
                        // Możesz dodać spinner albo zablokować UI
                    }
                    LoginResult.Empty -> Unit
                }
            }
        }
    }

    private fun goToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
