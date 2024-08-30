package com.example.chat.chatui

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chat.ChatApplication
import com.example.chat.databinding.ActivityMainBinding
import com.example.chat.map.MapsActivity
import com.example.chat.signup.CameraActivity
import com.example.chat.util.ChatPerference
import com.mesibo.api.Mesibo
import com.mesibo.api.MesiboMessage
import com.mesibo.api.MesiboPresence
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.ExecutorService
import javax.inject.Inject


@AndroidEntryPoint
class ChatActivity : AppCompatActivity(), Mesibo.MessageListener, Mesibo.ConnectionListener,
    Mesibo.PresenceListener {
    @Inject
    lateinit var chatRepository: ChatRepository
    private val chatViewModel: ChatViewModel by viewModels()
    var imageUri: Uri? = null
    var loggedInUser: Long = 0
    private val REQUEST_CODE_PICK_IMAGE = 1000
    private val PERMISSION_REQUEST_CODE = 1001
    private lateinit var cameraExecutor: ExecutorService

    lateinit var name: String
    private lateinit var uid: String
    private lateinit var username: String
    private lateinit var pickLocationLauncher: ActivityResultLauncher<Intent>
    private lateinit var pickCapterImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var binding: ActivityMainBinding


    private lateinit var chatAdapter: ChatAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = DataBindingUtil.setContentView(this, com.example.chat.R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(com.example.chat.R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if (Mesibo.getInstance() == null) {
            (applicationContext as ChatApplication).initializeMesibo()
            Timber.d("Mesibo initialized")
        }
        loggedInUser = ChatPerference(this).authId()
        cameraExecutor = java.util.concurrent.Executors.newSingleThreadExecutor()

        /*binding.main.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            binding.main.getWindowVisibleDisplayFrame(rect)
            val screenHeight = binding.main.height
            val keypadHeight = screenHeight - rect.bottom
            binding.rvChatList.setPadding(0, 0, 0, keypadHeight)
        }*/

        name = intent.getStringExtra("name")!!
        uid = intent.getStringExtra("id")!!
        username = intent.getStringExtra("username")!!
        supportActionBar?.apply {
            title = name
            setDisplayShowTitleEnabled(true)
            show()
        }

        Mesibo.addListener(this)

        binding.btnSend.setOnClickListener {
            val message = binding.etChat.text.trim().toString()
            if (message.isNotEmpty()) chatViewModel.sendMsg(username, message)
            else Toast.makeText(this, "Please enter input", Toast.LENGTH_SHORT).show()
            binding.etChat.text.clear()
        }

        binding.ivSelect.setOnClickListener {
            checkAndRequestPermissions()
        }

        binding.ivLocation.setOnClickListener {
            pickLocationLauncher.launch(Intent(this, MapsActivity::class.java))
        }
        setAdapter()
        binding.ivCamera.setOnClickListener {
            pickCapterImageLauncher.launch(Intent(this, CameraActivity::class.java))
        }

        pickLocationLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val lat = result.data?.getDoubleExtra("selected_lat", 0.0)
                    val lng = result.data?.getDoubleExtra("selected_lng", 0.0)
                    chatViewModel.sendLocation(lat, lng, username)
                } else if (result.resultCode == Activity.RESULT_OK) {
                    val uri = result.data?.getStringExtra("url")
                    uri?.let {
                        val uriPath = Uri.parse(it)
                        Dialog.showImageDialog(uriPath, this, username)
                    }
                }

            }

        pickCapterImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val uri = result.data?.getStringExtra("url")
                    uri?.let {
                        val uriPath = Uri.parse(it)
                        Dialog.showImageDialog(uriPath, this, username)
                    }
                }
            }
    }

    private fun setAdapter() {
        lifecycleScope.launch {
            chatViewModel.messageList(loggedInUser, uid.toLong()).observe(this@ChatActivity) {
                val layoutManager = binding.rvChatList.layoutManager as LinearLayoutManager
                layoutManager.stackFromEnd = true
                val lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()
                chatAdapter = ChatAdapter(it, this@ChatActivity)
                binding.rvChatList.adapter = chatAdapter
                if (it.isNotEmpty()) binding.rvChatList.smoothScrollToPosition(it.size - 1)
                if (lastVisibleItemPosition == it.size - 2) {
                    binding.rvChatList.smoothScrollToPosition(it.size - 1)
                }
            }
        }
    }

    override fun Mesibo_onMessage(message: MesiboMessage) {
        Timber.d("outgoing: ${message.isOutgoing}   incoming : ${message.isIncoming}")
        if (::chatRepository.isInitialized) {
            chatRepository.storeMessage(
                message,
                loggedInUser,
                uid.toLong()
            )
            Timber.d("chat repository initialized")
        } else {
            Timber.d("chat repository not initialized")
        }

        when (message.status) {
            Mesibo.MSGSTATUS_OUTBOX -> {
                Timber.d("Mesibo_onMessage: -------------> Outbox")
            }

            Mesibo.MSGSTATUS_SENT -> {
                Timber.d("Mesibo_onMessage: -------------------> Send")
            }

            Mesibo.MSGSTATUS_DELIVERED -> {
                Timber.d("Mesibo_onMessage: ------------------------> Delivered")
            }

            Mesibo.MSGSTATUS_READ -> {
                Timber.d("Mesibo_onMessage: -----------------------> Read")
            }

            Mesibo.MSGSTATUS_RECEIVEDNEW -> {
                Timber.d("Mesibo_onMessage: -----------------------------> New Message")
            }

            Mesibo.MSGSTATUS_RECEIVEDREAD -> {
                Timber.d("Mesibo_onMessage: ------------------------------> Read Recevied")
            }
        }

    }

    override fun Mesibo_onMessageStatus(message: MesiboMessage) {
        if (::chatRepository.isInitialized) {
            chatRepository.storeMessage(
                message,
                loggedInUser,
                uid.toLong()
            )
            Timber.d("chat repository initialized")
        } else {
            Timber.d("chat repository not initialized")
        }
        Timber.d("Mesibo_Status: ${message.status}")
    }

    override fun Mesibo_onMessageUpdate(message: MesiboMessage) {
        if (::chatRepository.isInitialized) {
            chatRepository.storeMessage(
                message,
                loggedInUser,
                uid.toLong()
            )
            Timber.d("chat repository initialized")
        } else {
            Timber.d("chat repository not initialized")
        }
        Timber.d("Mesibo_update: ${message.status}")
    }

    override fun Mesibo_onConnectionStatus(status: Int) {
        when (status) {
            Mesibo.STATUS_UNKNOWN -> {
                Timber.d("status unknown")
                // Handle logout scenario
            }

            Mesibo.STATUS_ONLINE -> {
                Timber.d("online")
                // Handle login success, e.g., update UI or start sync
            }

            Mesibo.STATUS_OFFLINE -> {
                Timber.d("offline")
                // Handle network error, e.g., show error message
            }

            Mesibo.STATUS_SIGNOUT -> {
                Timber.d("Logged out")
                // Handle authentication error, e.g., prompt for re-login
            }

            Mesibo.STATUS_AUTHFAIL -> {
                Timber.d("Authentication error")
                // Handle client is logged in scenario
            }

            Mesibo.STATUS_STOPPED -> Timber.d("stopped")
            Mesibo.STATUS_CONNECTING -> Timber.d("connecting")
            Mesibo.STATUS_CONNECTFAILURE -> Timber.d("connect failure")
            Mesibo.STATUS_ONPREMISEERROR -> Timber.d("on premise error")
            Mesibo.STATUS_SUSPENDED -> Timber.d("status suspended")

            else -> {
                Timber.d("Unknown status: $status")
                // Handle unknown or unexpected statuses
            }
        }
    }

    private fun checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES),
                    PERMISSION_REQUEST_CODE
                )
            } else {
                openGallery()
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_CODE
                )
            } else {
                openGallery()
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                Toast.makeText(this, "Permission required to access images", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK) {
            imageUri = data?.data
            if (imageUri != null) {

                Dialog.showImageDialog(imageUri, this, username)
                imageUri = null
            }
        }
    }


    override fun Mesibo_onPresence(p0: MesiboPresence) {

    }

    override fun Mesibo_onPresenceRequest(p0: MesiboPresence) {

    }

}