package xyz.javidsattar.falcon.navigator.data.remote

import kotlinx.coroutines.flow.Flow

interface WebSocketService {
    fun observeCameraData(): Flow<ByteArray>
    suspend fun connect()
    suspend fun disconnect()
}
