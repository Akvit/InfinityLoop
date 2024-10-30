package com.azhel.infinityloop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.azhel.infinityloop.ui.theme.InfinityLoopTheme
import com.azhel.infinityloop.ui.theme.axisWidth
import com.azhel.infinityloop.ui.theme.loopWidth
import com.azhel.infinityloop.ui.theme.rainbowList
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InfinityLoopTheme { InfinityLoopScreen() }
        }
    }
}

@Composable
fun InfinityLoopScreen() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        InfinityLoop(modifier = Modifier.weight(1f))
    }
}

@Composable
fun InfinityLoop(modifier: Modifier = Modifier) {

    val lineState = remember { mutableStateListOf(
        Line(0f, 0f, 0f,0f, Color.White)
    ) }

    LaunchedEffect(Unit) {

        var rotate = 0
        while (rotate < 1800) {

            val rotateAngle = rotate.toRad()

            var step = 0
            while (step < 3600) {

                val stepAngle = step.toRad()

                val x1 = getX(stepAngle = stepAngle, rotateAngle = rotateAngle).toFloat()
                val y1 = getY(stepAngle = stepAngle, rotateAngle = rotateAngle).toFloat()

                step += 10

                val stepAngle2 = step.toRad()

                val x2 = getX(stepAngle = stepAngle2, rotateAngle = rotateAngle).toFloat()
                val y2 = getY(stepAngle = stepAngle2, rotateAngle = rotateAngle).toFloat()

                val currentColorId = step * rainbowList.size / 3601
                val newLine = Line(
                    x1 = x1, y1 = y1, x2 = x2, y2 = y2,
                    color = rainbowList[currentColorId]
                )

                lineState.add(newLine)
                delay(1)
            }
            rotate += 1500
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {

        val (width, height) = size.width to size.height
        val (centerX, centerY) = width / 2 to height / 2
        val scale = min(width, height) / 2

        drawLine(
            color = Color.Red,
            start = Offset(centerX, 0f),
            end = Offset(centerX, height),
            strokeWidth = axisWidth
        )
        drawLine(
            color = Color.Red,
            start = Offset(0f, centerY),
            end = Offset(width, centerY),
            strokeWidth = axisWidth
        )

        lineState.map {
            it.toCenter(centerX = centerX, centerY = centerY, scale = scale)
        }.forEach {

            drawLine(
                color = it.color,
                start = Offset(it.x1, it.y1),
                end = Offset(it.x2, it.y2),
                strokeWidth = loopWidth
            )

        }
    }

}

data class Line(val x1: Float, val y1: Float, val x2: Float, val y2: Float, val color: Color)

fun getX(stepAngle: Double, rotateAngle: Double) =
    (cos(stepAngle) * cos(rotateAngle) - sin(stepAngle) * cos(stepAngle) * sin(rotateAngle)) /
            (1 + sin(stepAngle).pow(2))

fun getY(stepAngle: Double, rotateAngle: Double) =
    (cos(stepAngle) * sin(rotateAngle) - sin(stepAngle) * cos(stepAngle) * cos(rotateAngle)) /
            (1 + sin(stepAngle).pow(2))

fun Int.toRad() = this * Math.PI / 1800

fun Line.toCenter(centerX: Float, centerY: Float, scale: Float): Line {

    val line = Line(
        x1 = x1 * scale + centerX,
        y1 = y1 * scale + centerY,
        x2 = x2 * scale + centerX,
        y2 = y2 * scale + centerY,
        color = color
    )
    return line
}
