package com.example.chat.chatui

import android.widget.ImageView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.chat.R
import com.example.chat.api.model.entity.Message
import com.example.chat.util.ChatPerference
import com.example.chat.util.MessageType
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun ChatScreen(navController: NavHostController, name: String?, uid: Long?) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        val context = LocalContext.current
        val loggedInUid = ChatPerference(context).authId()
        val chatViewModel: ChatViewModel = hiltViewModel()
        uid?.let { chatViewModel.getMessageList(loggedInUid, it) }
        val messageList by chatViewModel.messageList.collectAsState(emptyList())

        val (nameText, messageListView, iconLocation, iconGallery, iconCamera, tfMessage, btnSend) = createRefs()

        LazyColumn(modifier = Modifier.constrainAs(messageListView) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(tfMessage.top, 50.dp)
        }) {
            items(messageList) { message ->
                when (message.messageType) {
                    MessageType.TEXT.value -> {
                        if (message.isSent) SendTextMessageView(message) else ReceviedTextMessageView(
                            message
                        )
                    }

                    MessageType.IMAGE.value -> {
                        if (message.isSent) SendImageView(message) else ReceviedImageView(message)
                    }

                    MessageType.LOCATION.value -> {
                        if (message.isSent) SendLocationView(message) else ReceviedLocationView(
                            message
                        )
                    }

                }

            }

        }
        Text(text = "${name ?: " "} $uid", modifier = Modifier.constrainAs(nameText) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
        })
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_location_on_24),
            tint = Color.Gray,
            contentDescription = "Location",
            modifier = Modifier.constrainAs(iconLocation) {
                top.linkTo(tfMessage.top)
                start.linkTo(parent.start)
                bottom.linkTo(tfMessage.bottom)
            }
        )
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_image_24),
            tint = Color.Gray,
            contentDescription = "Gallery",
            modifier = Modifier.constrainAs(iconGallery) {
                top.linkTo(iconLocation.top)
                start.linkTo(iconLocation.end, 5.dp)
            }
        )
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_camera_alt_24),
            contentDescription = "Camera",
            tint = Color.Gray,
            modifier = Modifier.constrainAs(iconCamera) {
                top.linkTo(iconLocation.top)
                start.linkTo(iconGallery.end)
                end.linkTo(tfMessage.start, 5.dp)
            }
        )
        OutlinedTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier
                .constrainAs(tfMessage) {
                    top.linkTo(iconCamera.top)
                    // start.linkTo(iconCamera.end)
                    bottom.linkTo(parent.bottom, 25.dp)
                    end.linkTo(btnSend.start, 5.dp)
                }
                .fillMaxWidth(0.5f)
        )
        Button(onClick = {}, modifier = Modifier.constrainAs(btnSend) {
            top.linkTo(tfMessage.top)
            end.linkTo(parent.end, 5.dp)
        }) {
            Text("Send")
        }
    }
}

@Composable
fun ReceviedImageView(message: Message) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val (receviedImage, textTime, iconGallery, iconCamera, tfMessage, btnSend) = createRefs()

        AndroidView(factory = { context ->
            ImageView(context).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
        },
            modifier = Modifier
                .constrainAs(receviedImage) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
                .fillMaxWidth(0.7f)
                .height(240.dp),
            update = {
                Glide.with(it.context).load(message.imageUrl)
                    .apply(RequestOptions.centerCropTransform()).into(it)
            }
        )
        Text(text = message.timestamp.toString(), modifier = Modifier.constrainAs(textTime) {
            top.linkTo(receviedImage.bottom)
            end.linkTo(receviedImage.end, 5.dp)
        })

    }
}

@Composable
fun SendImageView(message: Message) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val (receviedImage, textTime, iconGallery, iconCamera, tfMessage, btnSend) = createRefs()

        AndroidView(factory = { context ->
            ImageView(context).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
        },
            modifier = Modifier
                .constrainAs(receviedImage) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }
                .fillMaxWidth(0.7f)
                .height(240.dp),
            update = {
                Glide.with(it.context).load(message.imageUrl)
                    .apply(RequestOptions.centerCropTransform()).into(it)
            }
        )
        Text(text = message.timestamp.toString(), modifier = Modifier.constrainAs(textTime) {
            top.linkTo(receviedImage.bottom)
            end.linkTo(receviedImage.end, 5.dp)
        })

    }
}

