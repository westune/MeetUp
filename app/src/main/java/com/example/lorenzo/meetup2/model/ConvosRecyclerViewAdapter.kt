package com.example.lorenzo.meetup2.model


import android.app.AlertDialog
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.view.Gravity
import android.widget.LinearLayout
import com.example.lorenzo.meetup2.MainActivity
import com.example.lorenzo.meetup2.R
import com.google.firebase.database.DataSnapshot
import com.squareup.picasso.Picasso

/*
resources Used:
https://stackoverflow.com/questions/5959870/programmatically-set-height-on-layoutparams-as-density-independent-pixels
 */

class ConvosRecyclerViewAdapter(val list: MutableList<DataSnapshot>, private val mActivity:MainActivity, private val dialog:AlertDialog?): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val LAYOUT = R.layout.convo_list
    private lateinit var view:View
    private lateinit var mInflater:LayoutInflater

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        mInflater = LayoutInflater.from(p0.context)
        view = mInflater.inflate(LAYOUT, p0, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as ConvosRecyclerViewAdapter.ViewHolder
        val convo = list[position]
        val buyerName = convo.ref.key.toString()
        val itemId = convo.ref.parent!!.key.toString()
        viewHolder.name.text = buyerName
        holder.layout.setOnClickListener{
            val bundle = Bundle()
            bundle.putString("productId", itemId)
            bundle.putString("buyer", buyerName)
            mActivity.showChatFragment(bundle)
            dialog!!.dismiss()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val name = itemView.findViewById<TextView>(R.id.name)!!
        val layout = itemView.findViewById<LinearLayout>(R.id.layout)!!
    }

}