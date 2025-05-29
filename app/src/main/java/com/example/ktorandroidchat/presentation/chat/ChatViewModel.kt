package com.example.ktorandroidchat.presentation.chat

import android.util.Log
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ktorandroidchat.data.remote.ChatSocketService
import com.example.ktorandroidchat.data.remote.MessageService
import com.example.ktorandroidchat.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messageService: MessageService,
    private val chatSocketService: ChatSocketService,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _messageText = mutableStateOf("")
    val messageText: State<String> = _messageText

    private val _state = mutableStateOf(ChatState())
    val state: State<ChatState> = _state

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()


    // CONNECT TO CHAT
    fun connectToChat() {
        Log.d("ChatViewModel","connectToChat is executed")
        getAllMessages()
        savedStateHandle.get<String>("username")?.let { username ->
            viewModelScope.launch {
                val result = chatSocketService.initSession(username)
                when(result) {
                    is Resource.Success -> {
                        Log.d("ChatViewModel","Resource.Success")
                        chatSocketService.observeMessages()
                            .onEach { message ->
                                val newList = state.value.messages.toMutableStateList().apply {
                                    add(0, message)
                                }
                                _state.value = state.value.copy(
                                    messages = newList
                                )
                            }.launchIn(viewModelScope)
                    }

                    is Resource.Error -> {
                        Log.d("ChatViewModel","Resource.Error")
                        _toastEvent.emit(result.message ?: "Unknown Error")
                    }
                }
            }
        }
    }


    // ON MESSAGE CHANGE
    fun onMessageChange(message: String) {
        Log.d("ChatViewModel","onMessageChange is executed")
        _messageText.value = message
    }


    // DISCONNECT
    fun disconnect() {
        Log.d("ChatViewModel","disconnect is executed")
        viewModelScope.launch {
            chatSocketService.closeSession()
        }
    }


    // GET ALL MESSAGES
    private fun getAllMessages() {
        Log.d("ChatViewModel", "getAllMessages is running")
        viewModelScope.launch {
            _state.value = state.value.copy(isLoading = true)
            val result = messageService.getAllMessages()
            Log.d("ChatViewModel", "Fetched messages: $result") // Add this
            _state.value = state.value.copy(
                messages = result,
                isLoading = false
            )
        }
    }



    // SEND MESSAGE
    fun sendMessage() {
        Log.d("ChatViewModel", "sendMessage is running")
        viewModelScope.launch {
            Log.d("ChatViewModel", "ViewModelScope in sendMessage is launched")
            if (messageText.value.isNotBlank()) {
                Log.d("ChatViewModel", "chatSocketService.sendMessage is executed. Message: ${messageText.value}")
                chatSocketService.sendMessage(messageText.value)
                _messageText.value = ""
            }
        }
    }

    // CLEAR
    override fun onCleared() {
        Log.d("ChatViewModel", "onCleared is running")
        super.onCleared()
        disconnect()
    }

}