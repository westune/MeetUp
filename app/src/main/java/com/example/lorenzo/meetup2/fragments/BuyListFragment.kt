package com.example.lorenzo.meetup2.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.location.*
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatImageButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.example.lorenzo.meetup2.MainActivity
import com.example.lorenzo.meetup2.R
import com.example.lorenzo.meetup2.model.Item
import com.example.lorenzo.meetup2.model.ItemRecyclerViewAdapter
import com.google.firebase.database.*
/*
TUTORIALS FOLLOWED:
https://www.youtube.com/playlist?list=PLk7v1Z2rk4hj6SDHf_YybDeVhUT9MXaj1


I HAVE EDITED THE CODE GIVEN, BUT DON'T TAKE FULL CREDIT FOR IT
*/
class BuyListFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private val LOG = "Buy List Fragment: "
    private lateinit var spinner: Spinner
    private lateinit var mZipText: EditText
    private val LAYOUT = R.layout.buy_list_fragment
    private lateinit var locationManager: LocationManager
    private lateinit var locationButton: AppCompatImageButton
    private lateinit var mActivity:MainActivity
    private var distance: Int = 10
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemRecyclerViewAdapter
    private var list: MutableList<Item> = mutableListOf()
    private lateinit var ref: DatabaseReference
    private val latMile = .016666666667
    private val lonMile = .019999999999



    override fun onNothingSelected(p0: AdapterView<*>?) {}

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        Log.d("POSITION", position.toString())
        when (position) {
            0 -> distance = 10
            1 -> distance = 25
            2 -> distance = 50
            3 -> distance = 100
        }
        if(mZipText.text.toString() != "") {
            val address = mActivity.getAddress(mZipText.text.toString())
            if (address != null) {
                getItemsFromDb(address.longitude, address.latitude)
            }
        }
    }

    override fun onAttach(context: Context?) {
        Log.d(LOG, "On Attach")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ref = FirebaseDatabase.getInstance().getReference("Items")
        mActivity =  activity as MainActivity
        locationManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        Log.d(LOG, "On Create")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(LOG, "On Create View")
        val view = inflater.inflate(LAYOUT, container, false)

        //Initialize UI Elements
        spinner = view.findViewById(R.id.radius_spinner)
        mZipText = view.findViewById(R.id.ZipText)
        locationButton = view.findViewById(R.id.locationButton)
        locationButton.setOnClickListener { Button ->
            when (Button.id) {
                R.id.locationButton -> setZip()
            }
        }

        mZipText.setOnFocusChangeListener{_, hasFocus ->
            if(!hasFocus) {
                //hides keyboard
                (activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(mZipText.windowToken, 0)
                val address = mActivity.getAddress(mZipText.text.toString())
                if(address == null){
                    mZipText.error = "Please Enter Valid Zip"
                } else {
                    getItemsFromDb(address.longitude, address.latitude)
                }
            } else {
                // has focus
            }
        }
        setUpSpinner()
        getItemsFromDb()
        recyclerView = view!!.findViewById(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.addOnScrollListener(MainActivity.onScrollListener(mActivity))
        return view
    }

    private fun setUpSpinner(){
        ArrayAdapter.createFromResource(
                this.context!!,
                R.array.distances,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
            spinner.onItemSelectedListener = this
        }
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
        Log.d(LOG, "On Destroy")
        super.onDestroyView()
    }

    @SuppressLint("MissingPermission")
    private fun setZip() {
        mZipText.setText(mActivity.getZip())
    }

    private fun getItemsFromDb() {
        list.clear()
        val query = ref.orderByChild("lon")
        val fragment = this
        val thread = Thread {
            query.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(data: DataSnapshot) {
                    if (data.exists()) {
                        for (i in data.children) {
                            if((i.getValue(Item::class.java) as Item).seller != mActivity.sUserEmail)
                            list.add(i.getValue(Item::class.java)!!)
                        }
                    }
                    adapter = ItemRecyclerViewAdapter(list, mActivity, fragment, false)
                    recyclerView.adapter = adapter
                }
            })
        }
        thread.run()
    }



    private fun getItemsFromDb(lon:Double, lat:Double) {
        Log.d("DISTANCE", distance.toString())
        list.clear()
        val query = ref.orderByChild("lat")
                .startAt((lat - latMile * distance))
                .endAt((lat + latMile * distance))
        val fragment = this
        val thread = Thread {
            query.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(data: DataSnapshot) {
                    if (data.exists()) {
                        for (i in data.children) {
                            val item = (i.getValue(Item::class.java) as Item)
                            Log.d(item.name, item.lon.toString() + " " + lon)
                            if( item.lon >= (lon - lonMile * distance) && item.lon <= (lon + lonMile * distance) && item.seller != mActivity.sUserEmail) {
                                list.add(i.getValue(Item::class.java)!!)
                            }
                        }
                    }
                    adapter = ItemRecyclerViewAdapter(list, mActivity, fragment , false)
                    recyclerView.adapter = adapter
                }
            })
        }
        thread.run()
    }
}