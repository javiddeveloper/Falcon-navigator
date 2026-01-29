package xyz.javidsattar.falcon.navigator.data.repository

import xyz.javidsattar.falcon.navigator.data.remote.WebSocketService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CameraRepositoryImpl @Inject constructor(
    private val webSocketService: WebSocketService
) : CameraRepository {

    override fun getCameraStream(): Flow<ByteArray> {
        return webSocketService.observeCameraData()
    }

    override suspend fun startStream() {
        webSocketService.connect()
    }

    override suspend fun stopStream() {
        webSocketService.disconnect()
    }
}
