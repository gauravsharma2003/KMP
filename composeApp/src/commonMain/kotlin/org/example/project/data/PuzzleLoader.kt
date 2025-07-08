package org.example.project.data

import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi
import takeitezzyy.composeapp.generated.resources.Res

class PuzzleLoader {
    @OptIn(ExperimentalResourceApi::class)
    suspend fun loadPuzzles(): List<PuzzleData> {
        return try {
            val jsonString = Res.readBytes("files/PyramidPuzzle.json").decodeToString()
            Json.decodeFromString<List<PuzzleData>>(jsonString)
        } catch (e: Exception) {
            println("Error loading puzzles: ${e.message}")
            emptyList()
        }
    }
} 