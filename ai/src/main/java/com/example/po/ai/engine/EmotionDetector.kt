package com.example.po.ai.engine

import com.example.po.domain.model.Emotion
import javax.inject.Inject

class EmotionDetector @Inject constructor() {
    fun detect(text: String): Emotion {
        val lowerText = text.lowercase()
        return when {
            listOf("sad", "cry", "alone", "tired").any { lowerText.contains(it) } -> Emotion.SAD
            listOf("angry", "hate", "frustrated").any { lowerText.contains(it) } -> Emotion.ANGRY
            listOf("anxious", "scared", "nervous").any { lowerText.contains(it) } -> Emotion.ANXIOUS
            listOf("happy", "great", "excited").any { lowerText.contains(it) } -> Emotion.HAPPY
            else -> Emotion.NEUTRAL
        }
    }
}
