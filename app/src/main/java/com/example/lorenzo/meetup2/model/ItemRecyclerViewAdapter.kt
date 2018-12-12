package com.example.lorenzo.meetup2.model


import android.app.AlertDialog
import android.os.Bundle
import android.support.design.R.id.center
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.util.TypedValue
import android.widget.Button
import com.example.lorenzo.meetup2.MainActivity
import com.example.lorenzo.meetup2.R
import com.example.lorenzo.meetup2.fragments.ConvosFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

/*
resources Used:
https://stackoverflow.com/questions/5959870/programmatically-set-height-on-layoutparams-as-density-independent-pixels
 */

class ItemRecyclerViewAdapter(private val list: List<Item>, private val activity: MainActivity, private val fragment: Fragment, private val sell:Boolean): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val LAYOUT = R.layout.item_card
    private lateinit var view:View
    private lateinit var mInflater:LayoutInflater
    private lateinit var ref:DatabaseReference
    private val mSellDataSnapshotList: MutableList<DataSnapshot> = mutableListOf()
    private lateinit var mRecyclerView:RecyclerView
    private val mActivity = activity

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        mInflater = LayoutInflater.from(p0.context)
        view = mInflater.inflate(LAYOUT, p0, false)
        ref = FirebaseDatabase.getInstance().getReference("Items")
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
            when(fragment.javaClass){
                ConvosFragment::class.java -> {
                    when(sell){
                        true -> showConvos(item)
                        false -> openChat(item)
                    }
                }
                else -> showItem(item)
            }
        }
    }

    private fun showConvos(item: Item){
        val dialogBuilder = AlertDialog.Builder(activity)
        val dialogView = mInflater.inflate(R.layout.convo_list, null)
        dialogBuilder.setView(dialogView)
        val dialog = dialogBuilder.show()
        mRecyclerView = dialogView.findViewById(R.id.recyclerView)
        mRecyclerView.adapter = ConvosRecyclerViewAdapter(mSellDataSnapshotList, mActivity, null)
        mRecyclerView.layoutManager = LinearLayoutManager(activity)
        val nameText = dialogView.findViewById<TextView>(R.id.name)
        nameText.text = item.name
        getConvos(item.id, dialog)
    }

    private fun getConvos(itemId:String, dialog:AlertDialog){
        var thread = Thread()
        Log.d("id", itemId)
        val query = FirebaseDatabase.getInstance().getReference("messages/$itemId").orderByValue()
        thread = Thread {
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(data: DataSnapshot) {
                    if (data.exists()) {
                        mSellDataSnapshotList.clear()
                        for(i in data.children) {
                            mSellDataSnapshotList.add(i)
                        }
                        mRecyclerView.adapter = ConvosRecyclerViewAdapter(mSellDataSnapshotList, mActivity, dialog)
                    }
                    thread.interrupt()
                }
            })
        }
        thread.run()
    }
    private fun openChat(item:Item){
        val bundle = Bundle()
        bundle.putString("productId", item.id)
        bundle.putString("buyer", activity.sUserName)
        activity.showChatFragment(bundle)
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


    private fun showItem(item:Item){
        val dialogBuilder = AlertDialog.Builder(activity)
        var dialogView = mInflater.inflate(R.layout.item_info_fragment, null)
        dialogBuilder.setView(dialogView)
        val dialog = dialogBuilder.show()
        val image = dialogView.findViewById<ImageView>(R.id.image)
        val priceTextView = dialogView.findViewById<TextView>(R.id.price)
        val nameTextView = dialogView.findViewById<TextView>(R.id.name)
        val descriptionTextView = dialogView.findViewById<TextView>(R.id.description)
        val button = dialogView.findViewById<Button>(R.id.button)
        val buttonLayout = dialogView.findViewById<LinearLayout>(R.id.buttonLayout)
        if(item.imageUrl != "") {
            Picasso.with(activity)
                    .load(item.imageUrl)
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
        priceTextView.setText("$" + item.price)
        nameTextView.text = item.name
        descriptionTextView.text = item.description
        if(item.seller == mActivity.sUserEmail){
            val deleteButton = Button(activity)
            deleteButton.setText(R.string.delete_item)
            deleteButton.setBackgroundColor(activity.resources.getColor(R.color.red, mActivity.theme))
            deleteButton.setTextColor(activity.resources.getColor(R.color.white, mActivity.theme))
            deleteButton.gravity = center
            deleteButton.textAlignment = View.TEXT_ALIGNMENT_CENTER
            deleteButton.setPadding(5, 5, 5, 5)


            deleteButton.layoutParams = ViewGroup.LayoutParams(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75F, activity.resources.displayMetrics).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
            deleteButton.setOnClickListener {
                dialogView = mInflater.inflate(R.layout.delete_alert, null)
                dialogBuilder.setView(dialogView)
                val cancelButton = dialogView.findViewById<Button>(R.id.cancel_button)
                val deleteButton1 = dialogView.findViewById<Button>(R.id.delete_button)
                val popUp = dialogBuilder.show()
                cancelButton.setOnClickListener {
                    popUp.dismiss()
                }
                deleteButton1.setOnClickListener{
                    ref.child(item.id).removeValue()
                    dialog.dismiss()
                    popUp.dismiss()
                }
            }

            val soldButton = Button(activity)
            soldButton.setText(R.string.mark_item_as_sold)
            soldButton.setBackgroundColor(activity.resources.getColor(R.color.blue, mActivity.theme))
            soldButton.setTextColor(activity.resources.getColor(R.color.white, mActivity.theme))
            soldButton.gravity = center
            soldButton.textAlignment = View.TEXT_ALIGNMENT_CENTER
            soldButton.setPadding(5, 5, 5, 5)


            soldButton.layoutParams = ViewGroup.LayoutParams(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75F, activity.resources.displayMetrics).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
            soldButton.setOnClickListener {
                //Mark Item as Sold

                dialogView = mInflater.inflate(R.layout.delete_alert, null)
                dialogBuilder.setView(dialogView)
                val cancelButton = dialogView.findViewById<Button>(R.id.cancel_button)
                val soldButton = dialogView.findViewById<Button>(R.id.delete_button)
                val popUp = dialogBuilder.show()
                cancelButton.setOnClickListener {
                    popUp.dismiss()
                }
                soldButton.setOnClickListener{
                    val newItem = Item(item.id, item.description,
                            item.name, item.zip,
                            item.price, item.seller,
                            item.lon, item.lat,
                            item.imageUrl, true)
                    ref.child(item.id).setValue(newItem)
                    dialog.dismiss()
                    popUp.dismiss()
                }
            }

            button.setText(R.string.edit_item)
            button.setBackgroundColor(activity.resources.getColor(R.color.green, mActivity.theme))
            button.setTextColor(activity.resources.getColor(R.color.white, mActivity.theme))
            buttonLayout.addView(deleteButton)
            buttonLayout.addView(soldButton)
            button.setOnClickListener{
                //Edit Item
                val bundle = Bundle()
                bundle.putString("id", item.id)
                bundle.putString("name", item.name)
                bundle.putString("price", item.price)
                bundle.putString("description", item.description)
                bundle.putString("imageUrl", item.imageUrl)
                bundle.putString("zip", item.zip)
                activity.showPostItemFragment(bundle)
                dialog.dismiss()
            }
        }else{
            button.setOnClickListener{
                //Contact Seller
                mActivity.checkIfUserIsSignedIn()
                val bundle = Bundle()
                bundle.putString("productId", item.id)
                bundle.putString("buyer", activity.sUserName)
                activity.showChatFragment(bundle)
                dialog.dismiss()
            }
        }

    }





}