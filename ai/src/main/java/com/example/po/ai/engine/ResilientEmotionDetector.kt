package com.example.po.ai.engine

import android.content.Context
import android.util.Log
import com.example.po.ai.tokenizer.BertTokenizer
import com.example.po.domain.model.Emotion
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import javax.inject.Inject
import javax.inject.Singleton

/**
 * On-device emotion classifier powered by fine-tuned DistilBERT → TFLite.
 *
 * Replaces the keyword-based EmotionDetector entirely.
 * Implements the same EmotionDetector interface so zero ViewModel changes needed.
 *
 * Model input:
 *   - input_ids:      [1 × 64]  INT32
 *   - attention_mask: [1 × 64]  INT32
 *
 * Model output:
 *   - logits:         [1 × 6]   FLOAT32
 *     index → label: 0=sadness, 1=joy, 2=love, 3=anger, 4=fear, 5=surprise
 */
@Singleton
// 'open' to allow subclassing in tests
open class ResilientEmotionDetector @Inject constructor(
    private val context: Context,
) : EmotionDetector {

    companion object {
        private const val TAG          = "ResilientEmotionDetector"
        private const val MODEL_PATH   = "models/emotion_classifier.tflite"
        private const val MAX_LENGTH   = 64
        private const val NUM_LABELS   = 6
        private const val NUM_THREADS  = 2
    }

    // Fine-grained label index → Android Emotion enum
    private val labelMap = mapOf(
        0 to Emotion.SAD,
        1 to Emotion.HAPPY,
        2 to Emotion.HAPPY,    // love → HAPPY
        3 to Emotion.ANGRY,
        4 to Emotion.ANXIOUS,
        5 to Emotion.NEUTRAL,  // surprise → NEUTRAL
    )

    private val tokenizer: BertTokenizer by lazy { BertTokenizer(context) }

    private val interpreter: Interpreter by lazy {
        val options = Interpreter.Options().apply {
            numThreads = NUM_THREADS
            useNNAPI   = false   // keep inference deterministic; enable for perf if needed
        }
        Interpreter(loadModelBuffer(), options).also {
            Log.d(TAG, "TFLite interpreter ready")
        }
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Detect emotion from raw user text.
     * Falls back to [Emotion.NEUTRAL] on any error so the chat loop never breaks.
     */
    override fun detect(text: String): Emotion {
        if (text.isBlank()) return Emotion.NEUTRAL

        return try {
            val encoding = tokenizer.encode(text)
            val logits   = runInferenceForTest(encoding.inputIds, encoding.attentionMask)
            val predIdx  = logits.indices.maxByOrNull { logits[it] } ?: 5
            val emotion  = labelMap[predIdx] ?: Emotion.NEUTRAL

            Log.d(TAG, "text='${text.take(40)}' → label=$predIdx → $emotion  logits=${logits.toList()}")
            emotion
        } catch (e: Exception) {
            Log.e(TAG, "Inference failed, falling back to NEUTRAL", e)
            Emotion.NEUTRAL
        }
    }

    /**
     * Batch detection — useful for mood analysis over a session.
     */
    fun detectBatch(messages: List<String>): List<Emotion> =
        messages.map { detect(it) }

    /**
     * Returns raw confidence scores (softmax applied) for all 6 classes.
     * Useful for the personalization engine to weight uncertain predictions.
     */
    fun detectWithConfidence(text: String): Map<Emotion, Float> {
        if (text.isBlank()) return mapOf(Emotion.NEUTRAL to 1f)

        return try {
            val encoding = tokenizer.encode(text)
            val logits   = runInferenceForTest(encoding.inputIds, encoding.attentionMask)
            val probs    = softmax(logits)

            val result   = mutableMapOf<Emotion, Float>()
            probs.forEachIndexed { idx, prob ->
                val emotion = labelMap[idx] ?: Emotion.NEUTRAL
                result[emotion] = (result[emotion] ?: 0f) + prob
            }
            result
        } catch (e: Exception) {
            Log.e(TAG, "Confidence inference failed", e)
            mapOf(Emotion.NEUTRAL to 1f)
        }
    }

    // ── Inference ─────────────────────────────────────────────────────────────

    // Changed to 'open' protected so tests can override it
    protected open fun runInferenceForTest(inputIds: IntArray, attentionMask: IntArray): FloatArray {
        val inputIdsBuf  = intArrayToByteBuffer(inputIds)
        val attMaskBuf   = intArrayToByteBuffer(attentionMask)
        val outputBuffer = Array(1) { FloatArray(NUM_LABELS) }

        interpreter.runForMultipleInputsOutputs(
            arrayOf(inputIdsBuf, attMaskBuf),
            mapOf(0 to outputBuffer),
        )

        return outputBuffer[0]
    }

    private fun intArrayToByteBuffer(arr: IntArray): ByteBuffer {
        val buf = ByteBuffer
            .allocateDirect(arr.size * Int.SIZE_BYTES)
            .order(ByteOrder.nativeOrder())
        arr.forEach { buf.putInt(it) }
        buf.rewind()
        return buf
    }

    private fun softmax(logits: FloatArray): FloatArray {
        val max  = logits.max() ?: 0f
        val exps = logits.map { Math.exp((it - max).toDouble()).toFloat() }
        val sum  = exps.sum()
        return exps.map { it / sum }.toFloatArray()
    }

    // ── Model loading ─────────────────────────────────────────────────────────

    private fun loadModelBuffer(): ByteBuffer {
        val assetFd     = context.assets.openFd(MODEL_PATH)
        val inputStream = FileInputStream(assetFd.fileDescriptor)
        val channel     = inputStream.channel
        return channel.map(
            FileChannel.MapMode.READ_ONLY,
            assetFd.startOffset,
            assetFd.declaredLength,
        )
    }

    fun close() {
        interpreter.close()
    }
}
