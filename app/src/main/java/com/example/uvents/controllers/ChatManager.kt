package com.example.uvents.controllers

import android.view.View
import android.widget.TextView
import com.example.chatapplication.MessageAdapter
import com.example.uvents.controllers.adapter.OrganizerChatsAdapter
import com.example.uvents.model.Message
import com.example.uvents.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatManager(dbUrl: String) {

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val mDbRef: DatabaseReference = FirebaseDatabase.getInstance(dbUrl).getReference()

    fun updateUserListChats(userList: MutableList<User>, adapter: OrganizerChatsAdapter, msgEmptyChat: TextView) {
        mDbRef.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                val pendingCallbacks = snapshot.childrenCount
                var processedCallbacks = 0

                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(User::class.java)

                    if (mAuth.currentUser?.uid!! != currentUser?.uid) {
                        chatExists(mAuth.currentUser?.uid!! + currentUser?.uid!!) { exists, exception ->
                            if (exception != null) {
                                exception.printStackTrace()
                            } else if (exists) {
                                userList.add(currentUser)
                            }

                            processedCallbacks++
                            if (processedCallbacks.toLong() == pendingCallbacks) {
                                adapter.notifyDataSetChanged()
                                if(userList.isEmpty()) {
                                    msgEmptyChat.visibility = View.VISIBLE
                                }
                            }
                        }
                    } else {
                        processedCallbacks++
                        if (processedCallbacks.toLong() == pendingCallbacks) {
                            adapter.notifyDataSetChanged()
                            if(userList.isEmpty()) {
                                msgEmptyChat.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException()
            }
        })
    }

    private fun chatExists(chatId: String, callback: (Boolean, Exception?) -> Unit) {
        val chatRef = mDbRef.child("chat").child(chatId)

        chatRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    callback(true, null)
                } else {
                    callback(false, null)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(false, databaseError.toException())
            }
        })
    }

    fun updateMessageList(senderRoom: String, messageList: MutableList<Message>, adapter: MessageAdapter) {
        mDbRef.child("chat").child(senderRoom).child("messages").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()
                for (postSnapshot in snapshot.children) {
                    val message = postSnapshot.getValue(Message::class.java)
                    messageList.add(message!!)
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                println("Database error: ${error.message}")
            }

        })
    }

    fun addMessageOnDb(message: String, senderUid: String, senderRoom: String, receiverRoom: String) {
        val messageObject = Message(message, senderUid)

        mDbRef.child("chat").child(senderRoom).child("messages").push()
            .setValue(messageObject).addOnSuccessListener {
                mDbRef.child("chat").child(receiverRoom).child("messages").push()
                    .setValue(messageObject)
            }
    }

}