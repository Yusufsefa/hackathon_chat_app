package com.yyusufsefa.hackathon_chat_app.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.yyusufsefa.hackathon_chat_app.MainActivity
import com.yyusufsefa.hackathon_chat_app.R
import com.yyusufsefa.hackathon_chat_app.data.model.ChatMessage
import com.yyusufsefa.hackathon_chat_app.data.model.User
import com.yyusufsefa.hackathon_chat_app.util.saveCurrentUserDeviceToken

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private var notification_to_Id: String? = null
    private var notification_name: String? = null

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val currentUser = FirebaseAuth.getInstance().currentUser
        Log.e("onMessageReceived: ", remoteMessage.data.toString())
        if (currentUser != null) {
            val remoteData = remoteMessage.data
            notification_to_Id = remoteData["fromId"]
            val chatMessage = ChatMessage(
                text = remoteData["text"],
                fromId = remoteData["fromId"],
                toId = remoteData["toId"],
                isVoice = remoteData["isVoice"].toBoolean(),
                voicePath = remoteData["voicePath"]
            )
            prepareDataAndShowNotification(chatMessage)
        }
    }

    private fun prepareDataAndShowNotification(chatMessage: ChatMessage) {
        val dbRef = Firebase.database.getReference("users").child(chatMessage.fromId.toString())
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        notification_name = user.name
                        val username = "${user.name} ${user.lastName}"
                        val message =
                            if (chatMessage.isVoice!!) "Voice Message" else chatMessage.text
                        showNotification("New Message", "$username: $message")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        saveCurrentUserDeviceToken(token)
    }

    private fun showNotification(title: String, body: String) {
        val bundle = bundleOf("notification_to_id" to notification_to_Id)
        bundle.putString("notification_name", notification_name)
        val pendingIntent = NavDeepLinkBuilder(this)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.chatFragment)
            .setArguments(bundle)
            .createPendingIntent()

        val channelId = getString(R.string.app_name)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_email)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(1234, notificationBuilder.build())
    }
}
