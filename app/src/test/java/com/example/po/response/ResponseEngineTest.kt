package com.example.po.response

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ResponseEngineTest {

    private val engine = DefaultResponseEngineFactory.create()

    @Test
    fun `uses high-priority emotion plus intent rule`() {
        val output = engine.generate(
            ResponseRequest(
                emotion = Emotion.SAD,
                intent = Intent.SUPPORT,
                style = ResponseStyle.EMPATHETIC,
                variables = mapOf("name" to "Ava")
            )
        )

        assertTrue(output.contains("Ava"))
        assertTrue(output.contains("one moment at a time"))
    }

    @Test
    fun `supports multiple styles for same rule`() {
        val formal = engine.generate(
            ResponseRequest(
                emotion = Emotion.NEUTRAL,
                intent = Intent.GREETING,
                style = ResponseStyle.FORMAL,
                variables = mapOf("name" to "Sam")
            )
        )

        val casual = engine.generate(
            ResponseRequest(
                emotion = Emotion.NEUTRAL,
                intent = Intent.GREETING,
                style = ResponseStyle.CASUAL,
                variables = mapOf("name" to "Sam")
            )
        )

        assertNotEquals(formal, casual)
        assertTrue(formal.startsWith("Hello"))
        assertTrue(casual.startsWith("Hey"))
    }

    @Test
    fun `falls back to intent-default rule when no emotion-specific rule exists`() {
        val output = engine.generate(
            ResponseRequest(
                emotion = Emotion.EXCITED,
                intent = Intent.COMPLAINT,
                style = ResponseStyle.CASUAL,
                variables = mapOf("name" to "Riya")
            )
        )

        assertEquals("Thanks for flagging it, Riya. Let us get it fixed.", output)
    }
}
