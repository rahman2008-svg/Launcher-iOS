package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.LauncherApp
import com.example.ui.LauncherViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    
    // Bind Viewmodel with Factory injection
    val factory = LauncherViewModel.Factory(application)
    
    setContent {
      MyApplicationTheme {
        val viewModel: LauncherViewModel = viewModel(factory = factory)
        Surface(modifier = Modifier.fillMaxSize()) {
          LauncherApp(viewModel = viewModel)
        }
      }
    }
  }
}
