package com.example.po.presentation.chat

data class ChatUiState(
    val messages: List<String> = emptyList(),
    val isLoading: Boolean = false
)
