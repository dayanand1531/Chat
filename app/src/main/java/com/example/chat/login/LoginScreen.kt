package com.example.chat.login

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen() {
    Column(modifier = Modifier.padding(8.dp)) {
        val context = LocalContext.current
        val loginViewModel: LoginViewModel = hiltViewModel()
        val phoneNo by loginViewModel.mobileNo
        val password by loginViewModel.password
        Text("Login")

        OutlinedTextField(value = phoneNo, onValueChange = {
            loginViewModel.onPhoneChange(it)
        }, label = { Text("Enter Mobile Number") })

        OutlinedTextField(value = password, onValueChange = {
            loginViewModel.onPassword(it)
        }, label = { Text("Enter Mobile Number") })
        Button(onClick = {
            logIn(loginViewModel, context)
        }) {
            Text("Login")
        }
    }
}

private fun logIn(loginViewModel: LoginViewModel, context: Context) {
    val auth = FirebaseAuth.getInstance()
    val mobileNumber by loginViewModel.mobileNo
    val password by loginViewModel.password
    auth.signInWithEmailAndPassword("$mobileNumber@gmail.com", password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // val user = auth.currentUser
                loginViewModel.getAcceesToken(mobileNumber, com.example.chat.BuildConfig.appKey)
            } else {
                Toast.makeText(
                    context,
                    "Sign In Failed: ${task.exception?.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

}