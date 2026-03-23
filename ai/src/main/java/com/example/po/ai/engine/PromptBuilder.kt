package com.example.po.ai.engine

class PromptBuilder {
    fun buildPrompt(message: String, persona: String): String {
        return "Persona: $persona\nUser: $message"
    }
}
