package org.example.project.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.example.project.data.PuzzleData

class GameState {
    var currentPuzzleIndex by mutableStateOf(0)
        private set
    
    var currentStep by mutableStateOf(1) // 1 for 4-letter word, 2 for 5-letter word
        private set
    
    var word4Input by mutableStateOf("")
        private set
    
    var word5Input by mutableStateOf("")
        private set
    
    var showSuccess by mutableStateOf(false)
        private set
    
    var permanentHints4 by mutableStateOf(List(4) { "" })
        private set
    
    var permanentHints5 by mutableStateOf(List(5) { "" })
        private set
    
    var completedPuzzles by mutableStateOf(0)
        private set
    
    var shuffledPuzzles by mutableStateOf(emptyList<PuzzleData>())
        private set
    
    var word4Completed by mutableStateOf(false)
        private set
    
    var word5Completed by mutableStateOf(false)
        private set
    
    fun initializePuzzles(puzzles: List<PuzzleData>) {
        shuffledPuzzles = puzzles.shuffled()
        resetPuzzle()
    }
    
    fun getCurrentPuzzle(): PuzzleData? {
        return if (currentPuzzleIndex < shuffledPuzzles.size) {
            shuffledPuzzles[currentPuzzleIndex]
        } else null
    }
    
    fun handleInput(value: String, isStep2: Boolean) {
        val upperValue = value.uppercase()
        if (isStep2) {
            word5Input = upperValue
        } else {
            word4Input = upperValue
        }
        checkAnswer()
    }
    
    fun handleKeyPress(key: String) {
        val isStep2 = currentStep == 2
        val currentWord = if (isStep2) word5Input else word4Input
        val permanentHints = if (isStep2) permanentHints5 else permanentHints4
        val maxLength = if (isStep2) 5 else 4
        
        when (key) {
            "DEL" -> {
                var newWord = currentWord
                for (i in newWord.length - 1 downTo 0) {
                    if (permanentHints[i].isEmpty()) {
                        newWord = newWord.removeRange(i, i + 1)
                        break
                    }
                }
                if (isStep2) {
                    word5Input = newWord
                } else {
                    word4Input = newWord
                }
            }
            else -> {
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
                        
                        if (isStep2) {
                            word5Input = newWord
                        } else {
                            word4Input = newWord
                        }
                    }
                }
            }
        }
        checkAnswer()
    }
    
    private fun checkAnswer() {
        val currentPuzzle = getCurrentPuzzle() ?: return
        
        val completeWord4 = getCompleteWord(word4Input, permanentHints4, 4)
        val completeWord5 = getCompleteWord(word5Input, permanentHints5, 5)
        
        if (currentStep == 1) {
            if (completeWord4 == currentPuzzle.four.uppercase()) {
                word4Completed = true
                // Simple delay simulation - in a real app you'd use a Timer or similar
                currentStep = 2
                word4Completed = false
            }
        } else {
            if (completeWord5 == currentPuzzle.five.uppercase()) {
                word5Completed = true
                // Simple delay simulation - in a real app you'd use a Timer or similar
                showSuccess = true
                completedPuzzles++
                word5Completed = false
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
        currentPuzzleIndex = (currentPuzzleIndex + 1) % shuffledPuzzles.size
        resetPuzzle()
    }
    
    fun resetPuzzle() {
        currentStep = 1
        word4Input = ""
        word5Input = ""
        showSuccess = false
        permanentHints4 = List(4) { "" }
        permanentHints5 = List(5) { "" }
        word4Completed = false
        word5Completed = false
    }
    
    fun getHint(isStep2: Boolean) {
        val currentPuzzle = getCurrentPuzzle() ?: return
        
        val targetWord = if (isStep2) currentPuzzle.five.uppercase() else currentPuzzle.four.uppercase()
        val currentHints = if (isStep2) permanentHints5 else permanentHints4
        
        for (i in targetWord.indices) {
            if (currentHints[i].isEmpty()) {
                val newHints = currentHints.toMutableList()
                newHints[i] = targetWord[i].toString()
                
                if (isStep2) {
                    permanentHints5 = newHints
                } else {
                    permanentHints4 = newHints
                }
                break
            }
        }
        
        // Check if word is complete after hint and auto-progress
        checkAnswerAfterHint(isStep2)
    }
    
    private fun checkAnswerAfterHint(isStep2: Boolean) {
        val currentPuzzle = getCurrentPuzzle() ?: return
        
        if (isStep2) {
            val completeWord5 = getCompleteWord(word5Input, permanentHints5, 5)
            if (completeWord5 == currentPuzzle.five.uppercase()) {
                word5Completed = true
                showSuccess = true
                completedPuzzles++
                word5Completed = false
            }
        } else {
            val completeWord4 = getCompleteWord(word4Input, permanentHints4, 4)
            if (completeWord4 == currentPuzzle.four.uppercase()) {
                word4Completed = true
                currentStep = 2
                word4Completed = false
            }
        }
    }
    
    fun revealAnswer(isStep2: Boolean) {
        val currentPuzzle = getCurrentPuzzle() ?: return
        
        val targetWord = if (isStep2) currentPuzzle.five.uppercase() else currentPuzzle.four.uppercase()
        
        if (isStep2) {
            // Reveal 5-letter word and show success
            word5Input = targetWord
            permanentHints5 = List(5) { "" }
            word5Completed = true
            showSuccess = true
            completedPuzzles++
            word5Completed = false
        } else {
            // Reveal 4-letter word and move to 5-letter word
            word4Input = targetWord
            permanentHints4 = List(4) { "" }
            word4Completed = true
            currentStep = 2
            word4Completed = false
        }
    }
} 