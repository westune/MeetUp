package com.example.lorenzo.meetup2

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.example.lorenzo.meetup2.fragments.*
import com.google.android.gms.common.api.GoogleApiClient
import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.gms.common.ConnectionResult
import com.google.firebase.storage.StorageReference

/*
TUTORIALS FOLLOWED:
https://www.youtube.com/watch?v=VqEayo28VX8&t=505s


I HAVE EDITED THE CODE GIVEN, BUT DON'T TAKE FULL CREDIT FOR IT
 */

class MainActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener, AdapterView.OnItemSelectedListener {


    //Fragment Manager
    private val manager = supportFragmentManager
    //Firebase Auth
    private var mAuth:FirebaseAuth? = null
    private var mUser:FirebaseUser? = null
    var sUserName:String = ""
    var sLocation: Location? = null
    var sUserEmail:String = ""
    private lateinit var mLocationManager: LocationManager
    private var permission = arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_buy -> {
                showBuyListFragment()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_sell -> {
                showItemsForSaleFragment()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_messages -> {
                showConversationsFragment()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onConnectionFailed(p0: ConnectionResult){}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth!!.currentUser
        mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        setContentView(R.layout.activity_main)


        if(checkSelfPermission(permission[0]) == PackageManager.PERMISSION_DENIED || checkSelfPermission(permission[1]) == PackageManager.PERMISSION_DENIED){
            requestPermissions(permission, 0)
        }


        if(mUser == null){
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
            return
        } else {
            sUserName = mUser!!.displayName!!
            sUserEmail = FirebaseAuth.getInstance().currentUser!!.email.toString()
        }
        showBuyListFragment()
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        /*
        when(item.itemId){
            R.id.sign_out_menu ->{
                mAuth!!.signOut()
                Auth.GoogleSignInApi.signOut(mGoogleApiClient)
                mUserName = ""
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> {
                Log.d(TAG, "yes")
                return super.onOptionsItemSelected(item)
            }
        }
        */
        return super.onOptionsItemSelected(item)
    }

    fun showBuyListFragment(){
        val transaction = manager.beginTransaction()
        val fragment:Fragment?
        if(manager.findFragmentByTag("buy") == null){
            fragment = BuyListFragment()
            transaction.add(fragment, "buy")
            transaction.addToBackStack(null)
        }else{
            fragment = manager.findFragmentByTag("buy")
        }
        transaction.replace(R.id.fragment_layout, fragment!!)
        transaction.commit()
    }

    fun showItemsForSaleFragment(){
        val transaction = manager.beginTransaction()
        val fragment:Fragment?
        if(manager.findFragmentByTag("itemsforsale") == null){
            fragment = ItemsForSaleFragment()
            transaction.add(fragment, "itemsforsale")
            transaction.addToBackStack(null)
        }else{
            fragment = manager.findFragmentByTag("itemsforsale")
        }
        transaction.replace(R.id.fragment_layout, fragment!!)
        transaction.commit()
    }

    fun showPostItemFragment(){
        val transaction = manager.beginTransaction()
        val fragment:Fragment?
        if(manager.findFragmentByTag("postitem") == null){
            fragment = PostItemFragment()
            transaction.add(fragment, "postitem")
            transaction.addToBackStack(null)
        }else{
            fragment = manager.findFragmentByTag("postitem")
        }
        transaction.replace(R.id.fragment_layout, fragment!!)
        transaction.commit()
    }

    fun showConversationsFragment(){
        val transaction = manager.beginTransaction()
        val fragment:Fragment?
        if(manager.findFragmentByTag("conversations") == null){
            fragment = chatFragment()
            transaction.add(fragment, "conversations")
            transaction.addToBackStack(null)
        }else{
            fragment = manager.findFragmentByTag("conversations")
        }
        transaction.replace(R.id.fragment_layout, fragment!!)
        transaction.commit()
    }


    override fun onNothingSelected(view: AdapterView<*>?) {}
    override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, p3: Long) {}

    @SuppressLint("MissingPermission")
    fun getZip():String {
        val hasGps = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        var result = ""
        if (hasGps) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0F, object : LocationListener {
                override fun onLocationChanged(loc: Location?) {
                    if (loc != null) {
                        sLocation = loc
                    }
                }

                override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?){}
                override fun onProviderEnabled(p0: String?) {}
                override fun onProviderDisabled(p0: String?) {}

            })

            val localGpsLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (localGpsLocation != null) {
                sLocation = localGpsLocation
                val geocoder = Geocoder(this)
                val address = geocoder.getFromLocation(sLocation!!.latitude, sLocation!!.longitude, 1)
                result =  address[0].postalCode
            }else{
                Toast.makeText(this, "Unable To Use Location", Toast.LENGTH_LONG).show()
            }
        } else {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
        return result
    }

    fun getAddress(zip:String): Address? {
        val geocoder = Geocoder(this)
        val address = geocoder.getFromLocationName(zip, 1)
        if(address == null || address.size == 0) {return null}
        else return address[0]
    }


}
