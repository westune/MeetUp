package com.example.lorenzo.meetup2

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.lorenzo.meetup2.model.Item
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import java.io.Console

class PostItemFragment:Fragment(){

    private val LOG = "Post Item Fragment"
    private val LAYOUT = R.layout.post_item_fragment
    private var priceText:EditText? = null
    private var descriptionText:EditText? = null
    private var nameText:EditText? = null
    private var zipText:EditText? = null

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
        val view = inflater!!.inflate(LAYOUT, container, false)
        priceText = view.findViewById(R.id.price)
        nameText = view.findViewById(R.id.name)
        descriptionText = view.findViewById(R.id.description)
        zipText = view.findViewById(R.id.zip)
        val button = view.findViewById<Button>(R.id.postButton1)
        button.setOnClickListener{Button ->
            when(Button.id){
                R.id.postButton1 -> postItem(
                            nameText!!.text.toString().trim(),
                            descriptionText!!.text.toString().trim(),
                            priceText!!.text.toString().trim(),
                            zipText!!.text.toString().trim())
            }
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

    private fun postItem(name: String, description: String, price: String, zip: String){
        when{
            name.isEmpty() -> nameText!!.error = "Please Enter a Name"
            description.isEmpty() -> descriptionText!!.error = "Please Enter a Description"
            price.isEmpty() -> priceText!!.error = "Please Enter a Price"
            zip.isEmpty() -> zipText!!.error = "Please Enter a zip"
        }


        val ref = FirebaseDatabase.getInstance().getReference("Items")
        val id = ref.push().key
        val newItem = Item(id!!, description, name, zip, price)
        ref.child(id).setValue(newItem).addOnCompleteListener{
            Toast.makeText(this.context, "Item Posted!", Toast.LENGTH_LONG)
        }
    }




}