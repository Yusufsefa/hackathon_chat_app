package com.yyusufsefa.hackathon_chat_app.ui.chat

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.ramo.sweetrecycleradapter.SweetRecyclerAdapter
import com.yyusufsefa.hackathon_chat_app.R
import com.yyusufsefa.hackathon_chat_app.common.BaseFragment
import com.yyusufsefa.hackathon_chat_app.data.model.ChatMessage
import com.yyusufsefa.hackathon_chat_app.databinding.FragmentChatBinding

class ChatFragment : BaseFragment<FragmentChatBinding>(R.layout.fragment_chat) {

    private val viewModel: ChatViewModel by lazy {
        ViewModelProvider(this).get(ChatViewModel::class.java)
    }

    private val sweetAdapter = SweetRecyclerAdapter<ChatMessage>()
    private var userId: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = arguments?.getString("userId")!!

        initRecyclerView()
        getMessages()

        binding.btnSend.setOnClickListener {
            viewModel.sendMessage(
                ChatMessage(
                    binding.txtChat.text.toString(),
                    FirebaseAuth.getInstance().currentUser?.uid,
                    userId
                )
            )
            binding.txtChat.text?.clear()
        }
    }

    private fun getMessages() {
        viewModel.fetchMessage(FirebaseAuth.getInstance().currentUser!!.uid, userId!!)
        viewModel.myMessageList.observe(viewLifecycleOwner) { messages ->
            sweetAdapter.submitList(messages)
            binding.rvChat.smoothScrollToPosition(messages.size - 1)
        }
    }

    private fun initRecyclerView() {
        sweetAdapter.addHolder(R.layout.item_chat_right) { view, item ->
            val message = view.findViewById<AppCompatTextView>(R.id.txt_message)
            message.text = item.text.toString()
        }
        sweetAdapter.addHolder(R.layout.item_chat_left) { view, item ->
            val message = view.findViewById<AppCompatTextView>(R.id.txt_message)
            message.text = item.text.toString()
        }
        binding.rvChat.adapter = sweetAdapter
    }
}
