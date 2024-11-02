package dev.syoritohatsuki.stepcounter.ui.screens.home

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.syoritohatsuki.stepcounter.ui.screens.home.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel()
) {

    val context = LocalContext.current
    val stepsGoal = 50

    val steps by homeViewModel.stepCount.collectAsState()

    LaunchedEffect(Unit) {
        (context.getSystemService(Context.SENSOR_SERVICE) as SensorManager).apply {
            registerListener(
                homeViewModel,
                getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    Text("Step count: ${steps}/$stepsGoal")
    if (steps >= stepsGoal) Text("Goal arrived! Good job!")
}