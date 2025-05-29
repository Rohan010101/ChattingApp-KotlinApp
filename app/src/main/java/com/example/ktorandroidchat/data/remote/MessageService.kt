package com.example.ktorandroidchat.data.remote

import com.example.ktorandroidchat.domain.model.Message

interface MessageService {

    suspend fun getAllMessages(): List<Message>

    companion object {
//        const val BASE_URL = "https://192.168.0.116:8080"
//        const val BASE_URL = "ws://10.0.2.2:8080"  // Special address for Android Emulator
        const val BASE_URL = "http://192.168.0.103:8080"
    }

    sealed class Endpoints(val url: String) {
        object GetAllMessages: Endpoints("$BASE_URL/messages")
    }
}