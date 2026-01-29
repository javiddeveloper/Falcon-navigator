package xyz.javidsattar.falcon.navigator.data.repository

import kotlinx.coroutines.flow.Flow

interface CameraRepository {
    fun getCameraStream(): Flow<ByteArray>
    suspend fun startStream()
    suspend fun stopStream()
}
