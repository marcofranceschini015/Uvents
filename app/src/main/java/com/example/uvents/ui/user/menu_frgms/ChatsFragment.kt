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
import com.example.uvents.controllers.MapController
import com.example.uvents.controllers.adapter.OrganizerChatsAdapter
import com.example.uvents.model.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatsFragment(private val mapController: MapController) : Fragment() {

    private lateinit var msgEmptyChat: TextView
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var eventList: MutableList<Event>
    private lateinit var adapter: OrganizerChatsAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private val dbUrl: String = "https://uvents-d3c3a-default-rtdb.europe-west1.firebasedatabase.app/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This callback will only be called when the Fragment is at least Started.
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Handle the back button event
                mapController.switchFragment(MapFragment(mapController))
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
            eventList = mutableListOf()
            adapter = OrganizerChatsAdapter(mapController.mapActivity, eventList)
            mAuth = FirebaseAuth.getInstance()
            userRecyclerView = v.findViewById(R.id.rvOrganizerNames)
        }

        msgEmptyChat.visibility = View.GONE
        userRecyclerView.layoutManager = LinearLayoutManager(mapController.mapActivity)
        userRecyclerView.adapter = adapter
        mDbRef = FirebaseDatabase.getInstance(dbUrl).getReference()

        mDbRef.child("event").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                eventList.clear()
                val pendingCallbacks = snapshot.childrenCount
                var processedCallbacks = 0

                for (postSnapshot in snapshot.children) {
                    val currentEvent = postSnapshot.getValue(Event::class.java)

//                    if (mAuth.currentUser?.uid!! != currentEvent?.uid) {
                        val chatId1 = mAuth.currentUser?.uid!! + currentEvent?.uid!! + currentEvent.eid!!
                        val chatId2 = currentEvent.uid!! + mAuth.currentUser?.uid!! + currentEvent.eid!!
                        mapController.chatExists(chatId1, chatId2) { exists, exception ->
                            if (exception != null) {
                                exception.printStackTrace()
                            } else if (exists) {
                                eventList.add(currentEvent)
                            }

                            processedCallbacks++
                            if (processedCallbacks.toLong() == pendingCallbacks) {
                                adapter.notifyDataSetChanged()
                                if(eventList.isEmpty()) {
                                    msgEmptyChat.visibility = View.VISIBLE
                                }
                            }
                        }
//                    } else {
//                        processedCallbacks++
//                        if (processedCallbacks.toLong() == pendingCallbacks) {
//                            adapter.notifyDataSetChanged()
//                            if(eventList.isEmpty()) {
//                                msgEmptyChat.visibility = View.VISIBLE
//                            }
//                        }
//                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException()
            }
        })

        return v
    }

}