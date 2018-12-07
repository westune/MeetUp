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
import com.example.lorenzo.meetup2.MainActivity
import com.example.lorenzo.meetup2.R
import com.example.lorenzo.meetup2.model.ChatMessage
import com.example.lorenzo.meetup2.model.ChatRecyclerViewAdapter
import com.google.firebase.database.*

class ChatFragment:Fragment(){

    private val LOG:String = "Chat Fragment"
    private val MESSAGES_CHILD:String = "messages"
    private val ANONYMOUS:String = "anonymous"
    private var mProductId:String = ""
    private lateinit var mActivity:MainActivity
    private lateinit var mRef: DatabaseReference
    private lateinit var mUserRef: DatabaseReference
    private var mBuyer:String = ""
    private val mMessages:MutableList<ChatMessage> = mutableListOf()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatRecyclerViewAdapter

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

    override fun setArguments(args: Bundle?) {
        mProductId = args!!.get("productId")!!.toString()
        mBuyer = args.get("buyer")!!.toString()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(LOG, "On Create")
        mActivity = activity as MainActivity
        mRef = FirebaseDatabase.getInstance().getReference("$MESSAGES_CHILD/$mProductId/$mBuyer")
        mUserRef = FirebaseDatabase.getInstance().getReference("users/$mBuyer")
        mLinearLayoutManager = LinearLayoutManager(this.activity)
        mLinearLayoutManager.stackFromEnd = true
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(LOG, "On Create View")
        val view = inflater.inflate(R.layout.chat_fragment, container, false)
        recyclerView = view.findViewById(R.id.messageRecyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        adapter = ChatRecyclerViewAdapter(mMessages, activity as MainActivity)
        recyclerView.adapter = adapter
        mSendButton = view.findViewById(R.id.sendButton)
        mSendButton.setOnClickListener {
            when(it.id){
                R.id.sendButton -> sendMessage()
            }
        }
        mMessageRecyclerView = view.findViewById(R.id.messageRecyclerView)
        mProgressbar = view.findViewById(R.id.progressBar)
        mMessageEditText = view.findViewById(R.id.messageEditText)
        loadMessages()
        return view
    }


    fun sendMessage(){
        mMessageRecyclerView.scrollToPosition(0)
        val chatMessage = ChatMessage(mMessageEditText.text.toString(),
                mActivity.sUserName,
                mActivity.sUserEmail)
        if(mMessages.size == 0){
            mUserRef.setValue(mProductId)
        }
        mRef.push().setValue(chatMessage)
    }

    override fun onStart() {
        Log.d(LOG, "On Start")
        super.onStart()
    }

    override fun onResume() {
        Log.d(LOG, "On Resume")
        mMessages
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

    private fun loadMessages() {
        val thread = Thread {
            mRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}



                override fun onDataChange(data: DataSnapshot) {
                    if (data.exists()) {
                        for(i in data.children){
                            mMessages.add(i.getValue(ChatMessage::class.java)!!)
                        }
                    }
                    adapter = ChatRecyclerViewAdapter(mMessages, activity as MainActivity)
                    recyclerView.adapter = adapter
                }
            })
        }
        thread.run()
    }

}