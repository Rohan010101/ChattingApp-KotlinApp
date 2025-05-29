package com.example.ktorandroidchat

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ktorandroidchat.presentation.chat.ChatScreen
import com.example.ktorandroidchat.presentation.username.UsernameScreen
import com.example.ktorandroidchat.ui.theme.KtorAndroidChatTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KtorAndroidChatTheme {

                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "username_screen"
                ) {
                    composable(route = "username_screen"){
                        UsernameScreen(onNavigate = navController::navigate)
                    }
                    composable(
                        route = "chat_screen/{username}",
                        arguments = listOf(
                            navArgument(name = "username") {
                                type = NavType.StringType
                                nullable = true
                            }
                        )
                    ){
                        Log.d("Main Activity", "ChatScreen is about to be executed")
                        val username = it.arguments?.getString("username")
                        Log.d("Main Activity", "USERNAME: $username")
                        ChatScreen(username = username)
                    }
                }
            }
        }
    }
}