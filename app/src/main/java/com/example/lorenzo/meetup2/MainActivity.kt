package com.example.lorenzo.meetup2

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.provider.Settings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.example.lorenzo.meetup2.fragments.*
import com.example.lorenzo.meetup2.model.Item
import com.example.lorenzo.meetup2.model.ItemRecyclerViewAdapter
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.api.GoogleApiClient
import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.gms.common.ConnectionResult
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/*
TUTORIALS FOLLOWED:
https://www.youtube.com/watch?v=VqEayo28VX8&t=505s


I HAVE EDITED THE CODE GIVEN, BUT DON'T TAKE FULL CREDIT FOR IT
 */

class MainActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener, AdapterView.OnItemSelectedListener {


    //Fragment Manager
    private val manager = supportFragmentManager
    //Firebase Auth
    private var mAuth: FirebaseAuth? = null
    private var mUser: FirebaseUser? = null
    var sUserName: String = ""
    var sLocation: Location? = null
    var sUserEmail: String = ""
    private lateinit var mGoogleApiClient:GoogleApiClient
    private lateinit var mLocationManager: LocationManager
    var permission = arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
    var sellList: MutableList<Item> = mutableListOf()
    var itemsForSaleList: MutableList<Item> = mutableListOf()

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
                showConvoFragment()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onConnectionFailed(p0: ConnectionResult) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth!!.currentUser
        mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        setContentView(R.layout.activity_main)
        showBuyListFragment()
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build()
        if(mUser != null){
            sUserName = mUser!!.displayName!!
            sUserEmail = FirebaseAuth.getInstance().currentUser!!.email.toString()
        }
        getItemsFromDb()
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    fun checkIfUserIsSignedIn(){
        if (mUser == null) {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
            return
        } else {
            sUserName = mUser!!.displayName!!
            sUserEmail = FirebaseAuth.getInstance().currentUser!!.email.toString()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options, menu)
        if(sUserName == ""){
            menu!!.findItem(R.id.sign_out_menu).title = "Sign In"
        }
        return true
    }


    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
         when(menuItem.itemId){
             R.id.sign_out_menu -> {
                 mAuth!!.signOut()
                 Auth.GoogleSignInApi.signOut(mGoogleApiClient)
                 sUserName = ""
                 sUserEmail = ""
             }
         }
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        return true
    }


    private fun showBuyListFragment() {
        val transaction = manager.beginTransaction()
        val fragment: Fragment?
        if (manager.findFragmentByTag("buy") == null) {
            fragment = BuyListFragment()
            transaction.add(fragment, "buy")
            transaction.addToBackStack(null)
        } else {
            fragment = manager.findFragmentByTag("buy")
        }
        transaction.replace(R.id.fragment_layout, fragment!!)
        transaction.commit()
    }


    fun showChatFragment(bundle: Bundle?) {
        val transaction = manager.beginTransaction()
        val fragment: Fragment?
        if (manager.findFragmentByTag("chat") == null) {
            fragment = ChatFragment()
            transaction.add(fragment, "chat")
            transaction.addToBackStack(null)
        } else {
            fragment = manager.findFragmentByTag("chat")
        }
        transaction.replace(R.id.fragment_layout, fragment!!)
        transaction.commit()
        if (bundle != null) {
            fragment.arguments = bundle
        }
    }

    private fun showConvoFragment() {
        val transaction = manager.beginTransaction()
        val fragment: Fragment?
        if (manager.findFragmentByTag("convo") == null) {
            fragment = ConvosFragment()
            transaction.add(fragment, "convo")
            transaction.addToBackStack(null)
        } else {
            fragment = manager.findFragmentByTag("convo")
        }
        transaction.replace(R.id.fragment_layout, fragment!!)
        transaction.commit()
    }

    fun showItemsForSaleFragment() {
        val transaction = manager.beginTransaction()
        val fragment: Fragment?
        if (manager.findFragmentByTag("itemsforsale") == null) {
            fragment = ItemsForSaleFragment()
            transaction.add(fragment, "itemsforsale")
            transaction.addToBackStack(null)
        } else {
            fragment = manager.findFragmentByTag("itemsforsale")
        }
        transaction.replace(R.id.fragment_layout, fragment!!)
        transaction.commit()
    }

    fun showPostItemFragment(bundle: Bundle?) {
        val transaction = manager.beginTransaction()
        val fragment: Fragment?
        if (manager.findFragmentByTag("postitem") == null) {
            fragment = PostItemFragment()
            transaction.add(fragment, "postitem")
            transaction.addToBackStack(null)
        } else {
            fragment = manager.findFragmentByTag("postitem")
        }

        transaction.replace(R.id.fragment_layout, fragment!!)
        transaction.commit()
        if (bundle != null) {
            fragment.arguments = bundle
        }
    }


    override fun onNothingSelected(view: AdapterView<*>?) {}
    override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, p3: Long) {}

    @SuppressLint("MissingPermission")
    fun getZip(): String {
        var result = ""
        if (checkSelfPermission(permission[0]) == PackageManager.PERMISSION_DENIED || checkSelfPermission(permission[1]) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(permission, 0)
        } else {
            val hasGps = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            if (hasGps) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0F, object : LocationListener {
                    override fun onLocationChanged(loc: Location?) {
                        if (loc != null) {
                            sLocation = loc
                        }
                    }

                    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}
                    override fun onProviderEnabled(p0: String?) {}
                    override fun onProviderDisabled(p0: String?) {}

                })

                val localGpsLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (localGpsLocation != null) {
                    sLocation = localGpsLocation
                    val geoCoder = Geocoder(this)
                    val address = geoCoder.getFromLocation(sLocation!!.latitude, sLocation!!.longitude, 1)
                    result = address[0].postalCode
                } else {
                    Toast.makeText(this, "Unable To Use Location", Toast.LENGTH_LONG).show()
                }
            } else {
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        }
        return result

    }

    fun getAddress(zip: String): Address? {
        val geocoder = Geocoder(this)
        val address = geocoder.getFromLocationName(zip, 1)
        if (address == null || address.size == 0) {
            return null
        } else return address[0]
    }

    private fun getItemsFromDb() {
        val query = FirebaseDatabase.getInstance().getReference("Items").orderByChild("seller")
        val thread = Thread {
            query.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(data: DataSnapshot) {
                    sellList.clear()
                    if (data.exists()) {
                        for (i in data.children) {
                            sellList.add(i.getValue(Item::class.java)!!)
                            val itemsForSale = manager.findFragmentByTag("itemsforsale")
                            if (itemsForSale != null) {
                                (itemsForSale as ItemsForSaleFragment).getItemsFromDb()
                            }
                        }
                    }
                }
            })
        }
        thread.run()
    }
}
