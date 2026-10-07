package com.atom.tecnohandapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.atom.tecnohandapp.ui.theme.TecnoHandAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TecnoHandAppTheme {
                ScanQrScreen()
            }
        }
    }
}
