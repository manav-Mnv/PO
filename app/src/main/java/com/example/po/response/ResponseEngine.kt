package com.example.po.response

/**
 * Contract for any component that can produce a response string
 * from a [ResponseRequest]. Enables DI and easy mocking in tests.
 */
interface ResponseGenerator {
    fun generate(request: ResponseRequest): String
}

/**
 * Rule-based response engine.
 *
 * Evaluates [rules] in descending priority order, picks the first
 * match, and delegates rendering to [templateEngine].
 */
class ResponseEngine(
    private val rules: List<ResponseRule>,
    private val templateEngine: TemplateEngine
) : ResponseGenerator {

    override fun generate(request: ResponseRequest): String {
        val winningRule = rules
            .sortedByDescending { it.priority }
            .firstOrNull { it.matches(request) }
            ?: error("No response rule matched request: $request")

        return templateEngine.render(
            templateKey = winningRule.templateKey,
            style = request.style,
            variables = request.variables
        )
    }
}
