package com.example.ktorandroidchat.data.remote

import android.util.Log
import com.example.ktorandroidchat.data.remote.dto.MessageDto
import com.example.ktorandroidchat.domain.model.Message
import com.example.ktorandroidchat.util.Resource
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.url
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json


class ChatSocketServiceImpl(
    private val client: HttpClient
): ChatSocketService {

    private var socket: WebSocketSession? = null


    // START SESSION
    override suspend fun initSession(username: String): Resource<Unit> {
        Log.d("ChatSocketServiceImpl","initSession is executed")
        return  try {
            Log.d("ChatSocketServiceImpl","Try in initSession is executed")
            socket = client.webSocketSession {
                Log.d("ChatSocketServiceImpl","URL: ${ChatSocketService.Endpoints.ChatSocket.url}?username=$username")
                url("${ChatSocketService.Endpoints.ChatSocket.url}?username=$username")
            }
            if(socket?.isActive == true) {
                Log.d("ChatSocketServiceImpl","Socket is active")
                Resource.Success(Unit)
            } else {
                Log.d("ChatSocketServiceImpl","Socket is inactive")
                Resource.Error("Couldn't establish a connection.")
            }
        } catch (e: Exception) {
            Log.d("ChatSocketServiceImpl","Catch in initSession is executed")
            e.printStackTrace()
            Resource.Error(e.localizedMessage ?: "Unknown Error")
        }
    }

    // SEND MESSAGE
    override suspend fun sendMessage(message: String) {
        Log.d("ChatSocketServiceImpl", "sendMessage is executed, Message: $message")
        try {
            Log.d("ChatSocketServiceImpl", "Try in sendMessage is executed")
            socket?.send(Frame.Text(message))
        } catch (e: Exception) {
            Log.d("ChatSocketServiceImpl", "Catch in sendMessage is executed")
            e.printStackTrace()
        }
    }

    // SEE MESSAGES
    override fun observeMessages(): Flow<Message> {
        Log.d("ChatSocketServiceImpl", "observeMessages is executed")
        return try {
            Log.d("ChatSocketServiceImpl", "Try in observeMessages is executed")
            socket?.incoming
                ?.receiveAsFlow()
                ?.filter { it is Frame.Text }
                ?.map {
                    Log.d("ChatSocketServiceImpl", "Map executed => Not Null")
                    val json = (it as? Frame.Text)?.readText() ?: ""
                    val messageDto = Json.decodeFromString<MessageDto>(json)
                    messageDto.toMessage()
                } ?: flow {
                Log.d("ChatSocketServiceImpl", "Map not executed => Null => Empty Flow")
            }
        } catch (e: Exception) {
            Log.d("ChatSocketServiceImpl", "Catch in observeMessages is executed")
            e.printStackTrace()
            flow {  }
        }
    }


    // CLOSE SESSION
    override suspend fun closeSession() {
        Log.d("ChatSocketServiceImpl", "closeSession is executed")
        socket?.close()
    }
}