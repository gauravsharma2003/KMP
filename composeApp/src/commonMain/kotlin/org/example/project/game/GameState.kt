package org.example.project.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.example.project.data.PuzzleData

enum class GameStep {
    WORD_4,
    WORD_5,
    PUZZLE_COMPLETED
}

data class GameLogicState(
    val currentPuzzleIndex: Int = 0,
    val currentStep: GameStep = GameStep.WORD_4,
    val word4Input: String = "",
    val word5Input: String = "",
    val permanentHints4: List<String> = List(4) { "" },
    val permanentHints5: List<String> = List(5) { "" },
    val completedPuzzles: Int = 0,
    val shuffledPuzzles: List<PuzzleData> = emptyList(),
    // Animation states
    val glowingLetters4: Set<Int> = emptySet(),
    val glowingLetters5: Set<Int> = emptySet(),
    val word4Glowing: Boolean = false,
    val word5Glowing: Boolean = false
)

class GameState {
    private var _state by mutableStateOf(GameLogicState())
    private val animationScope = CoroutineScope(Dispatchers.Main)
    
    // Public read-only access to state
    val state: GameLogicState get() = _state
    
    // Derived properties for UI
    val currentPuzzle: PuzzleData? 
        get() = if (_state.currentPuzzleIndex < _state.shuffledPuzzles.size) {
            _state.shuffledPuzzles[_state.currentPuzzleIndex]
        } else null
    
    val isWord4Complete: Boolean
        get() = currentPuzzle?.let { puzzle ->
            getCompleteWord(_state.word4Input, _state.permanentHints4, 4) == puzzle.four.uppercase()
        } ?: false
    
    val isWord5Complete: Boolean
        get() = currentPuzzle?.let { puzzle ->
            getCompleteWord(_state.word5Input, _state.permanentHints5, 5) == puzzle.five.uppercase()
        } ?: false
    
    val isPuzzleCompleted: Boolean
        get() = _state.currentStep == GameStep.PUZZLE_COMPLETED
    
    val shouldShowSuccess: Boolean
        get() = isPuzzleCompleted
    
    val isCurrentStep2: Boolean
        get() = _state.currentStep == GameStep.WORD_5
    
    val currentStepNumber: Int
        get() = when (_state.currentStep) {
            GameStep.WORD_4 -> 1
            GameStep.WORD_5 -> 2
            GameStep.PUZZLE_COMPLETED -> 2
        }
    
    fun initializePuzzles(puzzles: List<PuzzleData>) {
        _state = _state.copy(
            shuffledPuzzles = puzzles.shuffled()
        )
        resetPuzzle()
    }
    
    fun handleKeyPress(key: String) {
        when (key) {
            "DEL" -> handleBackspace()
            else -> handleLetterInput(key)
        }
        checkGameProgression()
    }
    
    private fun handleBackspace() {
        val isStep2 = _state.currentStep == GameStep.WORD_5
        val currentWord = if (isStep2) _state.word5Input else _state.word4Input
        val permanentHints = if (isStep2) _state.permanentHints5 else _state.permanentHints4
        
        var newWord = currentWord
        for (i in newWord.length - 1 downTo 0) {
            if (permanentHints[i].isEmpty()) {
                newWord = newWord.removeRange(i, i + 1)
                break
            }
        }
        
        _state = if (isStep2) {
            _state.copy(word5Input = newWord)
        } else {
            _state.copy(word4Input = newWord)
        }
    }
    
    private fun handleLetterInput(key: String) {
        val isStep2 = _state.currentStep == GameStep.WORD_5
        val currentWord = if (isStep2) _state.word5Input else _state.word4Input
        val permanentHints = if (isStep2) _state.permanentHints5 else _state.permanentHints4
        val maxLength = if (isStep2) 5 else 4
        
        if (currentWord.length < maxLength) {
            var insertPosition = -1
            for (i in 0 until maxLength) {
                if (permanentHints[i].isEmpty() && (i >= currentWord.length || currentWord[i] == ' ')) {
                    insertPosition = i
                    break
                }
            }
            
            if (insertPosition != -1) {
                val newWord = if (insertPosition < currentWord.length) {
                    currentWord.substring(0, insertPosition) + key + currentWord.substring(insertPosition + 1)
                } else {
                    currentWord + key
                }
                
                _state = if (isStep2) {
                    _state.copy(word5Input = newWord)
                } else {
                    _state.copy(word4Input = newWord)
                }
            }
        }
    }
    
