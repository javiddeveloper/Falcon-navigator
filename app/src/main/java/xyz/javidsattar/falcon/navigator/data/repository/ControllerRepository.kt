package xyz.javidsattar.falcon.navigator.data.repository

import xyz.javidsattar.falcon.navigator.data.repository.model.JoystickState


interface ControllerRepository {
    suspend fun sendControlData(left: JoystickState, right: JoystickState)
}
