package com.example.chat.contact

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.chat.R

@Composable
fun ContactScreen(navController: NavHostController) {
    Column(modifier = Modifier) {
        val contactViewModel: ContactViewModel = hiltViewModel()
        contactViewModel.contactList()
        contactViewModel.userList()
        val contactList by contactViewModel.contactList.collectAsState(emptyList())
        val userList by contactViewModel.userList.collectAsState(emptyList())
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

    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Previews() {
   // ContactScreen(navController, name, uid)
}