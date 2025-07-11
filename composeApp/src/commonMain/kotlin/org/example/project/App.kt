package org.example.project

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.example.project.ui.HomeScreen
import org.example.project.ui.WordPyramidScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

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
            var selectedGame by remember { mutableStateOf<String?>(null) }

            Crossfade(targetState = selectedGame) { gameId ->
                when (gameId) {
                    null -> HomeScreen(onGameSelected = { selectedGame = it })
                    "word_pyramid" -> WordPyramidScreen(onBackClicked = { selectedGame = null })
                    else -> HomeScreen(onGameSelected = { selectedGame = it }) // Fallback to home
                }
            }
        }
    }
}