package com.example.lorenzo.meetup2.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.lorenzo.meetup2.MainActivity
import com.example.lorenzo.meetup2.R
import com.example.lorenzo.meetup2.model.Convo
import com.example.lorenzo.meetup2.model.ConvosRecyclerViewAdapter
import com.example.lorenzo.meetup2.model.Item
import com.example.lorenzo.meetup2.model.ItemRecyclerViewAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ConvosFragment : Fragment() {

    private val LOG = "CONVOS Fragment"
    private val LAYOUT = R.layout.convos_fragment
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var buyAdapter: ItemRecyclerViewAdapter
    private lateinit var sellAdapter: ItemRecyclerViewAdapter
    private var buyConvoList: HashMap<String, Item> = HashMap()
    private lateinit var mBuyButton: Button
    private lateinit var mSellButton: Button

    private lateinit var ref: DatabaseReference
    private lateinit var mActivity: MainActivity
    private lateinit var fragment: Fragment

    override fun onAttach(context: Context?) {
        Log.d(LOG, "On Attach")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(LOG, "On Create")
        ref = FirebaseDatabase.getInstance().getReference("Items")
        fragment = this
        mActivity = activity as MainActivity
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(LOG, "On Create View")
        val view = inflater.inflate(LAYOUT, container, false)
        mRecyclerView = view.findViewById(R.id.recyclerView)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this.context)
        mSellButton = view.findViewById(R.id.sell_button)
        mBuyButton = view.findViewById(R.id.buy_button)
        buyAdapter = ItemRecyclerViewAdapter(
                buyConvoList.values.toList(), mActivity, fragment, false)
        sellAdapter = ItemRecyclerViewAdapter(mActivity.sellList, mActivity, this, true)
        setUpButtons()
        checkBuy()
        mActivity.checkIfUserIsSignedIn()
        return view
    }

    private fun checkBuy(){
        if (!buyConvoList.isEmpty()) {
            mRecyclerView.adapter = buyAdapter
        } else {
            getBuyConvos()
        }

    }

    private fun setUpButtons() {
        mBuyButton.setOnClickListener {
            checkBuy()
        }

        mSellButton.setOnClickListener {
            mRecyclerView.adapter = sellAdapter

        }
    }


    private fun getBuyConvos() {
        val thread: Thread
        val query = FirebaseDatabase.getInstance().getReference("users/${mActivity.sUserName}").orderByValue()
        thread = Thread {
            query.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(data: DataSnapshot) {
                    if (data.exists()) {
                        for (i in data.children) {
                            getItem(i.getValue(String::class.java)!!)
                        }
                    }
                }
            })
        }
        thread.run()
    }

    private fun getItem(itemId: String) {
        var thread = Thread()
        val query = FirebaseDatabase.getInstance().getReference("Items/$itemId").orderByValue()
        thread = Thread {
            query.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(data: DataSnapshot) {
                    if (data.exists()) {
                        val item = data.getValue(Item::class.java)!!
                        if (!buyConvoList.contains(item.id)) {
                            buyConvoList.put(item.id ,data.getValue(Item::class.java)!!)
                            buyAdapter = ItemRecyclerViewAdapter(buyConvoList.values.toList(), mActivity, fragment, false)
                            mRecyclerView.adapter = buyAdapter
                        }
                    }
                    thread.interrupt()
                }
            })
        }
        thread.run()

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