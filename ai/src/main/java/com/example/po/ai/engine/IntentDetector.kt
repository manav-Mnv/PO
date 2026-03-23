package com.example.po.ai.engine

import com.example.po.domain.model.Intent
import javax.inject.Inject

class IntentDetector @Inject constructor() {
    fun detect(text: String): Intent {
        val lowerText = text.lowercase()
        return when {
            listOf("hi", "hello", "hey").any { lowerText.contains(it) } -> Intent.GREETING
            listOf("bye", "later", "goodbye").any { lowerText.contains(it) } -> Intent.FAREWELL
            lowerText.contains("?") -> Intent.QUESTION
            listOf("help", "don't know", "what should").any { lowerText.contains(it) } -> Intent.SUPPORT
            listOf("feel", "feeling", "i am", "i'm").any { lowerText.contains(it) } -> Intent.VENTING
            else -> Intent.VENTING
        }
    }
}
