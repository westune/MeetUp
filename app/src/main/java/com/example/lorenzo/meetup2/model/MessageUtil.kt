package com.example.lorenzo.meetup2.model

import android.app.Activity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.lorenzo.meetup2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.chat_message.view.*

class MessageUtil{
    private val TAG = MessageUtil::class.java.simpleName
    val MESSAGES_CHILD = "messages"
    private val sFirebaseDatabaseReference:DatabaseReference =
            FirebaseDatabase.getInstance().reference
    lateinit var sAdapterListener: com.example.lorenzo.meetup2.MessageUtil.MessageLoadListener
    private lateinit var sFirebaseAuth: FirebaseAuth

    interface MessageLoadListener {
        fun onLoadComplete()
    }

    fun send(chatMessage: ChatMessage){
        sFirebaseDatabaseReference.child(MESSAGES_CHILD).push().setValue(chatMessage)
    }

    class MessageViewHolder(view: View): RecyclerView.ViewHolder(view){

        lateinit var messageTextView:TextView
        lateinit var messengerTextView: TextView
        lateinit var messengerImageView: ImageView

        init {
            messageTextView = view.findViewById(R.id.messageTextView)
            messengerTextView = view.findViewById(R.id.messengerTextView)
            messengerImageView = view.findViewById(R.id.messengerImageView)
        }
    }

    companion object {
        fun getFirebaseAdapter(activity: Activity,
                               listener: MessageLoadListener,
                               linearManager: LinearLayoutManager,
                               recyclerView: RecyclerView){
        }
    }
}