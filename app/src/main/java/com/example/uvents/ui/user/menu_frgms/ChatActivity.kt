package com.example.uvents.ui.user.menu_frgms

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
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
import com.example.uvents.controllers.ChatManager
import com.example.uvents.databinding.ActivityChatBinding
import com.example.uvents.model.Message
import kotlinx.coroutines.runBlocking

class ChatActivity() : AppCompatActivity() {

    private lateinit var rlKeyboard: RelativeLayout
    private lateinit var customBack: ImageView
    private lateinit var userName: TextView
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var linearLayout: LinearLayout
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: MutableList<Message>
    var receiverRoom: String? = null
    var senderRoom: String? = null

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
                // Optionally call the default back press behavior
                if (isEnabled) {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })

        customBack = findViewById(R.id.ivCustomBack)
        customBack.setOnClickListener {
            finish()
        }

        rlKeyboard = findViewById(R.id.rlKeyboard)
        rlKeyboard.visibility = View.GONE
        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        linearLayout = findViewById(R.id.linearLayout)
        messageBox = findViewById(R.id.messageBox)
        sendButton = findViewById(R.id.sendButton)

        // Adding a global layout listener to adjust the UI when the keyboard appears or disappears
        binding.main.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            binding.main.getWindowVisibleDisplayFrame(r)
            val screenHeight = binding.main.rootView.height
            val keypadHeight = screenHeight - r.bottom

            if (keypadHeight > screenHeight * 0.15) {
                // Keyboard is visible
                rlKeyboard.visibility = View.VISIBLE
            } else {
                // Keyboard is hidden
                rlKeyboard.visibility = View.GONE
            }
        }

        val chatManager = ChatManager(getString(R.string.firebase_url))
        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")
        val senderUid = chatManager.getCurrentUid()

        userName = findViewById(R.id.tvOrganizerName)
        userName.text = name

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        messageList = mutableListOf()
        messageAdapter = MessageAdapter(this, messageList,
            receiverRoom!!, senderRoom!!, chatManager)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter

        runBlocking {
            val numNewMsg = chatManager.readNewMessageNumber(receiverRoom!!)
            if(numNewMsg != null && numNewMsg != 0) {
                chatManager.updateUserTotalNewMessages(senderUid, false, numNewMsg)
            }
        }

        chatManager.updateMessageList(senderRoom!!, messageList, messageAdapter)

        sendButton.setOnClickListener {
            val message = messageBox.text.toString()
            runBlocking {
                chatManager.addMessageOnDb(message, senderUid, receiverUid!!, senderRoom!!, receiverRoom!!)
            }
            messageBox.setText("")
        }

    }

}