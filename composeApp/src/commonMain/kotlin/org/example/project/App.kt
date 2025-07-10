package org.example.project

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.example.project.ui.WordPyramidScreen

@Composable
@Preview
fun App() {
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Color(0xFF1a1a1a),
            surface = Color(0xFF1a1a1a),
            onBackground = Color.White,
            onSurface = Color.White
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding(),
            color = Color(0xFF1a1a1a)
        ) {
            WordPyramidScreen()
        }
    }
}