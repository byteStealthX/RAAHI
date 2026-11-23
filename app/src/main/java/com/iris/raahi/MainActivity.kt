package com.iris.raahi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.iris.raahi.navigation.AppNavigation
import com.iris.raahi.theme.RAAHITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RAAHITheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(application = application as RAAHIApplication)
                }
            }
        }
    }
}
