package com.example.lorenzo.meetup2.model


import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.lorenzo.meetup2.R

class RecyclerViewAdapter(list: MutableList<Item>, context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>(){


    val list  = list
    val context = context

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        val view:View = LayoutInflater.from(p0.context).inflate(R.layout.item_card, p0, false)
        return ViewHolder(view)
    }




    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder:ViewHolder = holder as ViewHolder
        val item:Item = list[position]
        viewHolder.name.text = item.name
        viewHolder.price.text = item.price
    }

    override fun getItemCount(): Int {
        return list.size
    }



    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val name = itemView.findViewById<TextView>(R.id.name)!!
        val price = itemView.findViewById<TextView>(R.id.price)!!
    }
}