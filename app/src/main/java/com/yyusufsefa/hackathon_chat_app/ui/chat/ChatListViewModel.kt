package com.yyusufsefa.hackathon_chat_app.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.yyusufsefa.hackathon_chat_app.data.model.ChatList
import com.yyusufsefa.hackathon_chat_app.data.model.User
import kotlinx.coroutines.launch

class ChatListViewModel : ViewModel() {

    private val _userList: MutableLiveData<List<User>> = MutableLiveData()
    val userList: LiveData<List<User>> get() = _userList

    fun fetchChatList() {
        viewModelScope.launch {
            val dbRefChatList = Firebase.database.getReference("chat_list")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
            dbRefChatList.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val chatList = mutableListOf<ChatList>()
                    for (item in snapshot.children){
                        val chatListItem = item.getValue<ChatList>()
                        if (chatListItem != null) {
                            chatList.add(chatListItem)
                        }
                    }
                    // GET ALL USER AND MATCH CHAT LIST
                    val dbRefUsers = Firebase.database.getReference("users")
                    dbRefUsers.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val userList = mutableListOf<User>()
                            for (item in snapshot.children) {
                                val user = item.getValue<User>()

                                for (chatListItem in chatList){
                                    if (user != null && user.userId.equals(chatListItem.userId))
                                        userList.add(user)
                                }
                            }
                            _userList.value = userList.toList()
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }
}
