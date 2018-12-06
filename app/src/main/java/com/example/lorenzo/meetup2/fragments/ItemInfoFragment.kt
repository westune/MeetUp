package com.example.lorenzo.meetup2.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.lorenzo.meetup2.R
import com.squareup.picasso.Picasso

class ItemInfoFragment:Fragment(){


    private lateinit var name:String
    private lateinit var price: String
    private lateinit var imageUrl: String
    private lateinit var description: String
    private lateinit var image: ImageView
    private lateinit var nameText:TextView
    private lateinit var priceText:TextView
    private lateinit var descriptionText:TextView
    private lateinit var button:Button
    private val LAYOUT = R.layout.item_info_fragment




    override fun setArguments(args: Bundle?) {
        super.setArguments(args)
        name = args!!.get("name")!!.toString()
        price = args.get("price")!!.toString()
        imageUrl = args.get("imageUrl")!!.toString()
        description = args.get("description")!!.toString()
    }

    private val LOG = "Item Info Fragment"

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
        val view = inflater.inflate(LAYOUT, container, false)
        button = view.findViewById(R.id.button)
        nameText = view.findViewById(R.id.name)
        priceText = view.findViewById(R.id.price)
        descriptionText = view.findViewById(R.id.description)
        image = view.findViewById(R.id.image)
        nameText.text = name
        val p = "$" + price
        priceText.text = p
        descriptionText.text = description


        if(imageUrl != "") {
            Picasso.with(context)
                    .load(imageUrl)
                    .fit()
                    .centerCrop()
                    .into(image)
        }else{
            Picasso.with(context)
                    .load("https://image.shutterstock.com/image-vector/no-image-available-sign-internet-260nw-261719003.jpg")
                    .fit()
                    .centerCrop()
                    .into(image)
        }

        return view
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
}