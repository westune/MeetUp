package com.example.lorenzo.meetup2

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
import com.example.lorenzo.meetup2.model.ChatMessage
import com.firebase.ui.database.FirebaseRecyclerAdapter
import kotlinx.android.synthetic.*

class chatFragment:Fragment(), MessageUtil.MessageLoadListener{

    private val LOG:String = "Chat Fragment"
    private var mFirebaseAdapter:FirebaseRecyclerAdapter<ChatMessage, MessageUtil.MessageViewHolder>? = null
    private val MESSAGES_CHILD:String = "messages"
    private val MSG_LENGTH_LIMIT:Int = 150
    private val ANONYMOUS:String = "anonymous"

    //UI Elements
    private var mSendButton:FloatingActionButton? = null
    private val mMessageRecyclerView:RecyclerView = RecyclerView(this.context!!)
    private var mLinearLayoutManager:LinearLayoutManager? =  null
    private var mMessageEditText:EditText? = null

    override fun onAttach(context: Context?) {
        Log.d(LOG, "On Attach")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(LOG, "On Create")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(LOG, "On Create View")
        mSendButton = container!!.findViewById(R.id.sendButton)
        mFirebaseAdapter = MessageUtil.Companion.getFirebaseAdapter(this.activity!!, this, mLinearLayoutManager!!, mMessageRecyclerView)
        mMessageRecyclerView.adapter = mFirebaseAdapter

        return inflater!!.inflate(R.layout.chat_fragment, container, false)
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

    }
}