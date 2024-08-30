Chat app Using SDK
1. Tools and Architecture
•	Programming language Kotlin
•	Android studio
•	3.MVVM Architecture
2. SDK and library
•	Mesibo: For message and real-time chat 
•	Glide: Display image on view
•	Firebase for authentication with Mesibo authentication
•	Retrofit, https -> Get user token from Mesibo server, log URL
•	Hilt Dependency Injection -> Provide dependency required in our project
•	Google map -> Share and show location.
•	Timber -> Log printing
•	Room Database-> Store data on  local device
•	Androidx camera literary for image captor
Flow how to use and screen
1. Signup -> Users sign up using basic details like name, mobile no, and password. Sign up on Firebase once successful then get an access token from Mesibo and set Mesibo profile. Then return to the login.
2. Login Screen -> Click on  ”Signup” to move to the signup screen. Entering login input like number and password. Verify whether the Firebase user exists or not. If exists then call the access token generated API for generate access token and save in share preference. If user already login in the app launch on contact activity.
3. Contact Screen: Contact does not load automatically when the user logs in. Users need to pull to refresh and load the contacts after granting contact permissions.
•	App user displays top in contact list and then contact.
•	Initiate the communication with App user who is in your contact.
4. Chat Screen -> 
•	To user name display on top
•	User can send text, and images by gallery or captor image from the camera, and share location using bottom icon button.
•	On clicking the “Location icon” new activity launches. In this, on clicking the share location button user sends the current location. If the user clicks on any point in the map then the pointed location send.
•	Click on the image icon launch galley and after picking the image show it in a dialog box. Here can send the image by the send button or cancel.
•	Clicking on the camera icon to launch the camera. Clicked the image then showed it in a dialog to send it. 

Known issue: After login conversation not loading.


