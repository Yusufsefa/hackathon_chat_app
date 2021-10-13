package com.yyusufsefa.hackathon_chat_app.ui.chat

import android.media.MediaRecorder
import android.net.Uri
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
import kotlinx.coroutines.launch
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
        }
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
