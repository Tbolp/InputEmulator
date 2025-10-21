package com.example.inputemulator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import com.example.inputemulator.ui.theme.InputEmulatorTheme
import java.time.temporal.ValueRange

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
                                HomePage(navController)
                            }
                            composable("TouchSettingPage") { TouchSettingPage() }
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
    var sensitive by remember {
        mutableFloatStateOf(1f)
    }
    var landscape by remember {
        mutableStateOf(false)
    }
    Column {
        FormSlider(
            name = "sensitiv",
            value = sensitive,
            onValueChange = { sensitive = it },
            valueRange = 0f..10f
        )
        FormSwitch(name = "landscape", value = landscape, onValueChange = { landscape = it })
        Button(onClick = {}) { Text("start") }
    }
}

@Composable
fun FormSlider(
    name: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(name)
        Spacer(modifier = Modifier.width(10.dp))
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
        )
    }
}

@Composable
fun FormSwitch(
    name: String,
    value: Boolean,
    onValueChange: ((Boolean) -> Unit)?,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(name)
        Spacer(modifier = Modifier.weight(1f))
        Switch(checked = value, onCheckedChange = onValueChange)
    }
}

