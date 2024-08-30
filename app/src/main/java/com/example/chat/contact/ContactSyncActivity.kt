package com.example.chat.contact

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.chat.ChatApplication
import com.example.chat.R
import com.example.chat.chatui.ChatActivity
import com.example.chat.chatui.MessageHandler
import com.example.chat.databinding.ActivityContactSyncBinding
import com.example.chat.login.LoginActivity
import com.example.chat.util.ChatPerference
import com.google.firebase.auth.FirebaseAuth
import com.mesibo.api.Mesibo
import com.mesibo.api.MesiboProfile
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class ContactSyncActivity : AppCompatActivity() {
    private val REQUEST_CODE_READ_CONTACTS = 1
    private lateinit var binding: ActivityContactSyncBinding
    var loggedUserId:Long = 0
    private val contactSyncViewModel: ContactViewModel by viewModels()
    private lateinit var contactAdapter: ContactAdapter
    private lateinit var userAdapter: UserAdapter
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contact_sync)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        loggedUserId = ChatPerference(this).authId()
        auth = FirebaseAuth.getInstance()
        if (Mesibo.getInstance() == null) {
            (applicationContext as ChatApplication).initializeMesibo()
        }
        contactAdapter()
        binding.swipeRefresh.setOnRefreshListener {
            checkPermissions()
            // contactAdapter.notifyDataSetChanged()
            binding.swipeRefresh.isRefreshing = false
        }
        binding.fabSignOut.setOnClickListener {
            signOut()
        }
    }

    private fun signOut() {
        auth.signOut()
        val result = ChatPerference(this).clear()
        if (result) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            contactSyncViewModel.clearDb()
            finish()
        }
        readMessageFromDB()
    }

    private fun readMessageFromDB(){
        val profile = Mesibo.getProfile(loggedUserId)
        Mesibo.setAppInForeground(this, 0, true)
        val mReadSession = profile.createReadSession(MessageHandler())
        mReadSession.enableReadReceipt(true)
        mReadSession.read(100)
    }

    private fun contactAdapter() {
        lifecycleScope.launch {
            contactSyncViewModel.getContactList().observe(this@ContactSyncActivity) { contactList ->
                val sortedContact = contactList.sortedBy { it.name }.distinct()
                contactAdapter = ContactAdapter()
                binding.rvContact.adapter = contactAdapter
                contactAdapter.submitList(sortedContact)
            }
            contactSyncViewModel.getUserList().observe(this@ContactSyncActivity) {
                val userSortList = it.filter { loggedUserId.toString() != it.UID }.sortedBy { it.name }.distinct()

                userAdapter = UserAdapter { user ->
                    val intent = Intent(this@ContactSyncActivity, ChatActivity::class.java)
                    intent.putExtra("name", user.name)
                    intent.putExtra("id", user.UID)
                    intent.putExtra("username", user.user)
                    startActivity(intent)
                }
                binding.rvUser.adapter = userAdapter
                userAdapter.submitList(userSortList)
            }
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.READ_CONTACTS
                )
            ) {
                showPermissionExplanationDialog()
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(android.Manifest.permission.READ_CONTACTS),
                    REQUEST_CODE_READ_CONTACTS
                )
            }
        } else {
            contactSyncViewModel.fetchContacts()
            // contactAdapter.notifyDataSetChanged()
        }
    }

    private fun showPermissionExplanationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("This app needs access to your contacts to display them. Please grant the permission.")
            .setPositiveButton("OK") { _, _ ->
                ActivityCompat.requestPermissions(
                    this, arrayOf(android.Manifest.permission.READ_CONTACTS),
                    REQUEST_CODE_READ_CONTACTS
                )
            }
            .setNegativeButton("Cancel") { _, _ ->
                // Handle the case where the user chooses to not grant the permission
                Toast.makeText(
                    this,
                    "Permission denied. Unable to fetch contacts.",
                    Toast.LENGTH_LONG
                ).show()
            }
            .create()
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_READ_CONTACTS && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            contactSyncViewModel.fetchContacts()
            // contactAdapter.notifyDataSetChanged()
        } else {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.READ_CONTACTS
                )
            ) {
                // Permission was denied and user has selected "Don't ask again"
                showPermissionSettingsDialog()
            } else {
                // Permission was denied, but "Don't ask again" was not selected
                Toast.makeText(
                    this,
                    "Permission denied. Unable to fetch contacts.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showPermissionSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("You have denied the permission permanently. Please go to settings and enable it manually.")
            .setPositiveButton("Settings") { _, _ ->
                // Redirect to app settings
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { _, _ ->
                Toast.makeText(this, "Unable to fetch contacts.", Toast.LENGTH_LONG).show()
            }
            .create()
            .show()
    }
}