package org.zynetic.sliderpoc

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import sliderpoc.composeapp.generated.resources.Res
import sliderpoc.composeapp.generated.resources.compose_multiplatform

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    MaterialTheme {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            CubicCurveDemo()


        }
    }
}

@Composable
fun CubicCurveDemo(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .border(1.dp, Color.Black)
    ) {
        // Get the actual canvas dimensions
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Define shape parameters as ratios of canvas dimensions
        val startY = canvasHeight * 0.75f // 75% from top
        val peakY = canvasHeight * 0.1f    // 10% from top (peak height)

        // Start more inward from left edge
        val start1 = Offset(canvasWidth * 0.0f, startY)

        // First curve control points - only TOP control point pushed outward
        val c1_1 = Offset(canvasWidth * 0.15f, startY) // Bottom control point - keep original position
        val c1_2 = Offset(canvasWidth * 0.18f, peakY)  // TOP control point - pushed MORE outward
        val end1 = Offset(canvasWidth * 0.40f, peakY)  // End of first curve (your desired 0.40f)

        // Straight section
        val straightLen = canvasWidth * 0.20f // Adjusted to 20% since we're using 0.40f for first curve
        val endStraight = Offset(end1.x + straightLen, end1.y)

        // Control points for straight section
        val straightCtrl1 = Offset(end1.x + straightLen / 3f, end1.y)
        val straightCtrl2 = Offset(end1.x + 2f * straightLen / 3f, end1.y)

        // Second curve control points - properly mirror the first curve
        // Mirror c1_2 around the center of the canvas
        val c2_1 = Offset(
            canvasWidth - (c1_2.x - canvasWidth * 0.0f), // Mirror c1_2 position relative to canvas width
            peakY
        )
        // Mirror c1_1 around the center of the canvas
        val c2_2 = Offset(
            canvasWidth - (c1_1.x - canvasWidth * 0.0f), // Mirror c1_1 position relative to canvas width
            startY
        )
        val end2 = Offset(canvasWidth, startY) // End at the very right edge

        val path = Path().apply {
            moveTo(start1.x, start1.y)

            // First curve
            cubicTo(c1_1.x, c1_1.y, c1_2.x, c1_2.y, end1.x, end1.y)

            // Straight section
            cubicTo(
                straightCtrl1.x, straightCtrl1.y,
                straightCtrl2.x, straightCtrl2.y,
                endStraight.x, endStraight.y
            )

            // Second mirrored curve
            cubicTo(
                c2_1.x, c2_1.y,
                c2_2.x, c2_2.y,
                end2.x, end2.y
            )

            // Close the path by connecting back to start
            lineTo(start1.x, start1.y)
            close()
        }

        val path2 = Path().apply {
            moveTo(start1.x, start1.y)

            // First curve
            cubicTo(c1_1.x, c1_1.y, c1_2.x, c1_2.y, end1.x, end1.y)

            // Straight section
            cubicTo(
                straightCtrl1.x, straightCtrl1.y,
                straightCtrl2.x, straightCtrl2.y,
                endStraight.x, endStraight.y
            )

            // Second mirrored curve
            cubicTo(
                c2_1.x, c2_1.y,
                c2_2.x, c2_2.y,
                end2.x, end2.y
            )

            // Close the path by connecting back to start
        }

        drawPath(
            path = path,
            color = Color.Black,
            style = Fill
        )
        drawPath(
            path = path2,
            color = Color.Red,
            style = Stroke(10f)
        )
    }
}

