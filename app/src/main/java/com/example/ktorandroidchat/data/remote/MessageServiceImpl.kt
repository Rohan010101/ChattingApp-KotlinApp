package com.example.ktorandroidchat.data.remote

import android.util.Log
import com.example.ktorandroidchat.data.remote.dto.MessageDto
import com.example.ktorandroidchat.domain.model.Message
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class MessageServiceImpl(
    private val client: HttpClient
): MessageService {

    override suspend fun getAllMessages(): List<Message> {
        Log.d("MessageServiceImpl", "getAllMessages is executed")
        return try {
            Log.d("MessageServiceImpl", "Try in getAllMessages is executed")
//            client.get<List<MessageDto>>(MessageService.Endpoints.GetAllMessages.url)

            // get: Sends a GET Request asynchronously to the given URL; a JSON data is responded by the server
            // body: deserializes the raw JSON response into a List<MessageDto> format
            // map: converts each MessageDto into Message object

            Log.d("MessageServiceImpl", "Fetching messages from URL: ${MessageService.Endpoints.GetAllMessages.url}")
            client.get(MessageService.Endpoints.GetAllMessages.url).body<List<MessageDto>>()
                .map { it.toMessage() }
        } catch (e: Exception) {
            Log.d("MessageServiceImpl", "Catch in getAllMessages is executed")
            emptyList()
        }
    }
}