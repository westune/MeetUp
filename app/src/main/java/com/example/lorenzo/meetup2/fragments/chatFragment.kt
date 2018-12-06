package com.example.lorenzo.meetup2.fragments

import android.content.Context
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import com.example.lorenzo.meetup2.R
import com.example.lorenzo.meetup2.model.ChatMessage
import com.example.lorenzo.meetup2.model.MessageUtil
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth

class chatFragment:Fragment(), MessageUtil.MessageLoadListener{

    private val LOG:String = "Chat Fragment"
    private lateinit var mFirebaseAdapter:FirebaseRecyclerAdapter<ChatMessage, MessageUtil.MessageViewHolder>
    private val MESSAGES_CHILD:String = "messages"
    private val MSG_LENGTH_LIMIT:Int = 150
    private val ANONYMOUS:String = "anonymous"
    private lateinit var productId:String

    //UI Elements
    private lateinit var mSendButton:FloatingActionButton
    private lateinit var mMessageRecyclerView:RecyclerView
    private lateinit var mLinearLayoutManager:LinearLayoutManager
    private lateinit var mMessageEditText:EditText
    private lateinit var mProgressbar:ProgressBar

    override fun onAttach(context: Context?) {
        Log.d(LOG, "On Attach")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(LOG, "On Create")
        mLinearLayoutManager = LinearLayoutManager(this.activity)
        mLinearLayoutManager.stackFromEnd = true
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(LOG, "On Create View")
        val view = inflater.inflate(R.layout.chat_fragment, container, false)
        mSendButton = view.findViewById(R.id.sendButton)
        mSendButton.setOnClickListener {
            when(it.id){
                R.id.sendButton -> sendMessage()
            }
        }
        mMessageRecyclerView = view.findViewById(R.id.messageRecyclerView)
        mProgressbar = view.findViewById(R.id.progressBar)
        mMessageEditText = view.findViewById(R.id.messageEditText)
        mFirebaseAdapter = MessageUtil.getFirebaseAdapter(this.activity!!, this, mLinearLayoutManager!!, mMessageRecyclerView)
        mMessageRecyclerView.layoutManager = mLinearLayoutManager
        mMessageRecyclerView.adapter = mFirebaseAdapter
        return view
    }

    fun sendMessage(){
        mMessageRecyclerView.scrollToPosition(0)
        val chatMessage = ChatMessage(mMessageEditText.text.toString(),
                FirebaseAuth.getInstance().currentUser!!.email.toString())
        MessageUtil.send(chatMessage, productId)
    }

    override fun onStart() {
        Log.d(LOG, "On Start")
        super.onStart()
    }

    override fun onResume() {
        Log.d(LOG, "On Resume")
        super.onResume()
    }

    override fun onPause() {
        Log.d(LOG, "On Pause")
        super.onPause()
    }

    override fun onStop() {
        Log.d(LOG, "On Stop")
        super.onStop()
    }

    override fun onDestroy() {
        Log.d(LOG, "On Destroy")
        super.onDestroy()
    }

    override fun onDetach() {
        Log.d(LOG, "On Detach")
        super.onDetach()
    }

    override fun onDestroyView() {
        Log.d(LOG, "On Destroy View")
        super.onDestroyView()
    }

    override fun onLoadComplete() {
        mProgressbar.visibility = View.INVISIBLE
    }
}