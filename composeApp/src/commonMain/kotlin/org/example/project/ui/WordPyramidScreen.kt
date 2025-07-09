package org.example.project.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.data.PuzzleData
import org.example.project.data.PuzzleLoader
import org.example.project.game.GameState
import org.example.project.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordPyramidScreen() {
    val gameState = remember { GameState() }
    val puzzleLoader = remember { PuzzleLoader() }
    
    // Initialize puzzles on first composition
    remember {
        val puzzles = puzzleLoader.loadPuzzles()
        gameState.initializePuzzles(puzzles)
    }

    val currentPuzzle = gameState.currentPuzzle
    
    if (currentPuzzle == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0A0A0A)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No puzzles available",
                color = Color.White,
                fontSize = 18.sp
            )
        }
        return
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
            .focusable()
            .onKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyDown) {
                    handleKeyInput(keyEvent, gameState)
                } else false
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header (simplified without branding)
            GameHeader(
                currentPuzzle = gameState.state.currentPuzzleIndex + 1,
                totalPuzzles = gameState.state.shuffledPuzzles.size,
                completedPuzzles = gameState.state.completedPuzzles
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Word Pyramid
            WordPyramid(
                puzzle = currentPuzzle,
                gameState = gameState
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Hint Text
            HintText(
                puzzle = currentPuzzle,
                currentStep = gameState.currentStepNumber
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Action Buttons
            ActionButtons(
                onHint = { gameState.getHint(gameState.isCurrentStep2) },
                onReveal = { gameState.revealAnswer(gameState.isCurrentStep2) },
                onReset = { gameState.resetPuzzle() },
                onNext = { gameState.nextPuzzle() }
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Custom Keyboard at bottom
            CustomKeyboard(
                onKeyPress = { key -> gameState.handleKeyPress(key) }
            )
        }
        
        // Success Bottom Sheet - driven by game logic
        if (gameState.shouldShowSuccess) {
            SuccessBottomSheet(
                puzzle = currentPuzzle,
                currentPuzzle = gameState.state.currentPuzzleIndex + 1,
                totalPuzzles = gameState.state.shuffledPuzzles.size,
                completedPuzzles = gameState.state.completedPuzzles,
                onNext = { gameState.nextPuzzle() }
            )
        }
    }
}

private fun handleKeyInput(keyEvent: KeyEvent, gameState: GameState): Boolean {
    val key = keyEvent.key
    
    return when {
        key == Key.Backspace -> {
            gameState.handleKeyPress("DEL")
            true
        }
        key == Key.Enter -> {
            // Enter key handling can be added if needed for explicit answer checking
            true
        }
        key.keyCode >= Key.A.keyCode && key.keyCode <= Key.Z.keyCode -> {
            val letter = ('A' + (key.keyCode - Key.A.keyCode).toInt()).toString()
            gameState.handleKeyPress(letter)
            true
        }
        else -> false
    }
}

@Composable
private fun GameHeader(
    currentPuzzle: Int,
    totalPuzzles: Int,
    completedPuzzles: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Puzzle $currentPuzzle of $totalPuzzles | Completed: $completedPuzzles",
                fontSize = 16.sp,
                color = Color(0xFF9CA3AF),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun WordPyramid(
    puzzle: PuzzleData,
    gameState: GameState
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top row - 3-letter word (always visible)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            puzzle.three.uppercase().forEach { letter ->
                LetterBox(
                    letter = letter.toString(),
                    isActive = false,
                    isRevealed = true,
                    isCompleted = true
                )
            }
        }
        
        // Middle row - 4-letter word
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val word4Input = gameState.state.word4Input
            val hints4 = gameState.state.permanentHints4
            val isWord4Active = !gameState.isCurrentStep2
            val isWord4Complete = gameState.isWord4Complete
            
            repeat(4) { index ->
                val letter = when {
                    hints4[index].isNotEmpty() -> hints4[index]
                    index < word4Input.length -> word4Input[index].toString()
                    else -> ""
                }
                
                LetterBox(
                    letter = letter,
                    isActive = isWord4Active && index == word4Input.length && hints4[index].isEmpty(),
                    isRevealed = hints4[index].isNotEmpty(),
                    isCompleted = isWord4Complete
                )
            }
        }
        
        // Bottom row - 5-letter word
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val word5Input = gameState.state.word5Input
            val hints5 = gameState.state.permanentHints5
            val isWord5Active = gameState.isCurrentStep2
            val isWord5Complete = gameState.isWord5Complete
            
            repeat(5) { index ->
                val letter = when {
                    hints5[index].isNotEmpty() -> hints5[index]
                    index < word5Input.length -> word5Input[index].toString()
                    else -> ""
                }
                
                LetterBox(
                    letter = letter,
                    isActive = isWord5Active && index == word5Input.length && hints5[index].isEmpty(),
                    isRevealed = hints5[index].isNotEmpty(),
                    isCompleted = isWord5Complete
                )
            }
        }
    }
}

@Composable
private fun HintText(
    puzzle: PuzzleData,
    currentStep: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${if (currentStep == 1) "4" else "5"}-letter word hint:",
                fontSize = 14.sp,
                color = Color(0xFF9CA3AF),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (currentStep == 1) puzzle.hint4 else puzzle.hint5,
                fontSize = 16.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
        }
    }
} 