/*
@Composable
fun CubicCurveDemo(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .border(1.dp, Color.Black)
    ) {
        // Get the actual canvas dimensions
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Define shape parameters as ratios of canvas dimensions
        val startY = canvasHeight * 0.75f // 75% from top
        val peakY = canvasHeight * 0.1f    // 10% from top (peak height)

        // Start more inward from left edge
        val start1 = Offset(canvasWidth * 0.0f, startY)

        // First curve control points - only TOP control point pushed outward
        val c1_1 = Offset(canvasWidth * 0.15f, startY) // Bottom control point - keep original position
        val c1_2 = Offset(canvasWidth * 0.18f, peakY)  // TOP control point - pushed MORE outward
        val end1 = Offset(canvasWidth * 0.40f, peakY)  // End of first curve

        // Straight section
        val straightLen = canvasWidth * 0.50f // 50% of canvas width
        val endStraight = Offset(end1.x + straightLen, end1.y)

        // Control points for straight section
        val straightCtrl1 = Offset(end1.x + straightLen / 3f, end1.y)
        val straightCtrl2 = Offset(end1.x + 2f * straightLen / 3f, end1.y)

        // Second curve control points - mirror the first curve properly
        val c2_1 = Offset(
            endStraight.x + (end1.x - c1_2.x), // Mirror the first curve's handle properly
            endStraight.y + (end1.y - c1_2.y)
        )
        val c2_2 = Offset(canvasWidth * 0.85f, startY) // Bottom control point - keep original position
        val end2 = Offset(canvasWidth, startY) // End at the very right edge

        val path = Path().apply {
            moveTo(start1.x, start1.y)

            // First curve
            cubicTo(c1_1.x, c1_1.y, c1_2.x, c1_2.y, end1.x, end1.y)

            // Straight section
            cubicTo(
                straightCtrl1.x, straightCtrl1.y,
                straightCtrl2.x, straightCtrl2.y,
                endStraight.x, endStraight.y
            )

            // Second mirrored curve
            cubicTo(
                c2_1.x, c2_1.y,
                c2_2.x, c2_2.y,
                end2.x, end2.y
            )

            // Close the path by connecting back to start
            lineTo(start1.x, start1.y)
            close()
        }

        drawPath(
            path = path,
            color = Color.Black,
            style = Fill
        )
    }
}


 */

/*
@Composable
fun CubicCurveDemo(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(400.dp)
            .border(1.dp, Color.Black)
    ) {

        val start1 = Offset(50f, 300f)
        val c1_1 = Offset(150f, 300f)
        val c1_2 = Offset(350f, 0f)
        val end1 = Offset(410f, 0f)

// mirror horizontally across vertical axis at x = end1.x
        val dx1 = c1_1.x - end1.x
        val dx2 = c1_2.x - end1.x
        val rawC2_2 = Offset(end1.x - dx1, c1_1.y)
        val rawEnd2 = Offset(end1.x + (end1.x - start1.x), start1.y)

// how long you want the “straight” part
        val straightLen = 300f
        val endStraight = Offset(end1.x + straightLen, end1.y)

// build a *single* cubic segment from end1 to endStraight
// control points all on a straight line horizontally:
        val straightCtrl1 = Offset(end1.x + straightLen / 3f, end1.y)
        val straightCtrl2 = Offset(end1.x + 2f * straightLen / 3f, end1.y)

// then continue into the mirrored curve starting at endStraight
        val shiftX = straightLen
        val c2_1 = Offset(
            endStraight.x + (end1.x - c1_2.x), // reflected handle
            endStraight.y + (end1.y - c1_2.y)
        )
        val c2_2 = Offset(rawC2_2.x + shiftX, rawC2_2.y)
        val end2 = Offset(rawEnd2.x + shiftX, rawEnd2.y)

        val path = Path().apply {
            moveTo(start1.x, start1.y)
            // first curve
            cubicTo(c1_1.x, c1_1.y, c1_2.x, c1_2.y, end1.x, end1.y)
            // “straight” part as a cubic segment
            cubicTo(
                straightCtrl1.x, straightCtrl1.y,
                straightCtrl2.x, straightCtrl2.y,
                endStraight.x, endStraight.y
            )
            // second mirrored curve starting smoothly at endStraight
            cubicTo(
                c2_1.x, c2_1.y,
                c2_2.x, c2_2.y,
                end2.x, end2.y
            )
            close()
        }

        drawPath(
            path = path,
            color = Color.Black,
            style = Fill
        )
    }
}


 */

