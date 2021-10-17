package com.yyusufsefa.hackathon_chat_app.ui.main

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.yyusufsefa.hackathon_chat_app.R
import com.yyusufsefa.hackathon_chat_app.common.BaseFragment
import com.yyusufsefa.hackathon_chat_app.databinding.FragmentHomeBinding
import com.yyusufsefa.hackathon_chat_app.ui.chat.ChatListFragment
import com.yyusufsefa.hackathon_chat_app.ui.main.users.UsersFragment

class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTabs()

        binding.btnLogOut.setOnClickListener {
            removeDeviceToken(FirebaseAuth.getInstance().currentUser!!.uid)
            FirebaseAuth.getInstance().signOut()
            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
        }
    }

    private fun removeDeviceToken(currentUserId: String) {
        Firebase.database.getReference("device_tokens").child(currentUserId).removeValue()
    }

    private fun initTabs() {
        val adapter = HomeFragmentStateAdapter(requireActivity())
        adapter.addFragment(ChatListFragment(), "Chats")
        adapter.addFragment(UsersFragment(), "Users")
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = adapter.getTabTitle(position)
        }.attach()
    }
}
