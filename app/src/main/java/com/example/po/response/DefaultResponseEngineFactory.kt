package com.example.po.response

/**
 * Factory that assembles a production-ready [ResponseEngine]
 * using [DefaultTemplates] data.
 */
object DefaultResponseEngineFactory {

    fun create(): ResponseEngine = ResponseEngine(
        rules = DefaultTemplates.RULES,
        templateEngine = TemplateEngine(DefaultTemplates.TEMPLATES)
    )
}
