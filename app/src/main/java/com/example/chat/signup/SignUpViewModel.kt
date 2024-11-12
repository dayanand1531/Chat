package com.example.chat.signup

import android.text.TextUtils
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chat.api.request.AccessTokenRequest
import com.example.chat.api.request.Token
import com.example.chat.api.request.User
import com.example.chat.api.response.AccessTokenResponse
import com.example.chat.login.LoginRepository
import com.example.chat.util.Response
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
   private val loginRepository: LoginRepository
) : ViewModel(
) {
    private var _loginResponse =
        MutableLiveData<Response<AccessTokenResponse>>()
    val loginResponse: LiveData<Response<AccessTokenResponse>>
        get() = _loginResponse

    private val _name = mutableStateOf("")
    val name: State<String> = _name
    private val _mobileNo = mutableStateOf("")
    val mobileNo: State<String> = _mobileNo
    private val _password = mutableStateOf("")
    val password: State<String> = _password
    private val _confirmPassword = mutableStateOf("")
    val confirmPassword: State<String> = _confirmPassword
    private val _signUpError = mutableStateOf("")
    val signUpError: State<String> = _signUpError

    fun onInputNameChanged(input: String) {
        _name.value = input
    }

    fun onInputMobileNoChanged(input: String) {
        _mobileNo.value = input
    }

    fun onInputPasswordChanged(input: String) {
        _password.value = input
    }

    fun onInputConfirmPasswordChanged(input: String) {
        _confirmPassword.value = input
    }

    internal fun getAcceesToken(number: String, appToken: String) {
        _loginResponse.postValue(Response.loading())
        viewModelScope.launch {
            val acceesTokenRequest = accessTokenRequest(number, appToken)
            val result = loginRepository.getAccessToken(acceesTokenRequest)
            if (result.isSuccessful) {
                if (result.body() != null) {
                    _loginResponse.postValue(Response.success(data = result.body()!!))
                } else _loginResponse.postValue(Response.error(result.message()))
            } else {
                _loginResponse.postValue(Response.error(result.message()))
            }
        }
    }

    fun Validation():Boolean {
        if (isValidName(name.value) && isValidMobile(mobileNo.value) && isValidPassword(password.value) && password.value == confirmPassword.value) {
                  return true
        } else {
            var errorMessage = "Invalid Name, Mobile Number or Password"
            if (!isValidMobile(mobileNo.value)) {
                errorMessage = "Invalid mobile no."
            } else if (password != confirmPassword) {
                errorMessage = "Passwords do not match"
            }
            _signUpError.value = errorMessage
            return false
        }
    }

    private fun accessTokenRequest(number: String, appToken: String): AccessTokenRequest {
        val token = Token(appid = "com.example.chat", expiry = 525600)
        val user = User(address = number, token)
        return AccessTokenRequest(
            op = "useradd",
            token = appToken,
            user = user
        )
    }

    private fun isValidName(name: String): Boolean {
        return !TextUtils.isEmpty(name) && name.length >= 2
    }

    private fun isValidMobile(mobile: String): Boolean {
        return !TextUtils.isEmpty(mobile) && mobile.length == 10 && mobile.matches(Regex("[6-9][0-9]{9}"))
    }

    // Validate Password (At least 8 characters, 1 uppercase, 1 lowercase, 1 digit, 1 special character)
    private fun isValidPassword(password: String): Boolean {
        val passwordPattern = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=]).{8,}$")
        return !TextUtils.isEmpty(password) && password.matches(passwordPattern)
    }

}