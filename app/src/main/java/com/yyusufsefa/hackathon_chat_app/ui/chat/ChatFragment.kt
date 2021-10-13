package com.yyusufsefa.hackathon_chat_app.ui.chat

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.ramo.sweetrecycleradapter.SweetRecyclerAdapter
import com.yyusufsefa.hackathon_chat_app.R
import com.yyusufsefa.hackathon_chat_app.common.BaseFragment
import com.yyusufsefa.hackathon_chat_app.data.model.ChatMessage
import com.yyusufsefa.hackathon_chat_app.databinding.FragmentChatBinding
import java.util.*

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
                    viewModel.stopRecording()
                    viewModel.saveVoiceMessage(
                        mLocalFilePath,
                        firebaseFileName = UUID.randomUUID().toString() + ".3gp",
                        userId!!
                    )
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
        sweetAdapter.addHolder(R.layout.item_voice_right) { view, item ->
            val btnPlay = view.findViewById<MaterialButton>(R.id.btn_play_pause)
            btnPlay.setOnClickListener {
                playVoice(item)
            }
        }
        sweetAdapter.addHolder(R.layout.item_chat_left) { view, item ->
            val message = view.findViewById<AppCompatTextView>(R.id.txt_message)
            message.text = item.text.toString()
        }
        sweetAdapter.addHolder(R.layout.item_voice_left) { view, item ->
            val btnPlay = view.findViewById<MaterialButton>(R.id.btn_play_pause)
            btnPlay.setOnClickListener {
                playVoice(item)
            }
        }
        binding.rvChat.adapter = sweetAdapter
    }

    private fun playVoice(chatMessage: ChatMessage) {
        val fileRef = FirebaseStorage.getInstance().reference.child("VoiceMessages").child("Audio")
            .child(chatMessage.voicePath.toString())
        fileRef.downloadUrl.addOnSuccessListener {
            Toast.makeText(requireContext(), "Playing", Toast.LENGTH_LONG).show()

            val mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(it.toString())
            mediaPlayer.setOnPreparedListener { player ->
                player.start()
            }
            mediaPlayer.prepareAsync()
        }
    }
}
