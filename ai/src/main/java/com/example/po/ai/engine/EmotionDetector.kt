package com.example.po.ai.engine

import com.example.po.domain.model.Emotion

/**
 * Contract for any emotion detection implementation.
 *
 * Both the legacy keyword detector and the new ML detector implement this.
 * Hilt swaps the binding in AiModule — nothing else in the codebase changes.
 */
interface EmotionDetector {
    fun detect(text: String): Emotion
}
