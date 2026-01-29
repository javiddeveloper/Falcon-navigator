package xyz.javidsattar.falcon.navigator.ui.joystick

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import xyz.javidsattar.falcon.navigator.data.repository.CameraRepository
import javax.inject.Inject

data class JoystickState(
    val x: Float = 0f,
    val y: Float = 0f,
    val angle: Float = 0f,
    val strength: Float = 0f
)

@HiltViewModel
class JoystickViewModel @Inject constructor(
    private val cameraRepository: CameraRepository
) : ViewModel() {
    private val _leftJoystickState = MutableStateFlow(JoystickState())
    val leftJoystickState: StateFlow<JoystickState> = _leftJoystickState.asStateFlow()

    private val _rightJoystickState = MutableStateFlow(JoystickState())
    val rightJoystickState: StateFlow<JoystickState> = _rightJoystickState.asStateFlow()

    private val _cameraFrame = MutableStateFlow<Bitmap?>(null)
    val cameraFrame: StateFlow<Bitmap?> = _cameraFrame.asStateFlow()

    private val _isCameraActive = MutableStateFlow(false)
    val isCameraActive: StateFlow<Boolean> = _isCameraActive.asStateFlow()

    init {
        // Initially camera is inactive as per request "switch controls socket"
        // But to keep previous behavior we can set it to false and let user turn it on.
        // User said: "when turned off socket closed, when turned on socket open"
    }

    fun toggleCamera(isActive: Boolean) {
        _isCameraActive.value = isActive
        if (isActive) {
            startCameraStream()
        } else {
            stopCameraStream()
        }
    }

    private fun startCameraStream() {
        viewModelScope.launch {
            cameraRepository.startStream()
            cameraRepository.getCameraStream().collect { bytes ->
                // Convert byte array to Bitmap
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                _cameraFrame.value = bitmap
            }
        }
    }

    private fun stopCameraStream() {
        viewModelScope.launch {
            cameraRepository.stopStream()
            _cameraFrame.value = null
        }
    }

    fun updateLeftJoystick(x: Float, y: Float, angle: Float, strength: Float) {
        _leftJoystickState.value = JoystickState(x, y, angle, strength)
    }

    fun updateRightJoystick(x: Float, y: Float, angle: Float, strength: Float) {
        _rightJoystickState.value = JoystickState(x, y, angle, strength)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            cameraRepository.stopStream()
        }
    }
}
