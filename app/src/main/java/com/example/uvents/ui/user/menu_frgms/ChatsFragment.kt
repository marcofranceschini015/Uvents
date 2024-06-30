package com.example.uvents.ui.user.menu_frgms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uvents.R
import com.example.uvents.controllers.ChatManager
import com.example.uvents.controllers.MenuController
import com.example.uvents.controllers.adapter.OrganizerChatsAdapter
import com.example.uvents.model.User

class ChatsFragment(private val menuController: MenuController) : Fragment() {

    private lateinit var msgEmptyChat: TextView
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: MutableList<User>
    private lateinit var newsList: MutableList<Int>
    private lateinit var adapter: OrganizerChatsAdapter
    private lateinit var chatManager: ChatManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This callback will only be called when the Fragment is at least Started.
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Handle the back button event
                menuController.switchFragment(MapFragment(menuController))
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v: View? = inflater.inflate(R.layout.fragment_chats, container, false)

        if(v != null) {
            msgEmptyChat = v.findViewById(R.id.tvNoChat)
            chatManager = ChatManager(getString(R.string.firebase_url))
            userList = mutableListOf()
            newsList = mutableListOf()
            adapter = OrganizerChatsAdapter(menuController.menuActivity, userList, newsList)
            userRecyclerView = v.findViewById(R.id.rvOrganizerNames)
        }

        msgEmptyChat.visibility = View.GONE
        userRecyclerView.layoutManager = LinearLayoutManager(menuController.menuActivity)
        userRecyclerView.adapter = adapter

        chatManager.updateUserListChats(userList, newsList, adapter, msgEmptyChat)

        return v
    }

}