package com.yyusufsefa.hackathon_chat_app.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.yyusufsefa.hackathon_chat_app.data.model.ChatMessage
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val _messageList: MutableLiveData<List<ChatMessage>> = MutableLiveData()
    val myMessageList: LiveData<List<ChatMessage>> get() = _messageList

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
}
