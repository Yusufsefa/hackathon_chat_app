package com.yyusufsefa.hackathon_chat_app.ui.main.users

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
import com.yyusufsefa.hackathon_chat_app.data.model.User
import kotlinx.coroutines.launch

class UsersViewModel : ViewModel() {

    private val _userList: MutableLiveData<List<User>> = MutableLiveData()
    val userList: LiveData<List<User>> get() = _userList

    fun fetchUserList() {
        viewModelScope.launch {
            val dbRef = Firebase.database.getReference("users")
            dbRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userList = mutableListOf<User>()
                    for (item in snapshot.children) {
                        val user = item.getValue<User>()
                        if (user != null && user.userId != FirebaseAuth.getInstance().currentUser?.uid)
                            userList.add(user)
                    }
                    _userList.value = userList.toList()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }

    fun searchUserList(text: String) {
        viewModelScope.launch {
            if (text.isNotEmpty()) {
                val dbRef = Firebase.database.getReference("users")
                dbRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val userList = mutableListOf<User>()
                        for (item in snapshot.children) {
                            val user = item.getValue<User>()
                            if (user != null &&
                                user.userId != FirebaseAuth.getInstance().currentUser?.uid &&
                                userConditionBySearchText(user, text)
                            )
                                userList.add(user)
                        }
                        _userList.value = userList.toList()
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            } else {
                fetchUserList()
            }
        }
    }

    private fun userConditionBySearchText(user: User, searchText: String): Boolean {
        return user.name?.contains(
            searchText,
            ignoreCase = true
        ) ?: false || user.lastName?.contains(searchText, ignoreCase = true) ?: false
    }
}
