package com.example.chat

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.chat.chatui.ChatScreen
//import com.example.chat.chatui.ChatScreen
import com.example.chat.contact.ContactScreen
import com.example.chat.login.LoginScreen
import com.example.chat.signup.SignUp

@Composable
fun NavHost(isLogin: Boolean) {
    val navController = rememberNavController()
    androidx.navigation.compose.NavHost(
        navController = navController,
        startDestination = if (isLogin) "contactScreen" else "loginScreen"
    ) {
        composable("loginScreen") {
            LoginScreen(navController)
        }

        composable("signUpScreen") {
            SignUp()
        }

        composable("contactScreen") {
            ContactScreen(navController)
        }

        composable("chatScreen?name={name}&uid={uid}", arguments = arrayListOf(navArgument("name") {
            type = NavType.StringType
            defaultValue = null
            nullable = true
        }, navArgument("uid") {
            type = NavType.LongType
        })) {
            val name = it.arguments?.getString("name")
            val uid = it.arguments?.getLong("uid")
            ChatScreen(navController,name,uid)
        }

    }

}