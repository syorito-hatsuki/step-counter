package dev.syoritohatsuki.stepcounter.ui.screens.home.viewmodel

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dev.syoritohatsuki.stepcounter.dto.Point
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel(), SensorEventListener {

    companion object {
        private const val ABOVE = 1
        private const val BELOW = 0
    }

    private val _prev = mutableStateOf(floatArrayOf(0f, 0f, 0f))

    private val _currentState = mutableIntStateOf(BELOW)
    private val _previousState = mutableIntStateOf(BELOW)

    private val _streakStartTime = mutableLongStateOf(0)
    private val _streakPrevTime = mutableLongStateOf(0)

    private val _stepCount: MutableStateFlow<Int> = MutableStateFlow(0)
    val stepCount: StateFlow<Int> = _stepCount.asStateFlow()

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> handleAccelerometerEvent(event)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, p1: Int) {
        // Unused for current task
    }

    private fun handleAccelerometerEvent(event: SensorEvent) {
        _prev.value = lowPassFilter(event.values, _prev.value) ?: return

        val point = Point(
            x = _prev.value[0],
            y = _prev.value[1],
            z = _prev.value[2]
        )

        when {
            point.magnitude > 10.5f -> {
                _currentState.intValue = ABOVE

                if (_previousState.intValue == _currentState.intValue) return

                _streakStartTime.longValue = System.currentTimeMillis()

                if ((_streakStartTime.longValue - _streakPrevTime.longValue) <= 250f) {
                    _streakPrevTime.longValue = System.currentTimeMillis()
                    return
                }

                _streakPrevTime.longValue = _streakStartTime.longValue
                _previousState.intValue = _currentState.intValue

                _stepCount.value++
            }
            point.magnitude < 10.5f -> {
                _currentState.intValue = BELOW
                _previousState.intValue = _currentState.intValue
            }
        }
    }

    private fun lowPassFilter(input: FloatArray?, prev: FloatArray): FloatArray? {
        input?.indices?.forEach { i ->
            prev[i] = prev[i] + 0.1f * (input[i] - prev[i])
        } ?: return null
        return prev
    }
}