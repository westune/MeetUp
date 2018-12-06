package com.example.lorenzo.meetup2.model


import android.content.Context
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.lorenzo.meetup2.fragments.ItemInfoFragment
import android.support.v4.app.FragmentManager
import com.example.lorenzo.meetup2.R
import com.squareup.picasso.Picasso

class RecyclerViewAdapter(val list: MutableList<Item>, val context: Context, val fragmentManager:FragmentManager): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val LAYOUT = R.layout.item_card
    private lateinit var view:View

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        view = LayoutInflater.from(p0.context).inflate(LAYOUT, p0, false)
        return ViewHolder(view)
    }




    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder:ViewHolder = holder as ViewHolder
        val item:Item = list[position]
        viewHolder.name.text = item.name
        viewHolder.price.text = item.price
        if(item.imageUrl != "") {
            Picasso.with(context)
                    .load(item.imageUrl)
                    .fit()
                    .centerCrop()
                    .into(viewHolder.image)
        }else{
            Picasso.with(context)
                    .load("https://image.shutterstock.com/image-vector/no-image-available-sign-internet-260nw-261719003.jpg")
                    .fit()
                    .centerCrop()
                    .into(viewHolder.image)
        }

        holder.layout.setOnClickListener{
            showItemInfo(item.name, item.price, item.imageUrl!!, item.description)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val name = itemView.findViewById<TextView>(R.id.name)!!
        val price = itemView.findViewById<TextView>(R.id.price)!!
        val image = itemView.findViewById<ImageView>(R.id.imageView)!!
        val layout = itemView.findViewById<LinearLayout>(R.id.layout)!!
    }


    private fun showItemInfo(name:String, price:String, imageUrl:String, description:String){
        val transaction = fragmentManager.beginTransaction()
        val fragment = ItemInfoFragment()
        val bundle = Bundle()
        bundle.putString("name", name)
        bundle.putString("price", price)
        bundle.putString("imageUrl", imageUrl)
        bundle.putString("description", description)
        fragment.arguments = bundle
        transaction.replace(R.id.fragment_layout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }



}