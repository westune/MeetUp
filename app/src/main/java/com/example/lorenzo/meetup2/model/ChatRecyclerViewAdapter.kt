package com.example.lorenzo.meetup2.model


import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.view.Gravity
import com.example.lorenzo.meetup2.MainActivity
import com.example.lorenzo.meetup2.R

/*
resources Used:
https://stackoverflow.com/questions/5959870/programmatically-set-height-on-layoutparams-as-density-independent-pixels
 */

class ChatRecyclerViewAdapter(val list: MutableList<ChatMessage>, val mActivity: MainActivity): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val LAYOUT = R.layout.chat_message
    private lateinit var view:View
    private lateinit var mInflater:LayoutInflater

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        mInflater = LayoutInflater.from(p0.context)
        view = mInflater.inflate(LAYOUT, p0, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder:ViewHolder = holder as ViewHolder
        val message:ChatMessage = list[position]
        if(message.sender == mActivity.sUserName){
            viewHolder.body.gravity = Gravity.END
        }
        viewHolder.body.text = message.body
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val body = itemView.findViewById<TextView>(R.id.body)!!
    }


}