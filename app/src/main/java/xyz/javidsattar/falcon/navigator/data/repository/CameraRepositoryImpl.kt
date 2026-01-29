package xyz.javidsattar.falcon.navigator.data.repository

import xyz.javidsattar.falcon.navigator.data.remote.SocketService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CameraRepositoryImpl @Inject constructor(
    private val socketService: SocketService
) : CameraRepository {

    override fun getCameraStream(): Flow<ByteArray> {
        return socketService.observeIncomingData()
    }

    override suspend fun startStream() {
        // Hardcoded IP/Port for now, or could be injected/configured
        socketService.connect("192.168.1.1", 8080)
    }

    override suspend fun stopStream() {
        socketService.disconnect()
    }
}
