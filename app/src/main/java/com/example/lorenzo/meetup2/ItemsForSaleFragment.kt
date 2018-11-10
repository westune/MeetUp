package com.example.lorenzo.meetup2

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class ItemsForSaleFragment:Fragment(){

    private val LOG = "Items For Sale Fragment"
    private val LAYOUT = R.layout.items_for_sale_fragment

    private val clickListener = View.OnClickListener {view ->
        when(view.id){
            R.id.postButton -> showPostView()
        }
    }

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
        val button = view.findViewById<Button>(R.id.postButton)
        button.setOnClickListener{Button ->
            when(Button.id){
                R.id.postButton -> showPostView()
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

    private fun showPostView(){
        val transaction = fragmentManager!!.beginTransaction()
        val fragment = PostItemFragment()
        transaction.replace(R.id.fragment_layout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}