package com.example.lorenzo.meetup2

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatImageButton
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.lorenzo.meetup2.model.Item
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import java.io.Console

class PostItemFragment:Fragment(){

    private val LOG = "Post Item Fragment"
    private val LAYOUT = R.layout.post_item_fragment
    private lateinit var priceText:EditText
    private lateinit var descriptionText:EditText
    private lateinit var nameText:EditText
    private lateinit var zipText:EditText
    private lateinit var postButton:Button
    private lateinit var locationButton:AppCompatImageButton
    private lateinit var locationManager: LocationManager
    private var hasGps = false
    private var hasInternet = false
    private lateinit var locationGps: Location
    private lateinit var locationNewtwork: Location

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
        priceText = view.findViewById(R.id.price)
        nameText = view.findViewById(R.id.name)
        descriptionText = view.findViewById(R.id.description)
        zipText = view.findViewById(R.id.zip)
        postButton = view.findViewById(R.id.postButton)
        locationButton = view.findViewById(R.id.locationButton)
        postButton.setOnClickListener{Button ->
            when(Button.id){
                R.id.postButton -> postItem(
                            nameText.text.toString().trim(),
                            descriptionText.text.toString().trim(),
                            priceText.text.toString().trim(),
                            zipText.text.toString().trim())
            }
        }

        locationButton.setOnClickListener{Button ->
            when(Button.id){
                R.id.locationButton -> getZip()
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
        val user = FirebaseAuth.getInstance().currentUser!!.email.toString()
        val newItem = Item(id!!, description, name, zip, price, user)
        ref.child(id).setValue(newItem).addOnCompleteListener{
            Toast.makeText(this.context, "Item Posted!", Toast.LENGTH_LONG)
        }
        showPostItems()
    }

    private fun showPostItems(){
        val transaction = fragmentManager!!.beginTransaction()
        val fragment = ItemsForSaleFragment()
        transaction.replace(R.id.fragment_layout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    @SuppressLint("MissingPermission")
    private fun getZip(){
        locationManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        hasInternet = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if(hasGps || hasInternet){

            if(hasGps){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0F, object: LocationListener{
                    override fun onLocationChanged(location: Location?) {
                        if(location != null){
                            locationGps = location
                        }
                    }

                    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

                    }

                    override fun onProviderEnabled(p0: String?) {

                    }

                    override fun onProviderDisabled(p0: String?) {

                    }

                })

                val localGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if(localGpsLocation != null){
                    locationGps = localGpsLocation
                }
            }else{
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0F, object: LocationListener{
                    override fun onLocationChanged(location: Location?) {
                        if(location != null){
                            locationNewtwork = location
                        }
                    }

                    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onProviderEnabled(p0: String?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onProviderDisabled(p0: String?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                })

                val localNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if(localNetworkLocation!= null){
                    locationNewtwork = localNetworkLocation
                }
            }

        }else{
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            return
        }

        Log.d("FUCK", locationGps.latitude.toString() + locationGps.longitude)
    }

}