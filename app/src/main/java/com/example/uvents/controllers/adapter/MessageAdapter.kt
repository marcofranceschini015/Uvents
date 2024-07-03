package com.example.chatapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uvents.R
import com.example.uvents.controllers.ChatManager
import com.example.uvents.model.Message
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking

class MessageAdapter(val context: Context, val messageList: List<Message>,
    val receiverRoom: String, val senderRoom: String,
    val chatManager: ChatManager): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_SENT = 1
    val ITEM_RECEIVED = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        val senderUid = chatManager.getCurrentUid()
//        runBlocking {
//            val numNewMsg = chatManager.readNewMessageNumber(receiverRoom)
//            if(numNewMsg != null && numNewMsg != 0) {
//                chatManager.updateUserTotalNewMessages(senderUid, false, numNewMsg)
//
//
//                if(chatManager.readNewMessageSenderUid(receiverRoom) != senderUid) {
//                    chatManager.updateNewMessageNumber(receiverRoom, false)
//                    chatManager.updateNewMessageNumber(senderRoom, false)
//                }
//            }
//        }

        if (viewType == 1) {
            val view: View = LayoutInflater.from(context).inflate(R.layout.message_sent, parent, false)
            return SentViewHolder(view)
        } else {
            val view: View = LayoutInflater.from(context).inflate(R.layout.message_received, parent, false)
            return ReceivedViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)) {
            return ITEM_SENT
        } else {
            return ITEM_RECEIVED
        }
    }

    override fun getItemCount(): Int {

        val senderUid = chatManager.getCurrentUid()
        runBlocking {
            val numNewMsg = chatManager.readNewMessageNumber(receiverRoom)
            if(numNewMsg != null && numNewMsg != 0) {
                chatManager.readUserTotalNewMessages(
                    onSuccess = {
                        if(it != null && it > 0) {
                            chatManager.updateUserTotalNewMessages(senderUid, false, numNewMsg)
                        }

                    },
                    onError = { exception ->
                        println("Failed to read value: ${exception.message}")
                    }
                )

                if(chatManager.readNewMessageSenderUid(receiverRoom) != senderUid) {
                    chatManager.updateNewMessageNumber(receiverRoom, false)
                    chatManager.updateNewMessageNumber(senderRoom, false)
                }
            }
        }

        return messageList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        if (holder.javaClass == SentViewHolder::class.java) {
            var viewHolder = holder as SentViewHolder
            holder.sentMessage.text = currentMessage.message
        } else {
            var viewHolder = holder as ReceivedViewHolder
            holder.receivedMessage.text = currentMessage.message
        }
    }

    class SentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val sentMessage = itemView.findViewById<TextView>(R.id.txt_sent_message)
    }

    class ReceivedViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val receivedMessage = itemView.findViewById<TextView>(R.id.txt_received_message)
    }

}