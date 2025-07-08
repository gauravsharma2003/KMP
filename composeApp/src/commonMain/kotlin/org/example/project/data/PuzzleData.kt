package org.example.project.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PuzzleData(
    @SerialName("puzzle_id") val puzzleId: Int,
    val three: String,
    val four: String,
    val five: String,
    @SerialName("4hint") val hint4: String,
    @SerialName("5hint") val hint5: String
) 