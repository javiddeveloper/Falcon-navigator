package xyz.javidsattar.falcon.navigator.ui.joystick

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import xyz.javidsattar.falcon.navigator.R
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

@Composable
fun JoystickScreen(
    modifier: Modifier = Modifier,
    viewModel: JoystickViewModel = viewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    val leftState by viewModel.leftJoystickState.collectAsState()
    val rightState by viewModel.rightJoystickState.collectAsState()
    val cameraFrame by viewModel.cameraFrame.collectAsState()
    val isCameraActive by viewModel.isCameraActive.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.falcon_icon),
                        contentDescription = "App Icon",
                        modifier = Modifier.size(64.dp),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                
                HorizontalDivider()
                
                // Active Camera Switch
                NavigationDrawerItem(
                    label = { Text("Active Camera") },
                    selected = false,
                    onClick = { /* No-op, handled by switch */ },
                    badge = {
                        Switch(
                            checked = isCameraActive,
                            onCheckedChange = { isActive ->
                                viewModel.toggleCamera(isActive)
                            }
                        )
                    }
                )
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Camera Stream or Placeholder
            if (cameraFrame != null && isCameraActive) {
                cameraFrame?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Camera Stream",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            } else {
                // Placeholder Center Content
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Replaced mipmap with vector icon to avoid XML parsing error
                    Image(
                        painter = painterResource(R.drawable.falcon_icon),
                        contentDescription = "App Icon",
                        modifier = Modifier.size(100.dp),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                }
            }
            
            // Settings Button (Top Left)
            IconButton(
                onClick = {
                    scope.launch {
                        drawerState.open()
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Left Joystick Container
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 60.dp, bottom = 32.dp, end = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier= Modifier.padding(12.dp),
                    text = "Altitude / Rotate",
                    color = MaterialTheme.colorScheme.primary,
                )
                Joystick(
                    modifier = Modifier.size(150.dp),
                    onMoved = { x, y ->
                        val angle = Math.toDegrees(atan2(y.toDouble(), x.toDouble())).toFloat()
                        val strength = hypot(x, y).coerceAtMost(1f)
                        viewModel.updateLeftJoystick(x, y, angle, strength)
                    }
                )
            }

            // Right Joystick Container
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(start = 32.dp, bottom = 32.dp, end = 60.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier= Modifier.padding(12.dp),
                    text = "Movement",
                    color = MaterialTheme.colorScheme.primary,
                )
                Joystick(
                    modifier = Modifier.size(150.dp),
                    onMoved = { x, y ->
                        val angle = Math.toDegrees(atan2(y.toDouble(), x.toDouble())).toFloat()
                        val strength = hypot(x, y).coerceAtMost(1f)
                        viewModel.updateRightJoystick(x, y, angle, strength)
                    }
                )
            }

            // Debug Info (Optional, keeping it as it's useful)
            Text(
                text = "L: X:%.2f Y:%.2f | R: X:%.2f Y:%.2f".format(
                    leftState.x, leftState.y,
                    rightState.x, rightState.y
                ),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
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
