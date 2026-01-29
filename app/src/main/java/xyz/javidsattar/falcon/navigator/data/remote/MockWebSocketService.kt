package xyz.javidsattar.falcon.navigator.data.remote

import android.graphics.Bitmap
import android.graphics.Color
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import kotlin.random.Random

class MockWebSocketService @Inject constructor() : WebSocketService {

    private val width = 320
    private val height = 240
    private var isConnected = false

    override fun observeCameraData(): Flow<ByteArray> = flow {
        while (true) {
            if (isConnected) {
                // Simulate generating a frame (random colored noise)
                val bytes = generateMockFrame()
                emit(bytes)
            }
            delay(10) // 10ms delay as requested
        }
    }

    override suspend fun connect() {
        isConnected = true
    }

    override suspend fun disconnect() {
        isConnected = false
    }

    private fun generateMockFrame(): ByteArray {
        // Create a simple bitmap with changing colors to simulate video
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        
        // Fill with random color to verify updates
        val r = Random.nextInt(256)
        val g = Random.nextInt(256)
        val b = Random.nextInt(256)
        bitmap.eraseColor(Color.rgb(r, g, b))
        
        // Add some noise or patterns if needed
        // For now, solid flashing color is enough to prove the stream works
        
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
        return stream.toByteArray()
    }
}
