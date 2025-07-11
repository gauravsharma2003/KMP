package org.example.project.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

actual class PlatformBackHandler {
    @Composable
    actual operator fun invoke(enabled: Boolean, onBack: () -> Unit) {
        BackHandler(enabled = enabled, onBack = onBack)
    }
}

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    BackHandler(enabled = enabled, onBack = onBack)
} 