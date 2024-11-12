package com.example.chat.contact

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.chat.R
import com.example.chat.util.ChatPerference
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ContactScreen(navController: NavHostController) {
    val contactViewModel: ContactViewModel = hiltViewModel()

    val contactList by contactViewModel.contactList.collectAsState(emptyList())
    val userList by contactViewModel.userList.collectAsState(emptyList())
    var isRefresh by remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefresh,
        onRefresh = {
            isRefresh = true
        }
    )
    LaunchedEffect(key1 = Unit) {
        if (isRefresh) {
            contactViewModel.contactList()
            contactViewModel.userList()
            isRefresh = false
        }
    }

    Column(modifier = Modifier) {
        val context = LocalContext.current
        BackHandler {
            (context as Activity).finish()
        }

        FloatingActionButton(onClick = {
            FirebaseAuth.getInstance().signOut()
            val result = ChatPerference(context).clear()
            if (result) {
                navController.navigate("loginScreen") {
                    popUpTo("loginScreen") { inclusive = false }
                }
            }
        }) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "logout")
        }
        LazyColumn {
            items(userList ?: emptyList()) { user ->
                ConstraintLayout(modifier = Modifier.clickable {
                    navController.navigate("chatScreen?name=${user.name}&uid=${user.UID}")
                }) {
                    val (profileImage, nameText, uidText, divider) = createRefs()
                    Image(
                        painter = painterResource(id = R.drawable.ic_person_24),
                        contentDescription = "Profile",
                        Modifier
                            .constrainAs(profileImage) {
                                top.linkTo(parent.top, 5.dp)
                                start.linkTo(parent.start, 5.dp)
                                bottom.linkTo(divider.top, 5.dp)
                            }
                            .clip(CircleShape)
                            .border(2.dp, Color.Gray, CircleShape)
                            .size(50.dp),

                        )
                    Text(text = user.name, modifier = Modifier.constrainAs(nameText) {
                        top.linkTo(profileImage.top)
                        start.linkTo(profileImage.end, 5.dp)
                        bottom.linkTo(uidText.top)
                    })
                    Text(
                        text = "${user.UID} ${user.address}",
                        modifier = Modifier.constrainAs(uidText) {
                            top.linkTo(nameText.bottom)
                            start.linkTo(nameText.start)
                            bottom.linkTo(divider.top)
                        })
                    Divider(
                        thickness = 1.dp,
                        color = Color.Gray,
                        modifier = Modifier.constrainAs(divider) {
                            top.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        })
                }
            }
        }
        Divider(thickness = 1.dp, color = Color.Gray)
        LazyColumn {
            items(contactList ?: emptyList()) { contact ->
                ConstraintLayout(modifier = Modifier) {
                    val (profile, name, number, divider) = createRefs()
                    Image(painter = painterResource(id = R.drawable.ic_person_24),
                        contentDescription = "profile", modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.Gray, CircleShape)
                            .constrainAs(profile) {
                                top.linkTo(parent.top, 5.dp)
                                start.linkTo(parent.start, 5.dp)
                                bottom.linkTo(divider.top, 5.dp)
                            })
                    Text(text = contact.name, modifier = Modifier.constrainAs(name) {
                        top.linkTo(profile.top)
                        start.linkTo(profile.end, 5.dp)
                        bottom.linkTo(number.top)
                    })
                    Text(text = contact.number, modifier = Modifier.constrainAs(number) {
                        top.linkTo(name.bottom)
                        start.linkTo(name.start)
                        bottom.linkTo(divider.top)
                    })
                    Divider(
                        thickness = 1.dp,
                        color = Color.Gray,
                        modifier = Modifier.constrainAs(divider) {
                            top.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        })
                }
            }
        }
        PullRefreshIndicator(
            refreshing = isRefresh,
            state = pullRefreshState,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun test() {
    Column(modifier = Modifier) {
        FloatingActionButton(onClick = { }) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "logout")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Previews() {
    test()
}