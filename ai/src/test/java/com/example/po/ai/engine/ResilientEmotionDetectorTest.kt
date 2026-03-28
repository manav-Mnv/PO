package com.example.po.ai.engine

import android.content.Context
import com.example.po.domain.model.Emotion
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for ResilientEmotionDetector.
 *
 * Uses MockK to stub the TFLite interpreter so tests run on JVM
 * without needing a device or .tflite file.
 *
 * Run with: ./gradlew :ai:test
 */
class ResilientEmotionDetectorTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = mockk(relaxed = true)
    }

    @Test
    fun `blank text returns NEUTRAL`() {
        val detector = buildDetectorWithLogits(FloatArray(6) { 0f })
        assertEquals(Emotion.NEUTRAL, detector.detect(""))
        assertEquals(Emotion.NEUTRAL, detector.detect("   "))
    }

    @Test
    fun `label index 1 joy maps to HAPPY`() {
        val logits = FloatArray(6).apply { this[1] = 5f }
        val result = buildDetectorWithLogits(logits).detect("I am so happy today")
        assertEquals(Emotion.HAPPY, result)
    }

    @Test
    fun `label index 0 sadness maps to SAD`() {
        val logits = FloatArray(6).apply { this[0] = 5f }
        val result = buildDetectorWithLogits(logits).detect("I feel really down")
        assertEquals(Emotion.SAD, result)
    }

    @Test
    fun `label index 4 fear maps to ANXIOUS`() {
        val logits = FloatArray(6).apply { this[4] = 5f }
        val result = buildDetectorWithLogits(logits).detect("I am so scared")
        assertEquals(Emotion.ANXIOUS, result)
    }

    @Test
    fun `label index 3 anger maps to ANGRY`() {
        val logits = FloatArray(6).apply { this[3] = 5f }
        val result = buildDetectorWithLogits(logits).detect("This makes me furious")
        assertEquals(Emotion.ANGRY, result)
    }

    @Test
    fun `interpreter exception falls back to NEUTRAL`() {
        // Detector that throws on inference
        val detector = buildDetectorThatThrows()
        assertEquals(Emotion.NEUTRAL, detector.detect("anything"))
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun buildDetectorWithLogits(logits: FloatArray): ResilientEmotionDetector {
        // Subclass to bypass TFLite initialization in unit tests
        return object : ResilientEmotionDetector(context) {
            override fun runInferenceForTest(
                inputIds: IntArray,
                attentionMask: IntArray,
            ): FloatArray = logits
        }
    }

    private fun buildDetectorThatThrows(): ResilientEmotionDetector {
        return object : ResilientEmotionDetector(context) {
            override fun runInferenceForTest(
                inputIds: IntArray,
                attentionMask: IntArray,
            ): FloatArray = throw RuntimeException("Simulated TFLite failure")
        }
    }
}
