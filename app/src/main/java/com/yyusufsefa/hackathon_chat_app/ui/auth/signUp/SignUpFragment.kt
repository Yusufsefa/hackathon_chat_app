package com.yyusufsefa.hackathon_chat_app.ui.auth.signUp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.yyusufsefa.hackathon_chat_app.R
import com.yyusufsefa.hackathon_chat_app.common.BaseFragment
import com.yyusufsefa.hackathon_chat_app.data.model.User
import com.yyusufsefa.hackathon_chat_app.databinding.FragmentSignUpBinding
import com.yyusufsefa.hackathon_chat_app.util.validateAndDo

class SignUpFragment : BaseFragment<FragmentSignUpBinding>(R.layout.fragment_sign_up) {

    private val viewModel: SignUpViewModel by lazy {
        ViewModelProvider(this).get(SignUpViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSave.setOnClickListener {
            validateAndDo(
                listOf(
                    binding.txtRegisterEmail,
                    binding.txtRegisterName,
                    binding.txtRegisterLastname,
                    binding.txtRegisterPassword
                )
            ) {
                val user = User(
                    binding.txtRegisterEmail.text.toString(),
                    binding.txtRegisterName.text.toString(),
                    binding.txtRegisterLastname.text.toString()
                )
                viewModel.signUp(
                    user,
                    binding.txtRegisterPassword.text.toString()
                )
            }

        }

        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }

        viewModel.isRegister.observe(viewLifecycleOwner, {
            if (it) {
                Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
            } else {
                Toast.makeText(requireContext(), "Fail", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
