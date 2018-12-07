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
import com.squareup.picasso.Picasso

/*
resources Used:
https://stackoverflow.com/questions/5959870/programmatically-set-height-on-layoutparams-as-density-independent-pixels
 */

class RecyclerViewAdapter(val list: MutableList<Item>, val activity: MainActivity): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val LAYOUT = R.layout.item_card
    private lateinit var view:View
    private lateinit var mInflater:LayoutInflater

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        mInflater = LayoutInflater.from(p0.context)
        view = mInflater.inflate(LAYOUT, p0, false)
        return ViewHolder(view)
    }




    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder:ViewHolder = holder as ViewHolder
        val item:Item = list[position]
        viewHolder.name.text = item.name
        viewHolder.price.text = item.price
        if(item.imageUrl != "") {
            Picasso.with(activity)
                    .load(item.imageUrl)
                    .fit()
                    .centerCrop()
                    .into(viewHolder.image)
        }else{
            Picasso.with(activity)
                    .load("https://image.shutterstock.com/image-vector/no-image-available-sign-internet-260nw-261719003.jpg")
                    .fit()
                    .centerCrop()
                    .into(viewHolder.image)
        }

        holder.layout.setOnClickListener{
            showItem(item.name, item.price, item.imageUrl!!, item.description, item.seller)
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


    private fun showItem(name:String, price:String, imageUrl:String, description:String, seller:String){
        val dialogBuilder = AlertDialog.Builder(activity)
        val dialogView = mInflater.inflate(R.layout.item_info_fragment, null)
        dialogBuilder.setView(dialogView)
        val image = dialogView.findViewById<ImageView>(R.id.image)
        val priceTextView = dialogView.findViewById<TextView>(R.id.price)
        val nameTextView = dialogView.findViewById<TextView>(R.id.name)
        val descriptionTextView = dialogView.findViewById<TextView>(R.id.description)
        val button = dialogView.findViewById<Button>(R.id.button)
        val buttonLayout = dialogView.findViewById<LinearLayout>(R.id.buttonLayout)
        if(imageUrl != "") {
            Picasso.with(activity)
                    .load(imageUrl)
                    .fit()
                    .centerInside()
                    .into(image)
        }else{
            Picasso.with(activity)
                    .load("https://image.shutterstock.com/image-vector/no-image-available-sign-internet-260nw-261719003.jpg")
                    .fit()
                    .centerInside()
                    .into(image)
        }
        priceTextView.setText("$" + price)
        nameTextView.text = name
        descriptionTextView.text = description
        if(seller == FirebaseAuth.getInstance().currentUser!!.email.toString()){
            val deleteButton = Button(activity)
            deleteButton.setText(R.string.delete_item)
            deleteButton.setBackgroundColor(activity.resources.getColor(R.color.red, null))
            deleteButton.setTextColor(activity.resources.getColor(R.color.white, null))
            deleteButton.gravity = center
            deleteButton.textAlignment = View.TEXT_ALIGNMENT_CENTER
            deleteButton.setPadding(5, 5, 5, 5)


            deleteButton.layoutParams = ViewGroup.LayoutParams(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75F, activity.resources.displayMetrics).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
            deleteButton.setOnClickListener {

            }

            val soldButton = Button(activity)
            soldButton.setText(R.string.mark_item_as_sold)
            soldButton.setBackgroundColor(activity.resources.getColor(R.color.blue, null))
            soldButton.setTextColor(activity.resources.getColor(R.color.white, null))
            soldButton.gravity = center
            soldButton.textAlignment = View.TEXT_ALIGNMENT_CENTER
            soldButton.setPadding(5, 5, 5, 5)


            soldButton.layoutParams = ViewGroup.LayoutParams(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75F, activity.resources.displayMetrics).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
            soldButton.setOnClickListener {

            }

            button.setText(R.string.edit_item)
            button.layoutParams = ViewGroup.LayoutParams(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75F, activity.resources.displayMetrics).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
            button.setBackgroundColor(activity.resources.getColor(R.color.green, null))
            button.setTextColor(activity.resources.getColor(R.color.white, null))
            buttonLayout.addView(deleteButton)
            buttonLayout.addView(soldButton)
            button.setOnClickListener{
                //Mark Item as Sold

            }
        }else{
            button.setOnClickListener{
                //Contact Seller
            }
        }
        dialogBuilder.show()

    }





}