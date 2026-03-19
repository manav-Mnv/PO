package com.example.po.response

/**
 * Renders response templates by resolving `{placeholder}` tokens
 * against a supplied variable map.
 *
 * Performance notes:
 * - The placeholder regex is compiled once and reused across all calls.
 * - An LRU cache avoids re-rendering identical template+variables pairs.
 */
class TemplateEngine(
    private val templates: Map<String, Map<ResponseStyle, List<String>>>,
    private val cacheSize: Int = DEFAULT_CACHE_SIZE
) {
    private companion object {
        /** Matches `{word}` placeholders in templates. */
        val PLACEHOLDER_REGEX = """\{(\w+)}""".toRegex()

        const val DEFAULT_CACHE_SIZE = 128
    }

    /** Bounded LRU cache: oldest entry evicted when capacity is exceeded. */
    private val cache: LinkedHashMap<String, String> =
        object : LinkedHashMap<String, String>(cacheSize, 0.75f, true) {
            override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, String>?): Boolean =
                size > cacheSize
        }

    /**
     * Selects a template for [templateKey] + [style], applies [variables],
     * and returns the rendered string.
     *
     * Falls back to [ResponseStyle.CASUAL] when the requested style has
     * no entries.
     */
    fun render(
        templateKey: String,
        style: ResponseStyle,
        variables: Map<String, String>
    ): String {
        val cacheKey = buildCacheKey(templateKey, style, variables)
        cache[cacheKey]?.let { return it }

        val template = selectTemplate(templateKey, style)
        val rendered = applyVariables(template, variables)

        cache[cacheKey] = rendered
        return rendered
    }

    // ── private helpers ──────────────────────────────────────────────

    private fun selectTemplate(templateKey: String, style: ResponseStyle): String {
        val styleMap = templates[templateKey]
            ?: error("Missing templates for key: $templateKey")

        val candidates = styleMap[style] ?: styleMap[ResponseStyle.CASUAL]
            ?: error("No templates found for key: $templateKey and style: $style")

        return candidates.first()
    }

    private fun applyVariables(template: String, variables: Map<String, String>): String {
        val merged = mapOf("name" to "there") + variables
        return PLACEHOLDER_REGEX.replace(template) { match ->
            val key = match.groupValues[1]
            merged[key] ?: match.value   // leave unknown placeholders intact
        }
    }

    private fun buildCacheKey(
        templateKey: String,
        style: ResponseStyle,
        variables: Map<String, String>
    ): String = "$templateKey|$style|$variables"
}
