package com.yyusufsefa.hackathon_chat_app.ui.main.users

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ramo.sweetrecycleradapter.SweetRecyclerAdapter
import com.yyusufsefa.hackathon_chat_app.R
import com.yyusufsefa.hackathon_chat_app.common.BaseFragment
import com.yyusufsefa.hackathon_chat_app.data.model.User
import com.yyusufsefa.hackathon_chat_app.databinding.FragmentUsersBinding

class UsersFragment : BaseFragment<FragmentUsersBinding>(R.layout.fragment_users) {

    private val viewModel: UsersViewModel by lazy {
        ViewModelProvider(this).get(UsersViewModel::class.java)
    }

    private val sweetAdapter = SweetRecyclerAdapter<User>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initSearch()
        getUsers()
    }

    private fun initSearch() {
        binding.etSearch.addTextChangedListener { text ->
            viewModel.searchUserList(text.toString())
        }
    }

    private fun getUsers() {
        viewModel.fetchUserList()
        viewModel.userList.observe(viewLifecycleOwner) { userList ->
            if (userList.isEmpty()) {
                binding.txtNoUser.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.txtNoUser.visibility = View.GONE
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
}
