package com.yyusufsefa.hackathon_chat_app.ui.auth.signUp

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.yyusufsefa.hackathon_chat_app.R
import com.yyusufsefa.hackathon_chat_app.common.BaseFragment
import com.yyusufsefa.hackathon_chat_app.databinding.FragmentSignUpBinding

class SignUpFragment : BaseFragment<FragmentSignUpBinding>(R.layout.fragment_sign_up) {

    private val viewModel: SignUpViewModel by lazy {
        ViewModelProvider(this).get(SignUpViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSave.setOnClickListener {
            viewModel.signUp(
                binding.txtRegisterEmail.text.toString(),
                binding.txtRegisterPassword.text.toString()
            )
        }

        viewModel.isRegister.observe(viewLifecycleOwner, {
            if (it)
                Log.e("deneme", it.toString())
        })
    }
}
