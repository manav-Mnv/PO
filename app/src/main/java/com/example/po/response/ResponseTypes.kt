package com.example.po.response

enum class Emotion {
    HAPPY,
    SAD,
    ANGRY,
    ANXIOUS,
    NEUTRAL,
    EXCITED
}

enum class Intent {
    GREETING,
    QUESTION,
    COMPLAINT,
    SUPPORT,
    THANKS,
    FAREWELL
}

enum class ResponseStyle {
    FORMAL,
    CASUAL,
    EMPATHETIC,
    PLAYFUL
}

data class ResponseRequest(
    val emotion: Emotion,
    val intent: Intent,
    val style: ResponseStyle = ResponseStyle.CASUAL,
    val variables: Map<String, String> = emptyMap()
)