@Composable
fun ReceviedTextMessageView(message: Message) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),

        ) {
        val (receviedMessage, textTime) = createRefs()

        message.message?.let {
            Text(
                text = it,
                modifier = Modifier.constrainAs(receviedMessage) {
                    top.linkTo(parent.top)
                    end.linkTo(textTime.start)
                },
                fontSize = TextUnit(18f, TextUnitType.Sp)
            )
        }
        Text(
            text = message.timestamp.toString(), modifier = Modifier.constrainAs(textTime) {
                top.linkTo(receviedMessage.bottom)
                end.linkTo(receviedMessage.end, 5.dp)
            },
            fontSize = TextUnit(14f, TextUnitType.Sp),
            color = Color.Gray
        )

    }
}

@Composable
fun SendTextMessageView(message: Message) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),

        ) {
        val (sendMessage, textTime) = createRefs()

        Text(
            text = message.message ?: "",
            modifier = Modifier.constrainAs(sendMessage) {
                top.linkTo(parent.top)
                end.linkTo(textTime.start)
            },
            fontSize = TextUnit(18f, TextUnitType.Sp)
        )
        Text(
            text = message.timestamp.toString(), modifier = Modifier.constrainAs(textTime) {
                top.linkTo(sendMessage.bottom)
                end.linkTo(parent.end, 5.dp)
            },
            fontSize = TextUnit(14f, TextUnitType.Sp),
            color = Color.Gray
        )
    }
}

@Composable
fun ReceviedLocationView(message: Message) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),

        ) {
        val (receviedMap, textTime) = createRefs()
        val singapore = LatLng(message.latitude, message.longitude) // Example coordinates
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(singapore, 10f)
        }
        GoogleMap(
            modifier = Modifier
                .width(240.dp)
                .height(200.dp)
                .constrainAs(receviedMap) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                },
            cameraPositionState = cameraPositionState,
        ) {
            Marker(
                state = com.google.maps.android.compose.rememberMarkerState(position = singapore),
            )
        }

        Text(
            text = message.timestamp.toString(), modifier = Modifier.constrainAs(textTime) {
                top.linkTo(receviedMap.bottom)
                end.linkTo(receviedMap.end, 5.dp)
            },
            fontSize = TextUnit(14f, TextUnitType.Sp),
            color = Color.Gray
        )
    }
}

@Composable
fun SendLocationView(message: com.example.chat.api.model.entity.Message) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),

        ) {
        val (sendMap, textTime, sendingStatus) = createRefs()
        val singapore = LatLng(message.latitude, message.longitude) // Example coordinates
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(singapore, 10f)
        }
        GoogleMap(
            modifier = Modifier
                .width(240.dp)
                .height(200.dp)
                .constrainAs(sendMap) {
                    top.linkTo(parent.top)
                    end.linkTo(textTime.end)
                },
            cameraPositionState = cameraPositionState,
        ) {
            Marker(
                state = com.google.maps.android.compose.rememberMarkerState(position = singapore),
            )
        }

        Text(
            text = message.timestamp.toString(), modifier = Modifier.constrainAs(textTime) {
                top.linkTo(sendMap.bottom)
                end.linkTo(sendingStatus.start, 5.dp)
            },
            fontSize = TextUnit(14f, TextUnitType.Sp),
            color = Color.Gray
        )
        Column(modifier = Modifier
            .size(30.dp)
            .constrainAs(sendingStatus) {
                top.linkTo(textTime.top)
                end.linkTo(parent.end, 5.dp)
            }) {
            when (message.messageStatus) {
                0 -> Icon(
                    imageVector = Icons.Filled.Refresh,
                    tint = Color.Gray,
                    contentDescription = "sending"
                )

                1 -> Icon(
                    imageVector = Icons.Filled.Check,
                    tint = Color.Gray,
                    contentDescription = "sent"
                )

                2 -> Icon(
                    imageVector = Icons.Filled.Check,
                    tint = Color.Blue,
                    contentDescription = "recevied"
                )

                3 -> Icon(
                    imageVector = Icons.Filled.AddCircle,
                    tint = Color.Gray,
                    contentDescription = "received"
                )

                128 -> Icon(
                    imageVector = Icons.Filled.Favorite,
                    tint = Color.Gray,
                    contentDescription = "fail"
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewChatScreen() {
    //ChatScreen(navController)
}