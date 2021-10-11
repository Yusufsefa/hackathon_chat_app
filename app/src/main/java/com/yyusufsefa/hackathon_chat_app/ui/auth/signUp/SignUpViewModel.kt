package com.yyusufsefa.hackathon_chat_app.ui.auth.signUp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val _isRegister: MutableLiveData<Boolean> = MutableLiveData()
    val isRegister: LiveData<Boolean> get() = _isRegister

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    _isRegister.value = task.isSuccessful
                }
        }
    }
}
