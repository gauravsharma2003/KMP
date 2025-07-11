package org.example.project.ui

import androidx.compose.runtime.Composable

expect class PlatformBackHandler {
    @Composable
    operator fun invoke(enabled: Boolean = true, onBack: () -> Unit)
}

@Composable
expect fun BackHandler(enabled: Boolean = true, onBack: () -> Unit) 