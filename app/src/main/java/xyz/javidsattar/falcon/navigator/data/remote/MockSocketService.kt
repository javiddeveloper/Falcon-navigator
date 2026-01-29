package xyz.javidsattar.falcon.navigator.data.remote

import android.graphics.Bitmap
import android.graphics.Color
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import kotlin.random.Random
import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.graphics.createBitmap

class MockSocketService @Inject constructor() : SocketService {

    private val width = 640
    private val height = 480
    private var isConnected = false

    override fun observeIncomingData(): Flow<ByteArray> = flow {
        while (true) {
            if (isConnected) {
                // Simulate reading a frame from a socket input stream
                val bytes = generateMockFrame()
                emit(bytes)
            }
            delay(33) // ~30 FPS for video stream
        }
    }

    override suspend fun connect(host: String, port: Int) {
        delay(500) // Simulate connection delay
        isConnected = true
    }

    override suspend fun disconnect() {
        isConnected = false
    }

    override suspend fun sendData(data: ByteArray) {
        // Simulate sending data over socket
        if (!isConnected) return
        // In a real socket, we would write to outputStream here
        // Log.d("MockSocket", "Sent: ${String(data)}")
    }

    private fun generateMockFrame(): ByteArray {
        val bitmap = createBitmap(width, height)
        val canvas = Canvas(bitmap)
        val paint = Paint()

        // Background
        canvas.drawColor(Color.DKGRAY)

        // Draw some moving shapes to simulate video
        paint.color = Color.rgb(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
        val x = Random.nextFloat() * width
        val y = Random.nextFloat() * height
        val radius = Random.nextFloat() * 100 + 20
        canvas.drawCircle(x, y, radius, paint)
        
        // Add text to show it's a socket stream
        paint.color = Color.WHITE
        paint.textSize = 40f
        canvas.drawText("Socket Stream Simulation", 50f, 50f, paint)

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream)
        return stream.toByteArray()
    }
}
