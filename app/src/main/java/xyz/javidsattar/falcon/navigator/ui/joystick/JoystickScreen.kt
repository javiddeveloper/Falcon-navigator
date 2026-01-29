package xyz.javidsattar.falcon.navigator.ui.joystick

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

@Composable
fun JoystickScreen(
    modifier: Modifier = Modifier,
    viewModel: JoystickViewModel = viewModel()
) {
    // Debug Info
    val leftState by viewModel.leftJoystickState.collectAsState()
    val rightState by viewModel.rightJoystickState.collectAsState()
    var hasCameraPermission by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        launcher.launch(Manifest.permission.CAMERA)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (hasCameraPermission) {
            CameraPreview()
        } else {
            Text(
                text = "Camera permission required",
                modifier = Modifier.align(Alignment.Center),
                color = Color.White
            )
        }

        // Left Joystick
        Joystick(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(32.dp)
                .size(150.dp),
            onMoved = { x, y ->
                // Calculate Angle and Strength
                val angle = Math.toDegrees(atan2(y.toDouble(), x.toDouble())).toFloat()
                val strength = hypot(x, y).coerceAtMost(1f)
                
                viewModel.updateLeftJoystick(
                    x = x,
                    y = y,
                    angle = angle,
                    strength = strength
                )
            }
        )

        // Right Joystick
        Joystick(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(32.dp)
                .size(150.dp),
            onMoved = { x, y ->
                // Calculate Angle and Strength
                val angle = Math.toDegrees(atan2(y.toDouble(), x.toDouble())).toFloat()
                val strength = hypot(x, y).coerceAtMost(1f)
                
                viewModel.updateRightJoystick(
                    x = x,
                    y = y,
                    angle = angle,
                    strength = strength
                )
            }
        )

        
        Text(
            text = "L: X:%.2f Y:%.2f | R: X:%.2f Y:%.2f".format(
                leftState.x, leftState.y,
                rightState.x, rightState.y
            ),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(32.dp),
            color = MaterialTheme.colorScheme.primary // Use Primary Yellow
        )
    }
}

@Composable
fun Joystick(
    modifier: Modifier = Modifier,
    onMoved: (x: Float, y: Float) -> Unit
) {
    var knobPosition by remember { mutableStateOf(Offset.Zero) }
    val knobRadius = with(LocalDensity.current) { 30.dp.toPx() } // Increased size for better grip

    Box(
        modifier = modifier
    ) {
        val primaryColor = MaterialTheme.colorScheme.primary
        val surfaceColor = MaterialTheme.colorScheme.surface
        
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw Background
            drawCircle(
                color = surfaceColor.copy(alpha = 0.5f), 
                radius = size.minDimension / 2
            )
            // Draw Border
            drawCircle(
                color = primaryColor, 
                radius = size.minDimension / 2,
                style = Stroke(width = 2.dp.toPx())
            )
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            knobPosition = Offset.Zero
                            onMoved(0f, 0f)
                        },
                        onDragCancel = {
                            knobPosition = Offset.Zero
                            onMoved(0f, 0f)
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        
                        val newPos = knobPosition + dragAmount
                        val distance = hypot(newPos.x, newPos.y)
                        val radius = size.width / 2 - knobRadius
                        
                        knobPosition = if (distance > radius) {
                            val angle = atan2(newPos.y, newPos.x)
                            Offset(
                                (cos(angle) * radius),
                                (sin(angle) * radius)
                            )
                        } else {
                            newPos
                        }

                        // Normalize output to -1..1
                        val normalizedX = knobPosition.x / radius
                        val normalizedY = -knobPosition.y / radius 
                        onMoved(normalizedX, normalizedY)
                    }
                }
        ) {
            val centerPoint = center + knobPosition
            
            // Draw Shadow
            drawCircle(
                color = Color.Black.copy(alpha = 0.4f),
                radius = knobRadius,
                center = centerPoint + Offset(5f, 5f)
            )

            // Draw Main Knob Body (PlayStation Style Dark Grey/Black Gradient)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF444444), // Dark Grey
                        Color(0xFF111111)  // Almost Black
                    ),
                    center = centerPoint - Offset(10f, 10f), // Light source offset
                    radius = knobRadius
                ),
                radius = knobRadius,
                center = centerPoint
            )

            // Draw Grip Ring (Inner Circle Detail)
            drawCircle(
                color = Color(0xFF222222),
                radius = knobRadius * 0.7f,
                center = centerPoint,
                style = Stroke(width = 2.dp.toPx())
            )

            // Draw Accent/Concave Highlight (Top edge)
            drawCircle(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.1f),
                        Color.Transparent
                    ),
                    startY = centerPoint.y - knobRadius,
                    endY = centerPoint.y
                ),
                radius = knobRadius,
                center = centerPoint
            )
            
            // Draw Outer Ring Accent (Theme Color)
            drawCircle(
                color = primaryColor.copy(alpha = 0.8f),
                radius = knobRadius,
                center = centerPoint,
                style = Stroke(width = 1.dp.toPx())
            )
        }
    }
}
