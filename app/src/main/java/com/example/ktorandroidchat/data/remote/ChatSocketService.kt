package com.example.ktorandroidchat.data.remote

import com.example.ktorandroidchat.domain.model.Message
import com.example.ktorandroidchat.util.Resource
import kotlinx.coroutines.flow.Flow

interface ChatSocketService {

    // START SESSION
    suspend fun initSession(
        username: String
    ): Resource<Unit>

    // SEND MESSAGE
    suspend fun sendMessage(message: String)

    // SEE MESSAGES
    fun observeMessages(): Flow<Message>

    // CLOSE SESSION
    suspend fun closeSession()

    companion object {
//        const val BASE_URL = "ws://192.168.0.116:8080"
//        const val BASE_URL = "ws://10.0.2.2:8080"  // Special address for Android Emulator
        const val BASE_URL = "ws://192.168.0.103:8080"
    }

    sealed class Endpoints(val url: String) {
        object ChatSocket: Endpoints("$BASE_URL/chat-socket")
    }
}