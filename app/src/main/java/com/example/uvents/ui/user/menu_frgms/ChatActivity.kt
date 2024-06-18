package com.example.uvents.ui.user.menu_frgms

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.MessageAdapter
import com.example.uvents.R
import com.example.uvents.databinding.ActivityChatBinding
import com.example.uvents.model.Message
import com.example.uvents.ui.user.MapActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatActivity() : AppCompatActivity() {

    private lateinit var orgName: TextView
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var linearLayout: LinearLayout
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: MutableList<Message>
    var receiverRoom: String? = null
    var senderRoom: String? = null
    private lateinit var mDbRef: DatabaseReference
    private val dbUrl: String = "https://uvents-d3c3a-default-rtdb.europe-west1.firebasedatabase.app/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@ChatActivity, MapActivity::class.java)
//                intent.putExtra("uid", uid)
                startActivity(intent)
                finish()
            }
        })

        linearLayout = findViewById(R.id.linearLayout)

        // Adding a global layout listener to adjust the UI when the keyboard appears or disappears
        binding.main.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            binding.main.getWindowVisibleDisplayFrame(r)
            val screenHeight = binding.main.rootView.height
            val keypadHeight = screenHeight - r.bottom

            if (keypadHeight > screenHeight * 0.15) {
                // Keyboard is visible
                binding.linearLayout.translationY = (-keypadHeight.toFloat() * 0.87).toFloat()
            } else {
                // Keyboard is hidden
                binding.linearLayout.translationY = 0f
            }
        }

        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid!!

        orgName = findViewById(R.id.tvOrganizerName)
        orgName.text = name

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        mDbRef = FirebaseDatabase.getInstance(dbUrl).getReference()

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messageBox)
        sendButton = findViewById(R.id.sendButton)

        messageList = mutableListOf()
        messageAdapter = MessageAdapter(this, messageList)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter

        mDbRef.child("chat").child(senderRoom!!).child("messages").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()
                for (postSnapshot in snapshot.children) {
                    val message = postSnapshot.getValue(Message::class.java)
                    messageList.add(message!!)
                }
                messageAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                println("Database error: ${error.message}")
            }

        })

        sendButton.setOnClickListener {
            val message = messageBox.text.toString()
            val messageObject = Message(message, senderUid)

            mDbRef.child("chat").child(senderRoom!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                mDbRef.child("chat").child(receiverRoom!!).child("messages").push()
                    .setValue(messageObject)
            }

            messageBox.setText("")
        }

    }

}