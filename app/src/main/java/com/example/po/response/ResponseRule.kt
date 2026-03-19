package com.example.po.response

/**
 * A single rule that maps an [Intent]/[Emotion] combination to a template key.
 *
 * Rules with higher [priority] are evaluated first.
 * `null` fields act as wildcards (match any value).
 */
data class ResponseRule(
    val intent: Intent? = null,
    val emotion: Emotion? = null,
    val templateKey: String,
    val priority: Int = 0
) {
    fun matches(request: ResponseRequest): Boolean {
        val intentMatch = intent == null || intent == request.intent
        val emotionMatch = emotion == null || emotion == request.emotion
        return intentMatch && emotionMatch
    }
}
