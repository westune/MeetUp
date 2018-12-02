package com.example.lorenzo.meetup2

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatImageButton
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class BuyListFragment:Fragment(),  AdapterView.OnItemSelectedListener{

    private val LOG = "Buy List Fragment: "
    private lateinit var spinner: Spinner
    private lateinit var zip:EditText
    private val LAYOUT = R.layout.buy_list_fragment
    private lateinit var locationManager: LocationManager
    private var hasGps = false
    private var hasInternet = false
    private var location: Location? = null
    private lateinit var locationButton: AppCompatImageButton


    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAttach(context: Context?) {
        Log.d(LOG, "On Attach")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LOG, "On Create")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(LOG, "On Create View")
        val view = inflater.inflate(LAYOUT, container, false)
        spinner = view.findViewById(R.id.radius_spinner)
        zip = view.findViewById(R.id.ZipText)
        locationButton = view.findViewById(R.id.locationButton)
        ArrayAdapter.createFromResource(
                this.context!!,
        R.array.distances,
        android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
            spinner.onItemSelectedListener = activity as AdapterView.OnItemSelectedListener
        }

        locationButton.setOnClickListener { Button ->
            when (Button.id) {
                R.id.locationButton -> setZip()
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
        Log.d(LOG, "On Destroy")
        super.onDestroyView()
    }

    @SuppressLint("MissingPermission")
    private fun setZip() {
        locationManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        hasInternet = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
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
            }
        } else if (hasInternet) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0F, object : LocationListener {
                override fun onLocationChanged(loc: Location?) {
                    if (loc != null) {
                        location = loc
                    }
                }

                override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}
                override fun onProviderEnabled(p0: String?) {}
                override fun onProviderDisabled(p0: String?) { }

            })

            val localNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            if (localNetworkLocation != null) {
                location = localNetworkLocation
            }


        } else {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            return
        }

        val geocoder = Geocoder(activity)
        val address = geocoder.getFromLocation(location!!.latitude, location!!.longitude, 1)
        zip.setText(address[0].postalCode.toString())
    }


}