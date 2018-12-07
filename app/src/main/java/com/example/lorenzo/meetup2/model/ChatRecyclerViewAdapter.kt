package com.example.lorenzo.meetup2.model


import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.support.design.R.id.center
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.lorenzo.meetup2.fragments.ItemInfoFragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat.getColor
import android.text.Layout
import android.util.TypedValue
import android.widget.Button
import com.example.lorenzo.meetup2.MainActivity
import com.example.lorenzo.meetup2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

/*
resources Used:
https://stackoverflow.com/questions/5959870/programmatically-set-height-on-layoutparams-as-density-independent-pixels
 */

class ChatRecyclerViewAdapter(val list: MutableList<ChatMessage>, val activity: MainActivity): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

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
        val item:ChatMessage = list[position]
        viewHolder.body.text = item.body
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val body = itemView.findViewById<TextView>(R.id.body)!!
    }








}