package com.example.ktorandroidchat.presentation.chat

import com.example.ktorandroidchat.domain.model.Message

data class ChatState(
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false
)