package com.yyusufsefa.hackathon_chat_app.ui.chat

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
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

    private val mLocalFilePath by lazy {
        requireContext().externalCacheDir?.absolutePath + "/audiorecordtest.3gp"
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = arguments?.getString("userId")!!

        binding.txtUserName.text = arguments?.getString("userName")!!

        initRecyclerView()
        getMessages()
        traceMicIcon()

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

        binding.btnVoice.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    viewModel.startRecording(mLocalFilePath)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    viewModel.stopRecording(mLocalFilePath)
                    true
                }
                else -> false
            }
        }
    }

    private fun traceMicIcon() {
        viewModel.isRecording.observe(viewLifecycleOwner) {

            val iconId = if (it) R.drawable.ic_mic_off else R.drawable.ic_mic

            binding.btnVoice.icon = ContextCompat.getDrawable(requireActivity(), iconId)
        }
    }

    private fun getMessages() {
        viewModel.fetchMessage(FirebaseAuth.getInstance().currentUser!!.uid, userId!!)
        viewModel.myMessageList.observe(viewLifecycleOwner) { messages ->
            sweetAdapter.submitList(messages)
            if (messages.isNotEmpty())
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
