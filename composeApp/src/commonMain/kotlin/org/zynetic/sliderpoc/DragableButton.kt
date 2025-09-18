package org.zynetic.sliderpoc

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@Composable
fun SwipeLoadingButton(
    text: String,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        val parentMaxWidth = maxWidth
        val buttonHeight = 56.dp

        val targetWidth = if (isLoading) buttonHeight else parentMaxWidth

        val animatedWidth by animateDpAsState(
            targetValue = targetWidth,
            animationSpec = tween(600),
            label = "buttonWidthAnim"
        )

        Surface(
            modifier = Modifier
                .width(animatedWidth)
                .height(buttonHeight)
                .clickable(enabled = !isLoading) { onClick() },
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            shadowElevation = 4.dp
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(28.dp)
                    )
                } else {
                    Text(
                        text = text,
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}



@Composable
fun SwipeToConfirmWithLoading(
    modifier: Modifier = Modifier,
    buttonText: String = "Slide to confirm",
    onSwipeComplete: suspend () -> Unit
) {
    val buttonHeight = 56.dp
    val thumbSize = 48.dp
    val horizontalPadding = (buttonHeight - thumbSize) / 2
    val density = LocalDensity.current

    var buttonWidthPx by remember { mutableStateOf(0f) }
    val thumbSizePx = with(density) { thumbSize.toPx() }
    val horizontalPaddingPx = with(density) { horizontalPadding.toPx() }

    var offsetX by remember { mutableStateOf(0f) }
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(false) }

    val trackEndPx by remember(buttonWidthPx, thumbSizePx, horizontalPaddingPx) {
        mutableStateOf((buttonWidthPx - thumbSizePx - horizontalPaddingPx * 2).coerceAtLeast(0f))
    }
    val triggerThreshold by remember(trackEndPx) { mutableStateOf(trackEndPx * 0.85f) }

    // Animate width shrinking when loading
    val targetWidth = if (isLoading) buttonHeight else 350.dp
    val animatedWidth by animateDpAsState(targetValue = targetWidth)

    Box(
        modifier = modifier
            .width(animatedWidth)
            .height(buttonHeight)
            .onGloballyPositioned { buttonWidthPx = it.size.width.toFloat() }
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(percent = 50))
            .clip(RoundedCornerShape(percent = 50)),
        contentAlignment = Alignment.CenterStart
    ) {
        if (!isLoading) {
            // Background fill
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = (offsetX / trackEndPx).coerceIn(0f, 1f)),
                        RoundedCornerShape(percent = 50)
                    )
            )

            // Text label fading out
            val textAlpha by remember(offsetX, trackEndPx) {
                derivedStateOf {
                    if (trackEndPx > 0f) {
                        1f - (offsetX / trackEndPx).coerceIn(0f, 1f)
                    } else {
                        1f // fully visible until track size is known
                    }
                }
            }
            Text(
                text = buttonText,
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = textAlpha)
            )

            // Draggable thumb
            Box(
                modifier = Modifier
                    .offset { IntOffset((offsetX + horizontalPaddingPx).roundToInt(), 0) }
                    .size(thumbSize)
                    .align(Alignment.CenterStart)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .draggable(
                        orientation = Orientation.Horizontal,
                        state = rememberDraggableState { delta ->
                            offsetX = (offsetX + delta).coerceIn(0f, trackEndPx)
                        },
                        onDragStopped = {
                            val swiped = offsetX >= triggerThreshold
                            if (swiped) {
                                offsetX = trackEndPx
                                scope.launch {
                                    isLoading = true
                                    onSwipeComplete()
                                }
                            } else {
                                // Animate back to start
                                scope.launch {
                                    animate(
                                        initialValue = offsetX,
                                        targetValue = 0f,
                                        animationSpec = tween(durationMillis = 200)
                                    ) { value, _ -> offsetX = value }
                                }
                            }
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        } else {
            // Loading circle
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp,
                    modifier = Modifier.size(buttonHeight / 2)
                )
            }
        }
    }
}



