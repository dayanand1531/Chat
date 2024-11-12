package com.example.chat.signup

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SignUp() {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    val signUpViewModel: SignUpViewModel = hiltViewModel()
    val name by signUpViewModel.name
    val mobileNo by signUpViewModel.mobileNo
    val password by signUpViewModel.password
    val confirmPassword by signUpViewModel.confirmPassword
    Column {
        Text(text = "SignUp")
        OutlinedTextField(value = name, onValueChange = { inputName: String ->
            signUpViewModel.onInputNameChanged(inputName)
        }, label = { Text(text = "Enter Name") })

        OutlinedTextField(value = mobileNo, onValueChange = { inputMobile: String ->
            signUpViewModel.onInputMobileNoChanged(inputMobile)
        }, label = { Text(text = "Enter Mobile no.") })

        OutlinedTextField(value = password, onValueChange = { inputPassword: String ->
            signUpViewModel.onInputPasswordChanged(inputPassword)
        }, label = { Text(text = "Enter Password") })

        OutlinedTextField(value = confirmPassword, onValueChange = { inputConfirmPassword: String ->
            signUpViewModel.onInputConfirmPasswordChanged(inputConfirmPassword)
        }, label = { Text(text = "Enter Confirm Password") })

        Button(onClick = {
            if (signUpViewModel.Validation()) firebaseResgistration(
                mobileNo,
                password,
                auth,
                context,
                signUpViewModel
            )
        }) {
            Text("Sign Up")
        }
    }
}

private fun firebaseResgistration(
    mobile: String,
    password: String,
    auth: FirebaseAuth,
    context: Context,
    signUpViewModel: SignUpViewModel
) {
    auth.createUserWithEmailAndPassword("$mobile@gmail.com", password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
               // val user = auth.currentUser
                signUpViewModel.getAcceesToken(mobile, com.example.chat.BuildConfig.appKey)
            } else {
                Toast.makeText(
                    context,
                    "Sign Up Failed: ${task.exception?.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Preview() {
    SignUp()
}