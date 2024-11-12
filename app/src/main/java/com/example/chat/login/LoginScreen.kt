package com.example.chat.login

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.chat.api.response.AccessTokenResponse
import com.example.chat.chatui.ChatScreen
import com.example.chat.util.ChatPerference
import com.example.chat.util.Response
import com.example.chat.util.ResponseStatus
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(), contentAlignment = Alignment.Center
    ) {
        val loginViewModel: LoginViewModel = hiltViewModel()
        val loginResponse by loginViewModel.loginResponse.collectAsState()
        when (loginResponse.isLoading) {
            true -> {
                CircularProgressIndicator(modifier = Modifier)
            }

            false -> {
                LoginUi(loginViewModel, loginResponse, navController)
            }

        }
    }

}

@Composable
fun LoginUi(
    loginViewModel: LoginViewModel,
    loginResponse: Response<AccessTokenResponse>,
    navController: NavHostController
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        val context = LocalContext.current
        val (loginText, fieldPhone, fieldPassword, btnLogin,txtSignUp) = createRefs()
        createVerticalChain(
            loginText,
            fieldPhone,
            fieldPassword,
            btnLogin,
            txtSignUp,
            chainStyle = ChainStyle.Packed
        )
        val phoneNo by loginViewModel.mobileNo
        val password by loginViewModel.password

        Text("Login", modifier = Modifier.constrainAs(loginText) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(fieldPhone.top)
        }, fontSize = 20.sp, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = phoneNo,
            onValueChange = {
                loginViewModel.onPhoneChange(it)
            },
            label = { Text("Enter Mobile Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.constrainAs(fieldPhone) {
                top.linkTo(loginText.bottom, 5.dp)
                start.linkTo(loginText.start)
                end.linkTo(loginText.end)
            })

        OutlinedTextField(value = password, onValueChange = {
            loginViewModel.onPassword(it)
        }, label = { Text("Enter Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.constrainAs(fieldPassword) {
                top.linkTo(fieldPhone.bottom, 5.dp)
                start.linkTo(loginText.start)
                end.linkTo(loginText.end)
            }
        )
        Button(onClick = {
            loginViewModel.logIn()
        }, modifier = Modifier.constrainAs(btnLogin) {
            top.linkTo(fieldPassword.bottom, 5.dp)
            start.linkTo(loginText.start)
            end.linkTo(loginText.end)
        }) {
            Text("Login")
        }
        Text("Sign Up",modifier = Modifier.constrainAs(txtSignUp) {
            top.linkTo(btnLogin.bottom, 5.dp)
            start.linkTo(loginText.start)
            end.linkTo(loginText.end)
        }.clickable {
            navController.navigate("signUpScreen")
        })
        when (loginResponse.responseStatus) {
            ResponseStatus.SUCCESS -> {
                val isTokenSave = loginResponse.data?.user?.token?.let {
                    ChatPerference(context).storeAcceesToken(
                        it
                    )
                }
                if (isTokenSave == true) navController.navigate("contactScreen") {
                    popUpTo("loginScreen") { inclusive = true }
                } else {
                    Toast.makeText(context, "Token not save successfully", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            ResponseStatus.ERROR -> {
                Toast.makeText(context, "${loginResponse.message}", Toast.LENGTH_SHORT)
                    .show()
            }

            ResponseStatus.LOADING -> {

            }
        }
    }
}


