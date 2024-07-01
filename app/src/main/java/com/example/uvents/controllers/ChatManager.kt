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
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class ChatManager(dbUrl: String) {

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val mDbRef: DatabaseReference = FirebaseDatabase.getInstance(dbUrl).getReference()


    fun getCurrentUid():String {
        return mAuth.currentUser?.uid!!
    }


    fun updateUserListChats(userList: MutableList<User>, newsList: MutableList<Int>, adapter: OrganizerChatsAdapter, msgEmptyChat: TextView) {
        mDbRef.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                val pendingCallbacks = snapshot.childrenCount
                var processedCallbacks = 0

                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(User::class.java)

                    if (mAuth.currentUser?.uid!! != currentUser?.uid) {
                        val chatId = mAuth.currentUser?.uid!! + currentUser?.uid!!
                        chatExists(chatId) { exists, exception ->
                            if (exception != null) {
                                exception.printStackTrace()
                            } else if (exists) {
                                userList.add(currentUser)
                                var news: Int? = 0
                                runBlocking {
                                    if(readNewMessageSenderUid(chatId) != mAuth.currentUser?.uid!!) {
                                        news = readNewMessageNumber(chatId)
                                    }
                                }
                                newsList.add(news!!)
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


    fun addMessageOnDb(message: String, senderUid: String, receiverUid: String, senderRoom: String, receiverRoom: String) {
        val messageObject = Message(message, senderUid)

        mDbRef.child("chat").child(senderRoom).child("messages").push()
            .setValue(messageObject).addOnSuccessListener {
                mDbRef.child("chat").child(receiverRoom).child("messages").push()
                    .setValue(messageObject)
            }

        updateNewMessageSenderUid(receiverRoom, senderUid)
        updateNewMessageNumber(receiverRoom, true)

        updateNewMessageSenderUid(senderRoom, senderUid)
        updateNewMessageNumber(senderRoom, true)

        updateUserTotalNewMessages(receiverUid, true, 0)
    }


    fun updateNewMessageNumber(room: String, increase: Boolean) {
        val newsRef = mDbRef.child("chat").child(room).child("notification").child("numberNewMsg")

        newsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val currentValue = dataSnapshot.getValue(Int::class.java)
                if (currentValue != null) {
                    // Update the value
                    var newValue = 0
                    if(increase) {
                        newValue = currentValue + 1
                    }
                    newsRef.setValue(newValue)
                } else {
                    newsRef.setValue(1)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Failed to read value: ${databaseError.message}")
            }
        })
    }


    fun updateNewMessageSenderUid(receiverRoom: String, senderUid: String) {
        val newSenderRef = mDbRef.child("chat").child(receiverRoom).child("notification").child("senderIdNewMsg")

        newSenderRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val currentUid = dataSnapshot.getValue(String::class.java)
                if (currentUid == null) {
                    newSenderRef.setValue(senderUid)
                } else {
                    if(senderUid != currentUid) {
                        newSenderRef.setValue(senderUid)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Failed to read value: ${databaseError.message}")
            }
        })
    }


    suspend fun readNewMessageNumber(receiverRoom: String): Int? {
        val newsRef = mDbRef.child("chat").child(receiverRoom).child("notification").child("numberNewMsg")

        return try {
            val snapshot = newsRef.get().await()
            snapshot.getValue(Int::class.java)
        } catch (e: Exception) {
            println("Failed to read value: ${e.message}")
            null
        }
    }


    suspend fun readNewMessageSenderUid(receiverRoom: String): String? {
        val newSenderRef = mDbRef.child("chat").child(receiverRoom).child("notification").child("senderIdNewMsg")

        return try {
            val snapshot = newSenderRef.get().await()
            snapshot.getValue(String::class.java)
        } catch (e: Exception) {
            println("Failed to read value: ${e.message}")
            null
        }
    }


    fun updateUserTotalNewMessages(receiverUid: String, increase: Boolean, numMsgRead: Int) {
        val totalRef = mDbRef.child("user").child(receiverUid).child("totalNewMsg")

        totalRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val currentValue = dataSnapshot.getValue(Int::class.java)
                if (currentValue != null) {
                    // Update the value
                    var newValue = currentValue - numMsgRead
                    if(increase) {
                        newValue = currentValue + 1
                    }
                    totalRef.setValue(newValue)
                } else {
                    totalRef.setValue(1)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Failed to read value: ${databaseError.message}")
            }
        })
    }

}