/*
@Composable
fun SwipeToConfirmButton(
    modifier: Modifier = Modifier,
    buttonText: String = "Slide to confirm",
    onSwipeComplete: () -> Unit
) {
    val buttonHeight = 56.dp
    val thumbSize = 48.dp
    val horizontalPadding = (buttonHeight - thumbSize) / 2

    val density = LocalDensity.current
    var buttonWidthPx by remember { mutableStateOf(0f) }
    val thumbSizePx = with(density) { thumbSize.toPx() }
    val horizontalPaddingPx = with(density) { horizontalPadding.toPx() }

    var offsetX by remember { mutableStateOf(0f) }
    val scope = rememberCoroutineScope()

    val trackEndPx by remember(buttonWidthPx, thumbSizePx, horizontalPaddingPx) {
        mutableStateOf((buttonWidthPx - thumbSizePx - horizontalPaddingPx * 2).coerceAtLeast(0f))
    }
    val triggerThreshold by remember(trackEndPx) { mutableStateOf(trackEndPx * 0.85f) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(buttonHeight)
            .onGloballyPositioned { buttonWidthPx = it.size.width.toFloat() }
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(percent = 50))
            .clip(RoundedCornerShape(percent = 50))
    ) {
        // Background fill
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = (offsetX / trackEndPx).coerceIn(0f, 1f)),
                    RoundedCornerShape(percent = 50)
                )
        )

        // Text label fading out
        val textAlpha by remember(offsetX) { derivedStateOf { 1f - (offsetX / trackEndPx).coerceIn(0f, 1f) } }
        Text(
            text = buttonText,
            modifier = Modifier.align(Alignment.Center),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = textAlpha)
        )

        // Draggable thumb
        Box(
            modifier = Modifier
                .offset { IntOffset((offsetX + horizontalPaddingPx).roundToInt(), 0) }
                .size(thumbSize)
                .align(Alignment.CenterStart)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        offsetX = (offsetX + delta).coerceIn(0f, trackEndPx)
                    },
                    onDragStopped = {
                        val swiped = offsetX >= triggerThreshold
                        if (swiped) {
                            offsetX = trackEndPx // stop at end
                            onSwipeComplete()
                        } else {
                            // Animate back to start
                            scope.launch {
                                animate(
                                    initialValue = offsetX,
                                    targetValue = 0f,
                                    animationSpec = tween(durationMillis = 200)
                                ) { value, _ -> offsetX = value }
                            }
                        }
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            // Thumb icon changes on completion
            Crossfade(targetState = offsetX >= triggerThreshold) { confirmed ->
                Icon(
                    imageVector = if (confirmed) Icons.Default.Done else Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}

 */

/*
@Composable
fun SwipeToConfirmButton(
    modifier: Modifier = Modifier,
    buttonText: String = "Slide to confirm",
    onSwipeComplete: () -> Unit
) {
    val buttonHeight = 56.dp
    val thumbSize = 48.dp
    val horizontalPadding = (buttonHeight - thumbSize) / 2

    val density = LocalDensity.current
    var buttonWidthPx by remember { mutableStateOf(0f) }
    val thumbSizePx = with(density) { thumbSize.toPx() }
    val horizontalPaddingPx = with(density) { horizontalPadding.toPx() }

    var offsetX by remember { mutableStateOf(0f) }
    val scope = rememberCoroutineScope()

    val trackEndPx = remember(buttonWidthPx, thumbSizePx, horizontalPaddingPx) {
        (buttonWidthPx - thumbSizePx - horizontalPaddingPx * 2).coerceAtLeast(0f)
    }
    val triggerThreshold = trackEndPx * 0.85f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(buttonHeight)
            .onGloballyPositioned {
                buttonWidthPx = it.size.width.toFloat()
            }
            .background(Color.LightGray, RoundedCornerShape(percent = 50))
            .clip(RoundedCornerShape(percent = 50))
    ) {
        Text(
            text = buttonText,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = (-30).dp)
                .padding(start = thumbSize + (horizontalPadding * 2) + 8.dp, end = 16.dp),
            color = Color.DarkGray
        )

        Box(
            modifier = Modifier
                .offset {
                    IntOffset((offsetX + horizontalPaddingPx).roundToInt(), 0)
                }
                .size(thumbSize)
                .align(Alignment.CenterStart)
                .clip(CircleShape)
                .background(Color.Blue)
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        offsetX = (offsetX + delta).coerceIn(0f, trackEndPx)
                    },
                    onDragStopped = {
                        val swiped = offsetX >= triggerThreshold
                        if (swiped) {
                            onSwipeComplete()
//                            scope.cancel() //Cancel the scope as we would navigate to next screen
                        }
                        else{
                            scope.launch {
                                animate(
                                    initialValue = offsetX,
                                    targetValue = 0f,
                                    animationSpec = tween(durationMillis = 200)
                                ) { value, _ -> offsetX = value }
                            }
                        }

                        /**
                         * Two kinds of behaviour I was lookin for
                         * incomplete Drag: back to start
                         * complete Drag : Stop at end
                         */


//                        scope.launch {
//                            animate(
//                                initialValue = offsetX,
//                                targetValue = if (swiped && false) trackEndPx else 0f,
//                                animationSpec = tween(durationMillis = if (swiped && false) 100 else 300)
//                            ) { value, _ -> offsetX = value }
//                        }
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Swipe to confirm thumb",
                tint = Color.White
            )
        }
    }
}

*/