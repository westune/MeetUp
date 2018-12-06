package com.example.lorenzo.meetup2.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.*
import android.os.Bundle
import android.provider.Settings
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
import com.example.lorenzo.meetup2.R
import com.example.lorenzo.meetup2.model.Item
import com.example.lorenzo.meetup2.model.RecyclerViewAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class BuyListFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private val LOG = "Buy List Fragment: "
    private lateinit var spinner: Spinner
    private lateinit var zip: EditText
    private val LAYOUT = R.layout.buy_list_fragment
    private lateinit var locationManager: LocationManager
    private var location: Location? = null
    private lateinit var locationButton: AppCompatImageButton
    private var distance: Int = 10
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerViewAdapter
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
    }

    override fun onAttach(context: Context?) {
        Log.d(LOG, "On Attach")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ref = FirebaseDatabase.getInstance().getReference("Items")
        locationManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        Log.d(LOG, "On Create")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(LOG, "On Create View")
        val view = inflater.inflate(LAYOUT, container, false)

        //Initialize UI Elements
        spinner = view.findViewById(R.id.radius_spinner)
        zip = view.findViewById(R.id.ZipText)
        locationButton = view.findViewById(R.id.locationButton)
        locationButton.setOnClickListener { Button ->
            when (Button.id) {
                R.id.locationButton -> setZip()
            }
        }

        zip.setOnFocusChangeListener{ v, hasFocus ->
            if(!hasFocus) {
                //hides keyboard
                (activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(zip.windowToken, 0)
                val address = getAddress()
                if(address == null){
                    zip.error = "Please Enter Valid Zip"
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
            spinner.onItemSelectedListener = context as AdapterView.OnItemSelectedListener
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

        val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (hasGps) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0F, object : LocationListener {
                override fun onLocationChanged(loc: Location?) {
                    if (loc != null) {
                        location = loc
                    }
                }

                override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?){}
                override fun onProviderEnabled(p0: String?) {}
                override fun onProviderDisabled(p0: String?) {}

            })

            val localGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (localGpsLocation != null) {
                location = localGpsLocation
                val geocoder = Geocoder(activity)
                val address = geocoder.getFromLocation(location!!.latitude, location!!.longitude, 1)
                zip.setText(address[0].postalCode.toString())
            }else{
                Toast.makeText(this.activity, "Unable To Use Location", Toast.LENGTH_LONG).show()
            }
        } else {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            return
        }
    }

    private fun getItemsFromDb() {
        list.clear()
        val query = ref.orderByChild("lon")
        val thread = Thread {
            query.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(data: DataSnapshot) {
                    if (data.exists()) {
                        for (i in data.children) {
                            list.add(i.getValue(Item::class.java)!!)
                        }
                    }
                    adapter = RecyclerViewAdapter(list, activity!!.applicationContext, fragmentManager!!)
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
        val thread = Thread {
            query.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(data: DataSnapshot) {
                    if (data.exists()) {
                        for (i in data.children) {
                            val item = (i.getValue(Item::class.java) as Item)
                            Log.d(item.name, item.lon.toString() + " " + lon)
                            if( item.lon >= (lon - lonMile * distance) && item.lon <= (lon + lonMile * distance)) {
                                list.add(i.getValue(Item::class.java)!!)
                            }
                        }
                    }
                    adapter = RecyclerViewAdapter(list, activity!!.applicationContext, fragmentManager!!)
                    recyclerView.adapter = adapter
                }
            })
        }
        thread.run()
    }

    private fun getAddress(): Address? {
        val geocoder = Geocoder(activity)
        val address = geocoder.getFromLocationName(zip.text.toString(), 1)
        if(address == null || address.size == 0) {return null}
        else return address[0]
    }


}