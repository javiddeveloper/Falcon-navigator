package xyz.javidsattar.falcon.navigator.data.remote

import kotlinx.coroutines.flow.Flow

interface SocketService {
    fun observeIncomingData(): Flow<ByteArray>
    suspend fun connect(host: String, port: Int)
    suspend fun disconnect()
    suspend fun sendData(data: ByteArray)
}