    private fun checkGameProgression() {
        when (_state.currentStep) {
            GameStep.WORD_4 -> {
                if (isWord4Complete) {
                    triggerWordCompletionAnimation(false)
                    animationScope.launch {
                        delay(1000) // Show animation for 1 second
                        _state = _state.copy(
                            currentStep = GameStep.WORD_5,
                            word4Glowing = false
                        )
                    }
                }
            }
            GameStep.WORD_5 -> {
                if (isWord5Complete) {
                    triggerWordCompletionAnimation(true)
                    animationScope.launch {
                        delay(1000) // Show animation for 1 second
                        _state = _state.copy(
                            currentStep = GameStep.PUZZLE_COMPLETED,
                            completedPuzzles = _state.completedPuzzles + 1,
                            word5Glowing = false
                        )
                    }
                }
            }
            GameStep.PUZZLE_COMPLETED -> {
                // Already completed, waiting for next puzzle
            }
        }
    }
    
    private fun triggerWordCompletionAnimation(isStep2: Boolean) {
        _state = if (isStep2) {
            _state.copy(word5Glowing = true)
        } else {
            _state.copy(word4Glowing = true)
        }
    }
    
    private fun triggerLetterRevealAnimation(letterIndex: Int, isStep2: Boolean) {
        val currentGlowing = if (isStep2) _state.glowingLetters5 else _state.glowingLetters4
        val newGlowing = currentGlowing + letterIndex
        
        _state = if (isStep2) {
            _state.copy(glowingLetters5 = newGlowing)
        } else {
            _state.copy(glowingLetters4 = newGlowing)
        }
        
        // Remove glow after animation
        animationScope.launch {
            delay(1500) // Show letter glow for 1.5 seconds
            val updatedGlowing = if (isStep2) _state.glowingLetters5 else _state.glowingLetters4
            val finalGlowing = updatedGlowing - letterIndex
            
            _state = if (isStep2) {
                _state.copy(glowingLetters5 = finalGlowing)
            } else {
                _state.copy(glowingLetters4 = finalGlowing)
            }
        }
    }
    
    fun getCompleteWord(userInput: String, permanentHints: List<String>, length: Int): String {
        var completeWord = ""
        for (i in 0 until length) {
            completeWord += if (permanentHints[i].isNotEmpty()) {
                permanentHints[i]
            } else if (i < userInput.length) {
                userInput[i]
            } else {
                ""
            }
        }
        return completeWord
    }
    
    fun nextPuzzle() {
        val nextIndex = (_state.currentPuzzleIndex + 1) % _state.shuffledPuzzles.size
        _state = _state.copy(currentPuzzleIndex = nextIndex)
        resetPuzzle()
    }
    
    fun resetPuzzle() {
        _state = _state.copy(
            currentStep = GameStep.WORD_4,
            word4Input = "",
            word5Input = "",
            permanentHints4 = List(4) { "" },
            permanentHints5 = List(5) { "" },
            glowingLetters4 = emptySet(),
            glowingLetters5 = emptySet(),
            word4Glowing = false,
            word5Glowing = false
        )
    }
    
    fun getHint(isStep2: Boolean) {
        val currentPuzzle = this.currentPuzzle ?: return
        
        val targetWord = if (isStep2) currentPuzzle.five.uppercase() else currentPuzzle.four.uppercase()
        val currentHints = if (isStep2) _state.permanentHints5 else _state.permanentHints4
        
        for (i in targetWord.indices) {
            if (currentHints[i].isEmpty()) {
                val newHints = currentHints.toMutableList()
                newHints[i] = targetWord[i].toString()
                
                _state = if (isStep2) {
                    _state.copy(permanentHints5 = newHints)
                } else {
                    _state.copy(permanentHints4 = newHints)
                }
                
                // Trigger letter reveal animation
                triggerLetterRevealAnimation(i, isStep2)
                break
            }
        }
        
        checkGameProgression()
    }
    
    fun revealAnswer(isStep2: Boolean) {
        val currentPuzzle = this.currentPuzzle ?: return
        
        val targetWord = if (isStep2) currentPuzzle.five.uppercase() else currentPuzzle.four.uppercase()
        
        if (isStep2) {
            // Reveal 5-letter word
            _state = _state.copy(
                word5Input = targetWord,
                permanentHints5 = List(5) { "" },
                currentStep = GameStep.PUZZLE_COMPLETED,
                completedPuzzles = _state.completedPuzzles + 1
            )
            triggerWordCompletionAnimation(true)
            animationScope.launch {
                delay(1000)
                _state = _state.copy(word5Glowing = false)
            }
        } else {
            // Reveal 4-letter word and move to 5-letter word
            _state = _state.copy(
                word4Input = targetWord,
                permanentHints4 = List(4) { "" },
                currentStep = GameStep.WORD_5
            )
            triggerWordCompletionAnimation(false)
            animationScope.launch {
                delay(1000)
                _state = _state.copy(word4Glowing = false)
            }
        }
    }
} 