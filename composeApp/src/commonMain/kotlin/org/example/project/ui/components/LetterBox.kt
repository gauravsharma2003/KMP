package org.example.project.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LetterBox(
    letter: String,
    isActive: Boolean = false,
    isRevealed: Boolean = false,
    isCompleted: Boolean = false,
    shouldGlow: Boolean = false,
    modifier: Modifier = Modifier
) {
    // Glow animation
    val infiniteTransition = rememberInfiniteTransition()
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (shouldGlow) 0.8f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    val glowSize by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (shouldGlow) 8f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        )
    )

    val backgroundColor by animateColorAsState(
        targetValue = when {
            isCompleted -> Color(0xFF10B981) // Green for completed words
            isRevealed -> Color(0xFFFBBF24) // Yellow for revealed hints
            isActive -> Color(0xFF3B82F6) // Blue for current input position
            letter.isNotEmpty() -> Color(0xFF374151) // Dark gray for user input
            else -> Color(0xFF1A1A1A) // Darker gray for empty
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    val borderColor by animateColorAsState(
        targetValue = when {
            isCompleted -> Color(0xFF10B981) // Green
            isRevealed -> Color(0xFFFBBF24) // Yellow
            isActive -> Color(0xFF3B82F6) // Blue
            letter.isNotEmpty() -> Color(0xFF6B7280) // Medium gray for user input
            else -> Color(0xFF4B5563) // Gray for empty
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    val textColor by animateColorAsState(
        targetValue = when {
            isCompleted -> Color.White
            isRevealed -> Color.Black // Black text on yellow background
            isActive -> Color.White
            letter.isNotEmpty() -> Color.White
            else -> Color(0xFF9CA3AF) // Light gray for empty
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    // Scale animation for emphasis
    val scale by animateFloatAsState(
        targetValue = if (shouldGlow) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )
    
    Box(
        modifier = modifier
            .size(48.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .drawBehind {
                if (shouldGlow) {
                    drawGlowEffect(
                        glowColor = Color(0xFF10B981),
                        alpha = glowAlpha,
                        glowSize = glowSize
                    )
                }
            }
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letter,
            color = textColor,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

private fun DrawScope.drawGlowEffect(
    glowColor: Color,
    alpha: Float,
    glowSize: Float
) {
    val centerX = size.width / 2
    val centerY = size.height / 2
    val radius = (size.minDimension / 2) + glowSize
    
    // Outer glow
    drawCircle(
        color = glowColor.copy(alpha = alpha * 0.3f),
        radius = radius * 1.2f,
        center = androidx.compose.ui.geometry.Offset(centerX, centerY)
    )
    
    // Inner glow
    drawCircle(
        color = glowColor.copy(alpha = alpha * 0.6f),
        radius = radius,
        center = androidx.compose.ui.geometry.Offset(centerX, centerY)
    )
} 