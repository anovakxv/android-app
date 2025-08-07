package com.networkedcapital.rep

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.networkedcapital.rep.presentation.navigation.RepNavigation
import com.networkedcapital.rep.presentation.theme.RepTheme
import com.networkedcapital.rep.presentation.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RepTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = hiltViewModel()
                
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RepNavigation(
                        navController = navController,
                        authViewModel = authViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RepTheme {
        // Preview content will be added later
    }
}
