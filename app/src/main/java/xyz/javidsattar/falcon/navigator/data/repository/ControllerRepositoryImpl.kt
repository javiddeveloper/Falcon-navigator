package xyz.javidsattar.falcon.navigator.data.repository

import xyz.javidsattar.falcon.navigator.data.remote.SocketService
import xyz.javidsattar.falcon.navigator.data.repository.model.JoystickState
import javax.inject.Inject

class ControllerRepositoryImpl @Inject constructor(
    private val socketService: SocketService
) : ControllerRepository {

    override suspend fun sendControlData(left: JoystickState, right: JoystickState) {
        // Format the data as a string or byte array
        // Format: "L:x,y,angle,strength|R:x,y,angle,strength"
        val message = "L:${format(left)}|R:${format(right)}\n"
        socketService.sendData(message.toByteArray())
    }

    private fun format(state: JoystickState): String {
        return "%.2f,%.2f,%.2f,%.2f".format(state.x, state.y, state.angle, state.strength)
    }
}
