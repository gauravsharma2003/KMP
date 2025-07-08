package org.example.project.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.data.PuzzleData
import org.example.project.data.PuzzleLoader
import org.example.project.game.GameState
import org.example.project.ui.components.ActionButtons
import org.example.project.ui.components.LetterBox
import org.example.project.ui.components.SuccessBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordPyramidScreen() {
    val gameState = remember { GameState() }
    val puzzleLoader = remember { PuzzleLoader() }
    
    var isLoading by remember { mutableStateOf(true) }
    
    LaunchedEffect(Unit) {
        val puzzles = puzzleLoader.loadPuzzles()
        gameState.initializePuzzles(puzzles)
        isLoading = false
    }
    
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0A0A0A)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF9333EA),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Loading puzzles...",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        return
    }
    
    val currentPuzzle = gameState.getCurrentPuzzle()
    
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
                currentPuzzle = gameState.currentPuzzleIndex + 1,
                totalPuzzles = gameState.shuffledPuzzles.size,
                completedPuzzles = gameState.completedPuzzles
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
                currentStep = gameState.currentStep
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Action Buttons
            ActionButtons(
                onHint = { gameState.getHint(gameState.currentStep == 2) },
                onReveal = { gameState.revealAnswer(gameState.currentStep == 2) },
                onReset = { gameState.resetPuzzle() },
                onNext = { gameState.nextPuzzle() }
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Instructions for system keyboard
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Use your device keyboard to type letters • Backspace to delete • Enter to check",
                    color = Color(0xFF9CA3AF),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        
        // Success Bottom Sheet
        if (gameState.showSuccess) {
            SuccessBottomSheet(
                puzzle = currentPuzzle,
                currentPuzzle = gameState.currentPuzzleIndex + 1,
                totalPuzzles = gameState.shuffledPuzzles.size,
                completedPuzzles = gameState.completedPuzzles,
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
            // Handle enter key for checking answers
            val currentPuzzle = gameState.getCurrentPuzzle()
            if (currentPuzzle != null) {
                if (gameState.currentStep == 1) {
                    val completeWord4 = gameState.getCompleteWord(gameState.word4Input, gameState.permanentHints4, 4)
                    if (completeWord4 == currentPuzzle.four.uppercase()) {
                        // Already handled in checkAnswer
                    }
                } else {
                    val completeWord5 = gameState.getCompleteWord(gameState.word5Input, gameState.permanentHints5, 5)
                    if (completeWord5 == currentPuzzle.five.uppercase()) {
                        // Already handled in checkAnswer
                    }
                }
            }
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
        // 3-letter word (completed)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            puzzle.three.uppercase().forEach { letter ->
                LetterBox(
                    letter = letter.toString(),
                    isActive = false,
                    isHinted = false,
                    isCompleted = true,
                    isCorrect = true
                )
            }
        }
        
        // 4-letter word
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (i in 0 until 4) {
                val userLetter = if (i < gameState.word4Input.length) gameState.word4Input[i].toString() else ""
                val hintLetter = gameState.permanentHints4[i]
                val displayLetter = if (hintLetter.isNotEmpty()) hintLetter else userLetter
                
                LetterBox(
                    letter = displayLetter,
                    isActive = gameState.currentStep == 1,
                    isHinted = hintLetter.isNotEmpty(),
                    isCompleted = gameState.word4Completed,
                    isCorrect = gameState.currentStep == 2 || gameState.word4Completed
                )
            }
        }
        
        // 5-letter word
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (i in 0 until 5) {
                val userLetter = if (i < gameState.word5Input.length) gameState.word5Input[i].toString() else ""
                val hintLetter = gameState.permanentHints5[i]
                val displayLetter = if (hintLetter.isNotEmpty()) hintLetter else userLetter
                
                LetterBox(
                    letter = displayLetter,
                    isActive = gameState.currentStep == 2,
                    isHinted = hintLetter.isNotEmpty(),
                    isCompleted = gameState.word5Completed,
                    isCorrect = gameState.word5Completed
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
            modifier = Modifier.padding(16.dp)
        ) {
            if (currentStep == 1) {
                Text(
                    text = "Add one letter to ${puzzle.three.uppercase()} to make:",
                    color = Color(0xFF9CA3AF),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = puzzle.hint4,
                    color = Color.White,
                    fontSize = 16.sp
                )
            } else {
                Text(
                    text = "Add one letter to ${puzzle.four.uppercase()} to make:",
                    color = Color(0xFF9CA3AF),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = puzzle.hint5,
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }
} 