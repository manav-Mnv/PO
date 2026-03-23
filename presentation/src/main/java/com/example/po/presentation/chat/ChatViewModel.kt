package com.example.po.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.po.ai.engine.EmotionDetector
import com.example.po.ai.engine.IntentDetector
import com.example.po.data.local.dao.ChatMessageDao
import com.example.po.domain.model.ChatMessage
import com.example.po.domain.model.Sender
import com.example.po.response.ResponseEngine
import com.example.po.response.ResponseRequest
import com.example.po.response.ResponseStyle
import com.example.po.response.Emotion as AppEmotion
import com.example.po.response.Intent as AppIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val emotionDetector: EmotionDetector,
    private val intentDetector: IntentDetector,
    private val responseEngine: ResponseEngine,
    private val chatMessageDao: ChatMessageDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            chatMessageDao.getAllMessages().collect { entities ->
                val domainMessages = entities.map {
                    ChatMessage(it.id, it.text, it.sender, it.timestamp)
                }
                _uiState.update { it.copy(messages = domainMessages) }
            }
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            text = text,
            sender = Sender.USER,
            timestamp = System.currentTimeMillis()
        )

        viewModelScope.launch {
            // 1 & 7. Appends and persists user message
            val userEntity = com.example.po.data.local.entity.ChatMessageEntity(
                id = userMessage.id,
                text = userMessage.text,
                sender = userMessage.sender,
                timestamp = userMessage.timestamp
            )
            chatMessageDao.insertMessage(userEntity)

            // 2. Sets isTyping = true
            _uiState.update { it.copy(isTyping = true) }

            // 3. Runs EmotionDetector and IntentDetector
            val domainEmotion = emotionDetector.detect(text)
            val domainIntent = intentDetector.detect(text)

            val mappedEmotion = when(domainEmotion) {
                com.example.po.domain.model.Emotion.SAD -> AppEmotion.SAD
                com.example.po.domain.model.Emotion.HAPPY -> AppEmotion.HAPPY
                com.example.po.domain.model.Emotion.ANGRY -> AppEmotion.ANGRY
                com.example.po.domain.model.Emotion.ANXIOUS -> AppEmotion.ANXIOUS
                com.example.po.domain.model.Emotion.NEUTRAL -> AppEmotion.NEUTRAL
            }
            val mappedIntent = when(domainIntent) {
                com.example.po.domain.model.Intent.GREETING -> AppIntent.GREETING
                com.example.po.domain.model.Intent.VENTING -> AppIntent.SUPPORT
                com.example.po.domain.model.Intent.QUESTION -> AppIntent.QUESTION
                com.example.po.domain.model.Intent.SUPPORT -> AppIntent.SUPPORT
                com.example.po.domain.model.Intent.FAREWELL -> AppIntent.FAREWELL
            }

            // 4. Calls ResponseEngine.generate()
            val request = ResponseRequest(
                emotion = mappedEmotion,
                intent = mappedIntent,
                style = ResponseStyle.EMPATHETIC
            )
            val replyText = responseEngine.generate(request)

            // 5. Delay 600ms
            delay(600)

            // Appends/Persists AI reply
            val aiMessageId = UUID.randomUUID().toString()
            val aiEntity = com.example.po.data.local.entity.ChatMessageEntity(
                id = aiMessageId,
                text = replyText,
                sender = Sender.AI,
                timestamp = System.currentTimeMillis()
            )
            chatMessageDao.insertMessage(aiEntity)

            // 6. Sets isTyping = false
            _uiState.update { it.copy(isTyping = false) }
        }
    }
}