@Composable
fun AnimatedBorderDropdown() {
    var expanded by remember { mutableStateOf(false) }
    val progress by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec = tween(1200),
        label = "borderProgress"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        // Toggle button
        Button(onClick = { expanded = !expanded }) {
            Text(if (expanded) "Close Card" else "Open Card")
        }

        // Expandable wrapper with animated border
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                // Border animation
                Canvas(modifier = Modifier.matchParentSize()) {
                    val rect = Rect(0f, 0f, size.width, size.height)
                    val corner = 24.dp.toPx() // change this for roundness

                    val fullPath = Path().apply {
                        addRoundRect(
                            RoundRect(
                                rect,
                                cornerRadius = CornerRadius(corner, corner)
                            )
                        )
                    }

                    val pathMeasure = PathMeasure()
                    pathMeasure.setPath(fullPath, false)

                    val length = pathMeasure.length
                    val animatedPath = Path()
                    pathMeasure.getSegment(
                        0f,
                        length * progress,
                        animatedPath,
                        true
                    )

                    drawPath(
                        path = animatedPath,
                        color = Color.Black,
                        style = Stroke(
                            width = 3.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round // smooth corners
                        )
                    )
                }

                // Card content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White, shape = RoundedCornerShape(24.dp))
                        .padding(16.dp)
                ) {
                    Text("Expandable Card", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text("Now the border has rounded corners as it animates.")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun slider() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .safeContentPadding(),
        Arrangement.Center,
        Alignment.CenterHorizontally
    ) {
        val state = SliderState(
            value = 0.5f,
            valueRange = 0f..1f,
        )
        Slider(
            modifier = Modifier.fillMaxWidth(),
            state = state,
            track = { sliderState ->
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                ) {
                    val leftHeight = size.height * 0.3f   // narrow left side
                    val rightHeight = size.height * 0.9f  // wide right side

                    val halfLeft = leftHeight / 2f
                    val halfRight = rightHeight / 2f

                    // Build your original tapered shape
                    val path = Path().apply {
                        moveTo(0f, size.height / 2f + halfLeft)

                        lineTo(size.width - halfRight, size.height / 2f + halfRight)

                        arcTo(
                            rect = Rect(
                                left = size.width - rightHeight,
                                top = size.height / 2f - halfRight,
                                right = size.width,
                                bottom = size.height / 2f + halfRight
                            ),
                            startAngleDegrees = 90f,
                            sweepAngleDegrees = -180f,
                            forceMoveTo = false
                        )

                        lineTo(halfLeft, size.height / 2f - halfLeft)

                        arcTo(
                            rect = Rect(
                                left = 0f,
                                top = size.height / 2f - halfLeft,
                                right = leftHeight,
                                bottom = size.height / 2f + halfLeft
                            ),
                            startAngleDegrees = 270f,
                            sweepAngleDegrees = -180f,
                            forceMoveTo = false
                        )

                        close()
                    }

                    // Position of thumb
                    val thumbX = sliderState.value * size.width

                    // Active part (left of thumb)
                    clipRect(left = 0f, top = 0f, right = thumbX, bottom = size.height) {
                        drawPath(
                            path = path,
                            color = Color(0xFF1B1F27),
                            style = Fill
                        )
                    }

                    // Inactive part (right of thumb)
                    clipRect(left = thumbX, top = 0f, right = size.width, bottom = size.height) {
                        drawPath(
                            path = path,
                            color = Color.LightGray,
                            style = Fill
                        )
                    }
                }


            },
            thumb = { it ->
                Canvas(
                    modifier = Modifier
                        .size(40.dp)
                        .offset(x = if (it.value >= 0.95f) (-10).dp else 0.dp)
                ) {
                    drawCircle(
                        radius = size.minDimension / 2f,
                        color = Color(0xFF1B1F27),
                        style = Fill
                    )
                    drawCircle(
                        radius = (size.minDimension / 2f) - 12,
                        color = Color(0xFFF1F3F9),
                        style = Fill
                    )
                }
            }
        )

    }
}
