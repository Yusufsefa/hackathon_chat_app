package com.yyusufsefa.hackathon_chat_app.ui.auth.login

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.yyusufsefa.hackathon_chat_app.R
import com.yyusufsefa.hackathon_chat_app.common.BaseFragment
import com.yyusufsefa.hackathon_chat_app.databinding.FragmentLoginBinding
import com.yyusufsefa.hackathon_chat_app.util.validateAndDo

class LoginFragment : BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {

    private val viewModel: LoginViewModel by lazy {
        ViewModelProvider(this).get(LoginViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (FirebaseAuth.getInstance().currentUser != null) {
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
        }

        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        binding.btnLogin.setOnClickListener {
            validateAndDo(listOf(binding.txtEditEmail, binding.txtEditPassword)) {
                viewModel.login(
                    binding.txtEditEmail.text.toString(),
                    binding.txtEditPassword.text.toString()
                )
            }
        }

        trackLogin()
    }

    private fun trackLogin() {
        viewModel.isLogin.observe(viewLifecycleOwner) { isLogin ->
            if (isLogin) {
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            } else {
                Toast.makeText(requireActivity(), "Fail", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
