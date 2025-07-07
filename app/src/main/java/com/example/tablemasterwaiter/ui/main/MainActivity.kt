package com.example.tablemasterwaiter.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.tablemasterwaiter.ui.MainComposeHost
import com.example.tablemasterwaiter.ui.theme.TableMasterWaiterTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TableMasterWaiterTheme {
                MainComposeHost()
            }
        }
    }
}