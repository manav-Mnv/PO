package com.example.po.response

/**
 * Centralized store of built-in response templates.
 *
 * Each key maps to a style→candidates lookup used by [TemplateEngine].
 * Keeping the data separate from wiring logic makes it easy to
 * swap, extend, or load templates from an external source later.
 */
object DefaultTemplates {

    val RULES: List<ResponseRule> = listOf(
        ResponseRule(intent = Intent.SUPPORT, emotion = Emotion.SAD, templateKey = "support_sad", priority = 100),
        ResponseRule(intent = Intent.COMPLAINT, emotion = Emotion.ANGRY, templateKey = "deescalate", priority = 100),
        ResponseRule(intent = Intent.GREETING, templateKey = "greeting", priority = 80),
        ResponseRule(intent = Intent.THANKS, templateKey = "thanks", priority = 80),
        ResponseRule(intent = Intent.QUESTION, templateKey = "question", priority = 70),
        ResponseRule(intent = Intent.FAREWELL, templateKey = "farewell", priority = 70),
        ResponseRule(intent = Intent.SUPPORT, templateKey = "support_default", priority = 60),
        ResponseRule(intent = Intent.COMPLAINT, templateKey = "complaint_default", priority = 60),
        ResponseRule(templateKey = "fallback", priority = 1)
    )

    val TEMPLATES: Map<String, Map<ResponseStyle, List<String>>> = mapOf(
        "greeting" to mapOf(
            ResponseStyle.FORMAL to listOf("Hello {name}, how may I assist you today?"),
            ResponseStyle.CASUAL to listOf("Hey {name}, what can I help with?"),
            ResponseStyle.EMPATHETIC to listOf("Hi {name}, I am here with you. How are you feeling?"),
            ResponseStyle.PLAYFUL to listOf("Yo {name}! What adventure are we tackling today?")
        ),
        "support_sad" to mapOf(
            ResponseStyle.FORMAL to listOf("I am sorry you are going through this, {name}. Let us work through it step by step."),
            ResponseStyle.CASUAL to listOf("I am really sorry, {name}. Want to talk through what happened?"),
            ResponseStyle.EMPATHETIC to listOf("That sounds heavy, {name}. I am with you, and we can take this one moment at a time."),
            ResponseStyle.PLAYFUL to listOf("I have got your back, {name}. We will turn this around together.")
        ),
        "deescalate" to mapOf(
            ResponseStyle.FORMAL to listOf("I understand your frustration, {name}. Let us address this calmly and directly."),
            ResponseStyle.CASUAL to listOf("I hear you, {name}. Let us fix this together."),
            ResponseStyle.EMPATHETIC to listOf("It makes sense you are upset, {name}. I am here to help sort this out."),
            ResponseStyle.PLAYFUL to listOf("Big feelings noted, {name}. Let us channel that energy into a fix.")
        ),
        "question" to mapOf(
            ResponseStyle.FORMAL to listOf("That is a good question, {name}. Here is the best answer I can provide."),
            ResponseStyle.CASUAL to listOf("Great question, {name}. Here is what I found."),
            ResponseStyle.EMPATHETIC to listOf("Thanks for asking, {name}. Let us walk through it clearly."),
            ResponseStyle.PLAYFUL to listOf("Love that question, {name}. Let us dive in.")
        ),
        "thanks" to mapOf(
            ResponseStyle.FORMAL to listOf("You are welcome, {name}. I am glad I could assist."),
            ResponseStyle.CASUAL to listOf("Anytime, {name}!"),
            ResponseStyle.EMPATHETIC to listOf("Happy to help, {name}. I appreciate you saying that."),
            ResponseStyle.PLAYFUL to listOf("You got it, {name}. Teamwork unlocked.")
        ),
        "support_default" to mapOf(
            ResponseStyle.FORMAL to listOf("I am here to support you, {name}. Please share as much detail as you are comfortable with."),
            ResponseStyle.CASUAL to listOf("I am here for you, {name}. Tell me what you need."),
            ResponseStyle.EMPATHETIC to listOf("You do not have to handle this alone, {name}. We can figure it out together."),
            ResponseStyle.PLAYFUL to listOf("Support mode activated, {name}. Let us tackle this together.")
        ),
        "complaint_default" to mapOf(
            ResponseStyle.FORMAL to listOf("Thank you for raising this, {name}. I will help address it."),
            ResponseStyle.CASUAL to listOf("Thanks for flagging it, {name}. Let us get it fixed."),
            ResponseStyle.EMPATHETIC to listOf("I get why this is frustrating, {name}. I will do my best to help."),
            ResponseStyle.PLAYFUL to listOf("Got it, {name}. Let us squash this issue.")
        ),
        "farewell" to mapOf(
            ResponseStyle.FORMAL to listOf("Goodbye, {name}. Please reach out anytime you need assistance."),
            ResponseStyle.CASUAL to listOf("See you later, {name}."),
            ResponseStyle.EMPATHETIC to listOf("Take care, {name}. I am here whenever you need me."),
            ResponseStyle.PLAYFUL to listOf("Catch you soon, {name}.")
        ),
        "fallback" to mapOf(
            ResponseStyle.FORMAL to listOf("I understand, {name}. Could you share a bit more context so I can help better?"),
            ResponseStyle.CASUAL to listOf("Got you, {name}. Can you share a little more detail?"),
            ResponseStyle.EMPATHETIC to listOf("I am listening, {name}. Tell me more so I can support you well."),
            ResponseStyle.PLAYFUL to listOf("I am tuned in, {name}. Give me a few more clues.")
        )
    )
}
