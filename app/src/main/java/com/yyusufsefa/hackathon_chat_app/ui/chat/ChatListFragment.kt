package com.yyusufsefa.hackathon_chat_app.ui.chat

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.ramo.sweetrecycleradapter.SweetRecyclerAdapter
import com.yyusufsefa.hackathon_chat_app.R
import com.yyusufsefa.hackathon_chat_app.common.BaseFragment
import com.yyusufsefa.hackathon_chat_app.data.model.User
import com.yyusufsefa.hackathon_chat_app.databinding.FragmentChatListBinding
import com.yyusufsefa.hackathon_chat_app.util.saveCurrentUserDeviceToken


class ChatListFragment : BaseFragment<FragmentChatListBinding>(R.layout.fragment_chat_list) {


    private val viewModel: ChatListViewModel by lazy {
        ViewModelProvider(this).get(ChatListViewModel::class.java)
    }

    private val sweetAdapter = SweetRecyclerAdapter<User>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        getChatList()
        catchDeviceToken()
    }

    private fun getChatList() {
        viewModel.fetchChatList()
        viewModel.userList.observe(viewLifecycleOwner) { userList ->
            if (userList.isEmpty()) {
                binding.txtNoChat.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.txtNoChat.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                sweetAdapter.submitList(userList)
            }
        }
    }

    private fun initRecyclerView() {
        sweetAdapter.addHolder(R.layout.item_user) { view, item ->
            val name = view.findViewById<AppCompatTextView>(R.id.txt_name)
            name.text = item.name + " " + item.lastName
        }
        sweetAdapter.setOnItemClickListener { v, item ->
            val bundle = bundleOf("userId" to item.userId)
            bundle.putString("userName", item.name)
            findNavController().navigate(R.id.action_homeFragment_to_chatFragment, bundle)
        }
        binding.recyclerView.adapter = sweetAdapter
    }

    private fun catchDeviceToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            val token = task.result
            if (token != null) {
                saveCurrentUserDeviceToken(token)
            }
        })
    }
}
