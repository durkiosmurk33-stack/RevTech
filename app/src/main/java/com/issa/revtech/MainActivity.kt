
package com.issa.revtech

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.issa.revtech.navigation.AppNavHost
import com.issa.revtech.ui.theme.RevTechTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            RevTechTheme {
                AppNavHost()
            }
        }
    }
}


