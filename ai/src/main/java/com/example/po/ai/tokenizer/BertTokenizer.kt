package com.example.po.ai.tokenizer

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Minimal WordPiece tokenizer that mirrors DistilBertTokenizerFast.
 * Loads vocab.txt from assets/models/vocab.txt.
 *
 * Produces token IDs compatible with the exported emotion_classifier.tflite.
 */
class BertTokenizer(context: Context) {

    private val vocab: Map<String, Int>
    private val unkId: Int
    private val clsId: Int
    private val sepId: Int
    private val padId: Int = 0

    companion object {
        private const val VOCAB_PATH   = "models/vocab.txt"
        private const val MAX_LENGTH   = 64
        private const val TOKEN_UNK    = "[UNK]"
        private const val TOKEN_CLS    = "[CLS]"
        private const val TOKEN_SEP    = "[SEP]"
        private const val TOKEN_PAD    = "[PAD]"
    }

    init {
        val map = mutableMapOf<String, Int>()
        try {
            context.assets.open(VOCAB_PATH).use { stream ->
                BufferedReader(InputStreamReader(stream)).forEachIndexed { idx, line ->
                    map[line.trim()] = idx
                }
            }
        } catch (e: Exception) {
            // Safe fallback for tests
        }
        vocab  = map
        unkId  = map[TOKEN_UNK] ?: 100
        clsId  = map[TOKEN_CLS] ?: 101
        sepId  = map[TOKEN_SEP] ?: 102
    }

    // ── Public API ────────────────────────────────────────────────────────────

    data class Encoding(
        val inputIds:     IntArray,
        val attentionMask: IntArray,
    )

    fun encode(text: String): Encoding {
        val tokens = tokenize(text.lowercase().trim())

        // [CLS] + tokens + [SEP], truncated to MAX_LENGTH
        val ids = mutableListOf(clsId)
        for (tok in tokens) {
            if (ids.size >= MAX_LENGTH - 1) break
            ids.add(vocab[tok] ?: unkId)
        }
        ids.add(sepId)

        val inputIds      = IntArray(MAX_LENGTH) { padId }
        val attentionMask = IntArray(MAX_LENGTH) { 0 }

        ids.forEachIndexed { i, id ->
            inputIds[i]      = id
            attentionMask[i] = 1
        }

        return Encoding(inputIds, attentionMask)
    }

    // ── WordPiece tokenization ────────────────────────────────────────────────

    private fun tokenize(text: String): List<String> {
        val result = mutableListOf<String>()
        // Split on whitespace and punctuation
        val words = text.split(Regex("\\s+|(?=[^a-zA-Z0-9])|(?<=[^a-zA-Z0-9])"))
            .filter { it.isNotBlank() }

        for (word in words) {
            result.addAll(wordPiece(word))
        }
        return result
    }

    private fun wordPiece(word: String): List<String> {
        if (word in vocab) return listOf(word)

        val subTokens = mutableListOf<String>()
        var start = 0
        var isBad = false

        while (start < word.length) {
            var end = word.length
            var curSubStr: String? = null

            while (start < end) {
                val substr = if (start == 0) word.substring(start, end)
                             else "##${word.substring(start, end)}"
                if (substr in vocab) {
                    curSubStr = substr
                    break
                }
                end--
            }

            if (curSubStr == null) {
                isBad = true
                break
            }
            subTokens.add(curSubStr)
            start = end
        }

        return if (isBad) listOf(TOKEN_UNK) else subTokens
    }
}
