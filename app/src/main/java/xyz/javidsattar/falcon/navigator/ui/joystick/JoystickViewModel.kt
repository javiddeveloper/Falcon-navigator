package xyz.javidsattar.falcon.navigator.ui.joystick

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import xyz.javidsattar.falcon.navigator.data.repository.CameraRepository
import xyz.javidsattar.falcon.navigator.data.repository.ControllerRepository
import xyz.javidsattar.falcon.navigator.data.repository.model.JoystickState
import javax.inject.Inject

@HiltViewModel
class JoystickViewModel @Inject constructor(
    private val cameraRepository: CameraRepository,
    private val controllerRepository: ControllerRepository
) : ViewModel() {
    private val _leftJoystickState = MutableStateFlow(JoystickState())
    val leftJoystickState: StateFlow<JoystickState> = _leftJoystickState.asStateFlow()

    private val _rightJoystickState = MutableStateFlow(JoystickState())
    val rightJoystickState: StateFlow<JoystickState> = _rightJoystickState.asStateFlow()

    private val _cameraFrame = MutableStateFlow<Bitmap?>(null)
    val cameraFrame: StateFlow<Bitmap?> = _cameraFrame.asStateFlow()

    private val _isCameraActive = MutableStateFlow(false)
    val isCameraActive: StateFlow<Boolean> = _isCameraActive.asStateFlow()

    private var controlJob: Job? = null

    init {
        startSendingControlData()
    }

    private fun startSendingControlData() {
        controlJob?.cancel()
        controlJob = viewModelScope.launch {
            while (isActive) {
                controllerRepository.sendControlData(
                    _leftJoystickState.value,
                    _rightJoystickState.value
                )
                delay(10) // Repeat every 10ms
            }
        }
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
                _cameraFrame.update { bitmap }
            }
        }
    }

    private fun stopCameraStream() {
        viewModelScope.launch {
            cameraRepository.stopStream()
            _cameraFrame.update { null }
        }
    }

    fun updateLeftJoystick(x: Float, y: Float, angle: Float, strength: Float) {
        _leftJoystickState.update { JoystickState(x, y, angle, strength) }
    }

    fun updateRightJoystick(x: Float, y: Float, angle: Float, strength: Float) {
        _rightJoystickState.update { JoystickState(x, y, angle, strength) }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            cameraRepository.stopStream()
        }
        controlJob?.cancel()
    }
}
