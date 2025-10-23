package com.example.inputemulator

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import androidx.navigation.navArgument
import com.example.inputemulator.ui.theme.InputEmulatorTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InputEmulatorTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(10.dp)
                ) { paddingValues ->
                    val navController = rememberNavController()
                    val navGraph = remember {
                        navController.createGraph("Home") {
                            composable("Home") {
                                Home(navController)
                            }
                            composable(
                                "TouchSetting?message={message}",
                                arguments = listOf(navArgument("message") {
                                    type = NavType.StringType
                                    defaultValue = ""
                                })
                            ) { entry ->
                                TouchSetting(navController, entry.arguments?.getString("message"))
                            }
                            composable("Touch") {
                                Touch(
                                    onError = { code, message ->
                                        navController.navigate("TouchSetting?message=$code") {
                                            popUpTo("TouchSetting") { inclusive = true }
                                        }
                                    }
                                )
                            }
                        }
                    }
                    NavHost(
                        navController = navController,
                        graph = navGraph,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}

@Composable
fun Home(navController: NavController) {
    Column {
        Button(
            onClick = { navController.navigate(route = "TouchSetting") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Touch")
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun TouchSetting(navController: NavController, message: String? = "") {
    var sensitive by remember {
        mutableFloatStateOf(1f)
    }
    var landscape by remember {
        mutableStateOf(false)
    }
    var resultMessage by remember {
        mutableStateOf(message ?: "")
    }
    val context = LocalContext.current

    Column {
//        FormSlider(
//            name = "sensitiv",
//            value = sensitive,
//            onValueChange = { sensitive = it },
//            valueRange = 0f..10f
//        )
//        FormSwitch(name = "landscape", value = landscape, onValueChange = { landscape = it })
        Button(onClick = {
            ActivityCompat.requestPermissions(
                context.findAndroidActivity()!!,
                arrayOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_ADVERTISE
                ), 0
            )
            navController.navigate(route = "Touch")
        }) { Text("start") }
        Text(resultMessage)
    }
}
