package com.yyusufsefa.hackathon_chat_app.ui.chat

import android.media.MediaRecorder
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.yyusufsefa.hackathon_chat_app.data.model.ChatMessage
import com.yyusufsefa.hackathon_chat_app.data.model.DeviceToken
import com.yyusufsefa.hackathon_chat_app.data.model.NotificationModel
import com.yyusufsefa.hackathon_chat_app.data.model.NotificationRequest
import com.yyusufsefa.hackathon_chat_app.notification.send.ApiClient
import com.yyusufsefa.hackathon_chat_app.notification.send.ApiInterface
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.IOException


class ChatViewModel : ViewModel() {

    private val _messageList: MutableLiveData<List<ChatMessage>> = MutableLiveData()
    val myMessageList: LiveData<List<ChatMessage>> get() = _messageList

    private val _isRecording: MutableLiveData<Boolean> = MutableLiveData()
    val isRecording: LiveData<Boolean> get() = _isRecording

    private var mRecorder: MediaRecorder? = null

    fun fetchMessage(currentUid: String, userId: String) {
        viewModelScope.launch {
            val ref = Firebase.database.getReference("/messages")
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val chatList = mutableListOf<ChatMessage>()
                    for (item in snapshot.children) {
                        val chat = item.getValue(ChatMessage::class.java)

                        if (chat?.toId.equals(currentUid) && chat?.fromId.equals(userId) || chat?.toId.equals(
                                userId
                            ) && chat?.fromId.equals(currentUid)
                        ) {
                            chatList.add(chat!!)
                        }
                    }

                    _messageList.value = chatList.toList()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }

    fun sendMessage(chatMessage: ChatMessage) {
        viewModelScope.launch {
            val reference = Firebase.database.getReference("/messages")
            reference.push().setValue(chatMessage)
            saveLatestChat(chatMessage)
            sendNotification(chatMessage)
        }
    }

    private fun sendNotification(chatMessage: ChatMessage) {
        val dbRef =
            Firebase.database.getReference("device_tokens").child(chatMessage.toId.toString())
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val deviceToken = snapshot.getValue(DeviceToken::class.java)
                    val notificationRequest = NotificationRequest(
                        notification = NotificationModel(
                            title = "New Message",
                            body = if (chatMessage.isVoice!!) "Voice Message" else chatMessage.text.toString()
                        ),
                        token = deviceToken?.token.toString(),
                        data = chatMessage
                    )
                    Log.e( "sendNotification: ", notificationRequest.toString())
                    val apiService = ApiClient.client.create(ApiInterface::class.java)
                    apiService.sendNotification(notificationRequest)
                        .enqueue(object : retrofit2.Callback<ResponseBody> {
                            override fun onResponse(
                                call: Call<ResponseBody>,
                                response: Response<ResponseBody>
                            ) {
                                Log.e( "sendNotification: ", response.message())
                            }

                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                Log.e( "sendNotification: ", t.message.toString())
                            }
                        })
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

    }

    private fun saveLatestChat(chatMessage: ChatMessage) {
        val chatListFromReference =
            Firebase.database.getReference("chat_list").child(chatMessage.fromId!!)
                .child(chatMessage.toId!!)
        chatListFromReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists())
                    chatListFromReference.child("userId").setValue(chatMessage.toId)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        val chatListToReference =
            Firebase.database.getReference("chat_list").child(chatMessage.toId!!)
                .child(chatMessage.fromId!!)
        chatListToReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists())
                    chatListToReference.child("userId").setValue(chatMessage.fromId)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun startRecording(mLocalFilePath: String) {
        mRecorder = MediaRecorder()
        mRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mRecorder?.setOutputFile(mLocalFilePath)
        mRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        try {
            mRecorder?.prepare()
        } catch (e: IOException) {
        }
        mRecorder?.start()
        _isRecording.value = true
    }

    fun stopRecording() {
        mRecorder?.stop()
        mRecorder?.release()
        mRecorder = null
        _isRecording.value = false
    }

    fun saveVoiceMessage(mLocalFilePath: String, firebaseFileName: String, receiverUserId: String) {
        uploadAudio(mLocalFilePath, firebaseFileName)
        sendMessage(
            ChatMessage(
                fromId = FirebaseAuth.getInstance().currentUser!!.uid,
                toId = receiverUserId,
                isVoice = true,
                voicePath = firebaseFileName
            )
        )
    }

    private fun uploadAudio(mLocalFilePath: String, firebaseFileName: String) {
        val mFirebaseStorage = FirebaseStorage.getInstance().reference
        val firebasePath: StorageReference =
            mFirebaseStorage.child("VoiceMessages").child("Audio").child(firebaseFileName)
        val localUri: Uri = Uri.fromFile(File(mLocalFilePath))
        firebasePath.putFile(localUri).addOnSuccessListener(
            OnSuccessListener<Any?> {
            }
        ).addOnFailureListener(
            OnFailureListener { e ->
            }
        )
    }
}
