package com.yyusufsefa.hackathon_chat_app.ui.auth.signUp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.yyusufsefa.hackathon_chat_app.data.model.User
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val _isRegister: MutableLiveData<Boolean> = MutableLiveData()
    val isRegister: LiveData<Boolean> get() = _isRegister

    fun signUp(user: User, password: String) {
        viewModelScope.launch {
            firebaseAuth.createUserWithEmailAndPassword(user.email.toString(), password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) saveUserToDatabase(user)
                    _isRegister.value = task.isSuccessful
                }
        }
    }

    private fun saveUserToDatabase(user: User) {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val dbRef = Firebase.database.getReference("users").child(userId)
        user.userId = userId
        dbRef.setValue(user)
    }
}
