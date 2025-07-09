package org.example.project.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    modifier: Modifier = Modifier
) {
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
    
    Box(
        modifier = modifier
            .size(48.dp)
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