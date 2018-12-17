package com.example.lorenzo.meetup2.fragments

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.AsyncTaskLoader
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.lorenzo.meetup2.MainActivity
import com.example.lorenzo.meetup2.R
import com.example.lorenzo.meetup2.model.Item
import com.example.lorenzo.meetup2.model.ItemRecyclerViewAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ItemsForSaleFragment : Fragment() {

    private val TAG = "Items For Sale Fragment"
    private val LAYOUT = R.layout.items_for_sale_fragment
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var adapter: ItemRecyclerViewAdapter
    private lateinit var ref: DatabaseReference
    private lateinit var postButton: Button
    private lateinit var mActivity: MainActivity


    override fun onAttach(context: Context?) {
        Log.d(TAG, "On Attach")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "On Create")
        mActivity = activity as MainActivity
        ref = FirebaseDatabase.getInstance().getReference("Items")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "On Create View")
        val view = inflater.inflate(LAYOUT, container, false)
        mRecyclerView = view.findViewById(R.id.recyclerView)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this.context)
        mRecyclerView.addOnScrollListener(MainActivity.onScrollListener(mActivity))
        postButton = view.findViewById(R.id.postButton)
        postButton.setOnClickListener { Button ->
            when (Button.id) {
                R.id.postButton -> mActivity.showPostItemFragment(null)
            }
        }
        getItemsFromDb()
        return view
    }

    override fun onStart() {
        Log.d(TAG, "On Start")
        super.onStart()
    }

    override fun onResume() {
        Log.d(TAG, "On Resume")
        super.onResume()
    }

    override fun onPause() {
        Log.d(TAG, "On Pause")
        super.onPause()
    }

    override fun onStop() {
        Log.d(TAG, "On Stop")
        super.onStop()
    }

    override fun onDestroy() {
        Log.d(TAG, "On Destroy")
        super.onDestroy()
    }

    override fun onDetach() {
        Log.d(TAG, "On Detach")
        super.onDetach()
    }

    override fun onDestroyView() {
        Log.d(TAG, "On Destroy View")
        super.onDestroyView()
    }


    fun getItemsFromDb() {
        adapter = ItemRecyclerViewAdapter(mActivity.itemsForSaleList, mActivity, this, false)
        mRecyclerView.adapter = adapter
    }



}