package com.example.inputemulator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import com.example.inputemulator.ui.theme.InputEmulatorTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InputEmulatorTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize().padding(10.dp)) { paddingValues ->
                    val navController = rememberNavController()
                    val navGraph = remember {
                        navController.createGraph("Home") {
                            composable("Home") {
                                HomePage(navController)
                            }
                            composable("TouchSettingPage") { TouchSettingPage() }
                        }
                    }
                    NavHost(navController = navController, graph = navGraph, modifier = Modifier.padding(paddingValues))
                }
            }
        }
    }
}

@Composable
fun HomePage(navController: NavController) {
    Column {
        Button(
            onClick = { navController.navigate(route = "TouchSettingPage") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Touch")
        }
    }
}

@Composable
fun TouchSettingPage() {
    Text("second")
}