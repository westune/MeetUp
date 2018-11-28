package com.example.lorenzo.meetup2

import android.app.Activity
import android.graphics.Bitmap
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.lorenzo.meetup2.model.ChatMessage

class MessageUtil{

    private val LOG_TAG:String = MessageUtil::class.java.simpleName
    public val MESSAGES_CHILD = "messages"
    private val sFirebaseDatabaseReference:DatabaseReference =
            FirebaseDatabase.getInstance().reference
    private val sAdapaterListener:MessageLoadListener? = null

    public interface MessageLoadListener{ fun onLoadComplete() }

    fun send(chatMessage: ChatMessage){
        sFirebaseDatabaseReference.child(MESSAGES_CHILD).push().setValue(chatMessage)
    }

    class MessageViewHolder(v: View): RecyclerView.ViewHolder(v){
        var messageTextView:TextView? = null
        var messengerTextView:TextView? = null
        var messengerImageView: ImageView? = null

        init{
            messageTextView = itemView.findViewById(R.id.messageTextView)
            messengerTextView = itemView.findViewById(R.id.messengerTextView)
            messengerImageView = itemView.findViewById(R.id.messengerImageView)
        }
    }

    companion object {
        var MU = MessageUtil
        fun getFirebaseAdapter(activity: Activity,
                               listener: MessageLoadListener,
                               linearManager: LinearLayoutManager,
                               recyclerView: RecyclerView)
                :FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder>{
            return MU.getFirebaseAdapter(activity, listener, linearManager, recyclerView)
        }
    }

    fun getFirebaseAdapater(activity: Activity,
                            listener: MessageLoadListener,
                            linearManager: LinearLayoutManager,
                            recyclerView: RecyclerView): FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder> {
        val adapter = object: FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder>(ChatMessage::class.java,
        R.layout.chat_message,
        MessageViewHolder::class.java,
        sFirebaseDatabaseReference.child(MESSAGES_CHILD)){
            override fun populateViewHolder(viewHolder: MessageViewHolder?, model: ChatMessage?, position: Int) {
                sAdapaterListener!!.onLoadComplete()
                viewHolder!!.messageTextView!!.setText(model!!.getText())
                viewHolder!!.messengerTextView!!.setText(model!!.getName())
                if(model!!.getPhotoUrl() == null){
                    viewHolder!!.messengerImageView!!
                            .setImageDrawable(ContextCompat.getDrawable(
                                    activity, R.drawable.ic_account_circle_black_36dp
                            ))
                }else{
                    val target:SimpleTarget<Bitmap> = object: SimpleTarget<Bitmap>(){
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            viewHolder.messengerImageView!!.setImageBitmap(resource)
                        }
                    }

                    Glide.with(activity).asBitmap().load(model.getPhotoUrl()).into(target)
                }
            }
        }

        adapter.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver(){
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                val messageCount = adapter.itemCount
                val lastVisiblePosition = linearManager.findLastCompletelyVisibleItemPosition()
                if(lastVisiblePosition == -1 || (positionStart >= (messageCount - 1) && lastVisiblePosition == (positionStart - 1))){
                    recyclerView.scrollToPosition(positionStart)
                }
            }
        })
        return adapter
    }


}

