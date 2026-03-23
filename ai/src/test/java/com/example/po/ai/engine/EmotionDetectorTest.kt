package com.example.po.ai.engine

import com.example.po.domain.model.Emotion
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class EmotionDetectorTest {
    private lateinit var emotionDetector: EmotionDetector

    @Before
    fun setup() {
        emotionDetector = EmotionDetector()
    }

    @Test
    fun testSadKeyword() {
        val emotion = emotionDetector.detect("I am so tired today")
        assertEquals(Emotion.SAD, emotion)
    }

    @Test
    fun testAngryKeyword() {
        val emotion = emotionDetector.detect("I hate everything right now")
        assertEquals(Emotion.ANGRY, emotion)
    }

    @Test
    fun testAnxiousKeyword() {
        val emotion = emotionDetector.detect("Feeling really nervous about the test")
        assertEquals(Emotion.ANXIOUS, emotion)
    }

    @Test
    fun testHappyKeyword() {
        val emotion = emotionDetector.detect("This is great!")
        assertEquals(Emotion.HAPPY, emotion)
    }

    @Test
    fun testNeutralDefault() {
        val emotion = emotionDetector.detect("I had a sandwich for lunch.")
        assertEquals(Emotion.NEUTRAL, emotion)
    }
}
