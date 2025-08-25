package org.zynetic.sliderpoc

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.dp
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
                state = state,
                track = { sliderState ->
                    /*
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)   // 👈 give the track some visible height
                    ) {
                        val path1 = Path()
                        val path2 = Path()
                        path1.moveTo(0f, y = size.height/2f)
                        path2.moveTo(0f, y = -((size.height)/2f))
                        path1.lineTo(size.width , y = (size.height + 25)/2f )
                        path2.lineTo(size.width , y = -((size.height + 25)/2f))

                        drawPath(
                            path = path1,
                            color = Color.Blue,
                            style = Stroke(width = 5f)
                        )

                        drawPath(
                            path = path2,
                            color = Color.Blue,
                            style = Stroke(width = 5f)
                        )
                    }
                     */

//                    Canvas(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(40.dp) // taller so we can see the widening
//                    ) {
//                        val leftHeight = size.height * 0.3f   // narrow left side
//                        val rightHeight = size.height * 0.9f  // wide right side
//
//                        val halfLeft = leftHeight / 2f
//                        val halfRight = rightHeight / 2f
//
//                        val path = Path().apply {
//                            // Start at left center-bottom
//                            moveTo(0f, size.height / 2f + halfLeft)
//
//                            // Bottom edge → right
//                            lineTo(size.width - halfRight, size.height / 2f + halfRight)
//
//                            // Right arc (big end)
//                            arcTo(
//                                rect = Rect(
//                                    left = size.width - rightHeight,
//                                    top = size.height / 2f - halfRight,
//                                    right = size.width,
//                                    bottom = size.height / 2f + halfRight
//                                ),
//                                startAngleDegrees = 90f,
//                                sweepAngleDegrees = -180f,
//                                forceMoveTo = false
//                            )
//
//                            // Top edge → left
//                            lineTo(halfLeft, size.height / 2f - halfLeft)
//
//                            // Left arc (small end)
//                            arcTo(
//                                rect = androidx.compose.ui.geometry.Rect(
//                                    left = 0f,
//                                    top = size.height / 2f - halfLeft,
//                                    right = leftHeight,
//                                    bottom = size.height / 2f + halfLeft
//                                ),
//                                startAngleDegrees = 270f,
//                                sweepAngleDegrees = -180f,
//                                forceMoveTo = false
//                            )
//
//                            close()
//                        }
//
//                        drawPath(
//                            path = path,
//                            brush = Brush.horizontalGradient(
//                                listOf(Color.Cyan, Color.Blue)
//                            ),
//                            style = Fill
//                        )
//                    }


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
                thumb = {it->
                    Canvas(
                        modifier = Modifier
                            .size(40.dp)
                            .offset(x = if(it.value >= 0.95f) (-10).dp else 0.dp)
                    ) {
                        drawCircle(
                            radius = size.minDimension / 2f,
                            color = Color(0xFF1B1F27),
                            style = Fill
                        )
                        drawCircle(
                            radius = (size.minDimension / 2f)-12,
                            color = Color(0xFFF1F3F9),
                            style = Fill
                        )
                    }
                }
            )



        }
    }
}


