package xyz.javidsattar.falcon.navigator.ui.joystick

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class JoystickState(
    val x: Float = 0f,
    val y: Float = 0f,
    val angle: Float = 0f,
    val strength: Float = 0f
)

class JoystickViewModel : ViewModel() {
    private val _leftJoystickState = MutableStateFlow(JoystickState())
    val leftJoystickState: StateFlow<JoystickState> = _leftJoystickState.asStateFlow()

    private val _rightJoystickState = MutableStateFlow(JoystickState())
    val rightJoystickState: StateFlow<JoystickState> = _rightJoystickState.asStateFlow()

    fun updateLeftJoystick(x: Float, y: Float, angle: Float, strength: Float) {
        _leftJoystickState.value = JoystickState(x, y, angle, strength)
    }

    fun updateRightJoystick(x: Float, y: Float, angle: Float, strength: Float) {
        _rightJoystickState.value = JoystickState(x, y, angle, strength)
    }
}
