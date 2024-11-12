package com.example.chat.signup

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chat.util.ResponseStatus


@Composable
fun SignUp() {
    val context = LocalContext.current
    val signUpViewModel: SignUpViewModel = hiltViewModel()
    val signUpResponse by signUpViewModel.signUpResponse.collectAsState()
    val name by signUpViewModel.name
    val mobileNo by signUpViewModel.mobileNo
    val password by signUpViewModel.password
    val confirmPassword by signUpViewModel.confirmPassword
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (txtSignup, etName, etMobileNo, etPassword, etConfirmPassword, btnSignUp, loading) = createRefs()
        createVerticalChain(
            txtSignup,
            etName,
            etMobileNo,
            etPassword,
            etConfirmPassword,
            btnSignUp,
            chainStyle = ChainStyle.Packed
        )
            when(signUpResponse.responseStatus){
                ResponseStatus.SUCCESS -> {
                    Toast.makeText(context, "signUpResponse.message", Toast.LENGTH_SHORT).show()
                }
                ResponseStatus.ERROR -> {
                    Toast.makeText(context, "${signUpResponse.message}", Toast.LENGTH_SHORT).show()
                }
                ResponseStatus.LOADING -> {
                    if (signUpResponse.isLoading) {
                        ConstraintLayout(modifier = Modifier
                            .fillMaxSize()
                            .padding(5.dp)
                            .constrainAs(loading) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                            .background(color = Color.Transparent)
                        ) {
                            val (circularLoading) = createRefs()
                            CircularProgressIndicator(
                                modifier = Modifier.constrainAs(
                                    circularLoading
                                ) {
                                    top.linkTo(parent.top)
                                    bottom.linkTo(parent.bottom)
                                    start.linkTo(parent.start)
                                    end.linkTo(parent.end)
                                })
                        }
                    }
                }
            }

        Text(text = "SignUp", modifier = Modifier.constrainAs(txtSignup) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        })
        OutlinedTextField(value = name, onValueChange = { inputName: String ->
            signUpViewModel.onInputNameChanged(inputName)
        }, label = { Text(text = "Enter Name") }, modifier = Modifier.constrainAs(etName) {
            top.linkTo(txtSignup.bottom, 5.dp)
            start.linkTo(txtSignup.start)
            end.linkTo(txtSignup.end)
        })

        OutlinedTextField(value = mobileNo, onValueChange = { inputMobile: String ->
            signUpViewModel.onInputMobileNoChanged(inputMobile)
        }, label = { Text(text = "Enter Mobile no.") },
            modifier = Modifier.constrainAs(etMobileNo) {
                top.linkTo(etName.bottom, 5.dp)
                start.linkTo(etName.start)
                end.linkTo(etName.end)
            }
        )

        OutlinedTextField(value = password, onValueChange = { inputPassword: String ->
            signUpViewModel.onInputPasswordChanged(inputPassword)
        }, label = { Text(text = "Enter Password") },
            modifier = Modifier.constrainAs(etPassword) {
                top.linkTo(etMobileNo.bottom, 5.dp)
                start.linkTo(etMobileNo.start)
                end.linkTo(etMobileNo.end)
            }
        )

        OutlinedTextField(value = confirmPassword,
            onValueChange = { inputConfirmPassword: String ->
                signUpViewModel.onInputConfirmPasswordChanged(inputConfirmPassword)
            },
            label = { Text(text = "Enter Confirm Password") },
            modifier = Modifier.constrainAs(etConfirmPassword) {
                top.linkTo(etPassword.bottom, 5.dp)
                start.linkTo(etPassword.start)
                end.linkTo(etPassword.end)
            }
        )

        Button(onClick = {
            if (signUpViewModel.validation()) signUpViewModel.firebaseResgistration()
        }, modifier = Modifier.constrainAs(btnSignUp) {
            top.linkTo(etConfirmPassword.bottom, 5.dp)
            start.linkTo(etConfirmPassword.start)
            end.linkTo(etConfirmPassword.end)
        }) {
            Text("Sign Up")
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Preview() {
    SignUp()
}