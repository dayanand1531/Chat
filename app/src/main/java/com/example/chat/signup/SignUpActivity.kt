package com.example.chat.signup

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.chat.ChatApplication
import com.example.chat.R
import com.example.chat.contact.ContactSyncActivity
import com.example.chat.databinding.ActivitySignUpBinding
import com.example.chat.login.LoginActivity
import com.example.chat.login.LoginViewModel
import com.example.chat.util.ChatPerference
import com.example.chat.util.ResponseStatus
import com.google.firebase.auth.FirebaseAuth
import com.mesibo.api.Mesibo
import com.mesibo.api.MesiboProfile
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private val loginViewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = FirebaseAuth.getInstance()
        binding.btnSignUp.setOnClickListener {
            validation()
        }
        tokenObserver()
    }

    private fun validation() {
        val name = binding.etName.text.trim().toString()
        val mobile = binding.etPhone.text.trim().toString()
        val password = binding.etPassword.text.trim().toString()
        val confirmPassword = binding.etConfirmPassword.text.trim().toString()

        if (isValidName(name) && isValidMobile(mobile) && isValidPassword(password) && password == confirmPassword) {
            Toast.makeText(this, "Validation Successful!", Toast.LENGTH_SHORT).show()
            firebaseResgistration(mobile, password)
        } else {
            var errorMessage = "Invalid Name, Mobile Number or Password"
            if (!isValidMobile(mobile)) {
                errorMessage = "Invalid mobile no."
            } else if (password != confirmPassword) {
                errorMessage = "Passwords do not match"
            }
            Toast.makeText(this, "$errorMessage", Toast.LENGTH_SHORT).show()

        }
    }

    private fun tokenObserver() {
        loginViewModel.loginResponse.observe(this) {
            when (it.responseStatus) {
                ResponseStatus.SUCCESS -> {
                    val pref = ChatPerference(this)
                    val isSave =
                        it.data?.user?.let { loginUser ->

                            pref.storeAcceesToken(loginUser.token)
                        }
                    if (isSave == true) {
                        (applicationContext as ChatApplication).initializeMesibo()
                        val profile: MesiboProfile? = Mesibo.getSelfProfile()
                        val isSet = profile?.let {
                            it.name = binding.etName.text.trim().toString()
                            it.save()
                            ChatPerference(this).clear()
                        }

                        if (isSet == true) {
                            pref.clear()
                            finish()
                        } else {
                            Timber.d("profiel not set.")
                        }
                    }
                    binding.progressBar.progressLayout.visibility = View.GONE

                }

                ResponseStatus.LOADING -> {
                    binding.progressBar.progressLayout.visibility = View.VISIBLE
                }

                ResponseStatus.ERROR -> {
                    binding.progressBar.progressLayout.visibility = View.GONE
                    Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun firebaseResgistration(mobile: String, password: String) {
        auth.createUserWithEmailAndPassword(mobile + "@gmail.com", password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val user = auth.currentUser
                    loginViewModel.getAcceesToken(mobile, getString(R.string.appToken))

                } else {

                    Toast.makeText(
                        this,
                        "Sign Up Failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
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