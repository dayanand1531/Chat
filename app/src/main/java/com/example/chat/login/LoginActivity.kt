package com.example.chat.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.chat.ChatApplication
import com.example.chat.R
import com.example.chat.contact.ContactSyncActivity
import com.example.chat.databinding.ActivityLoginBinding
import com.example.chat.signup.SignUpActivity
import com.example.chat.util.ChatPerference
import com.example.chat.util.ResponseStatus
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private val loginViewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = FirebaseAuth.getInstance()
        tokenObserver()
        binding.btnLogin.setOnClickListener {
            logIn()
        }
        binding.tvSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            getResult.launch(intent)
        }

    }

    private fun tokenObserver() {
        loginViewModel.loginResponse.observe(this) {
            when (it.responseStatus) {
                ResponseStatus.SUCCESS -> {
                    val isSave =
                        it.data?.user?.let { loginUser ->
                            val pref = ChatPerference(this)
                            pref.storeAcceesToken(loginUser.token)
                            pref.storeUId(loginUser.uid.toLong())
                        }
                    if (isSave == true) {
                        (applicationContext as ChatApplication).initializeMesibo()
                        startActivity(Intent(this, ContactSyncActivity::class.java))
                    }
                    binding.progressLayout.progressLayout.visibility = View.GONE
                }

                ResponseStatus.LOADING -> {
                    binding.progressLayout.progressLayout.visibility = View.VISIBLE
                }

                ResponseStatus.ERROR -> {
                    binding.progressLayout.progressLayout.visibility = View.GONE
                    Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private val getResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val resultData = data?.getStringExtra("result_key")
                Toast.makeText(this, "Result: $resultData", Toast.LENGTH_LONG).show()
            }
        }

    private fun logIn() {
        val mobileNumber = binding.etPhone.text.trim().toString()
        val password = binding.etPassword.text.trim().toString()
        auth.signInWithEmailAndPassword(mobileNumber + "@gmail.com", password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(this, "Sign In Successful!", Toast.LENGTH_SHORT).show()
                    loginViewModel.getAcceesToken(mobileNumber, getString(R.string.appToken))
                } else {
                    Toast.makeText(
                        this,
                        "Sign In Failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

    }

    override fun onStart() {
        super.onStart()
        val accessToken = ChatPerference(this).getacceesToken()
        if (accessToken != null) {
            (applicationContext as ChatApplication).initializeMesibo()
            startActivity(Intent(this, ContactSyncActivity::class.java))
            finish()
        }
    }
}