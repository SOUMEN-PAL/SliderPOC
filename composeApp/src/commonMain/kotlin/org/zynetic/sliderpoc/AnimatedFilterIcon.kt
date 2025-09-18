package org.zynetic.sliderpoc

import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.delay

@Composable
fun AnimatedFilterIcon(
    modifier: Modifier = Modifier,
    barWidth: Dp = 24.dp,
    barHeight: Dp = 4.dp,
    barSpacing: Dp = 6.dp,
    color: Color = MaterialTheme.colorScheme.primary,
    animationDuration: Int = 300
) {
    var animateForward by remember { mutableStateOf(false) }

    // Heights of bars animated
    val topBarAnim = remember { Animatable(0f) }
    val middleBarAnim = remember { Animatable(0f) }
    val bottomBarAnim = remember { Animatable(0f) }

    // Trigger animation on click
    Box(
        modifier = modifier
            .size(width = barWidth, height = barHeight * 3 + barSpacing * 2)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = {
                    animateForward = !animateForward
                }
            )
    ) {
        LaunchedEffect(animateForward) {
            if (animateForward) {
                topBarAnim.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(animationDuration, easing = LinearEasing)
                )
                middleBarAnim.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(animationDuration, easing = LinearEasing)
                )
                bottomBarAnim.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(animationDuration, easing = LinearEasing)
                )
            } else {
                bottomBarAnim.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(animationDuration, easing = LinearEasing)
                )
                middleBarAnim.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(animationDuration, easing = LinearEasing)
                )
                topBarAnim.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(animationDuration, easing = LinearEasing)
                )
            }
        }

        // Draw the bars
        Column(
            verticalArrangement = Arrangement.spacedBy(barSpacing),
            modifier = Modifier.fillMaxSize()
        ) {
            Bar(color, barWidth, barHeight, topBarAnim.value)
            Bar(color, barWidth * 0.7f, barHeight, middleBarAnim.value) // smaller middle
            Bar(color, barWidth * 0.4f, barHeight, bottomBarAnim.value) // smallest bottom
        }
    }
}

@Composable
fun Bar(color: Color, width: Dp, height: Dp, progress: Float) {
    Box(
        modifier = Modifier
            .width(width * progress)
            .height(height)
            .drawBehind {
                drawRoundRect(
                    color = color,
                    cornerRadius = CornerRadius(height.toPx() / 2, height.toPx() / 2)
                )
            }
    )
}
