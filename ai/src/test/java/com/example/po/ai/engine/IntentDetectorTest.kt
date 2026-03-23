package com.example.po.ai.engine

import com.example.po.domain.model.Intent
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class IntentDetectorTest {
    private lateinit var intentDetector: IntentDetector

    @Before
    fun setup() {
        intentDetector = IntentDetector()
    }

    @Test
    fun testGreetingKeyword() {
        val intent = intentDetector.detect("Hey there!")
        assertEquals(Intent.GREETING, intent)
    }

    @Test
    fun testFarewellKeyword() {
        val intent = intentDetector.detect("Goodbye, talk later")
        assertEquals(Intent.FAREWELL, intent)
    }

    @Test
    fun testQuestionKeyword() {
        val intent = intentDetector.detect("Why is this happening?")
        assertEquals(Intent.QUESTION, intent)
    }

    @Test
    fun testSupportKeyword() {
        val intent = intentDetector.detect("I really need help")
        assertEquals(Intent.SUPPORT, intent)
    }

    @Test
    fun testVentingDefault() {
        val intent = intentDetector.detect("I just want to rant.")
        assertEquals(Intent.VENTING, intent)
    }
}
