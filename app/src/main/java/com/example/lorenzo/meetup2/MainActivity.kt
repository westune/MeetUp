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
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.example.lorenzo.meetup2.fragments.*
import com.example.lorenzo.meetup2.model.Item
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
https://stackoverflow.com/questions/44777869/hide-show-bottomnavigationview-on-scroll
https://www.clipartmax.com/download/m2i8d3b1d3K9b1K9_power-plug-icon-power-plug-icon-png/
https://www.youtube.com/watch?v=WeoryL3XyA4
https://www.youtube.com/watch?v=USbTcGx1mD0&list=PLk7v1Z2rk4hjHrGKo9GqOtLs1e2bglHHA
https://www.youtube.com/watch?v=_TFgmRy6X28&list=PLk7v1Z2rk4hhmWDU41pJ_qjBJvcI7Wo13
https://www.youtube.com/watch?v=Yhc4JkK-h8Y&t=576s
https://www.youtube.com/watch?v=on_OrrX7Nw4
I HAVE EDITED THE CODE GIVEN, BUT DON'T TAKE FULL CREDIT FOR IT
 */

class MainActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener, AdapterView.OnItemSelectedListener {


    //Fragment Manager
    private val mManager = supportFragmentManager
    //Firebase Auth
    private var mAuth: FirebaseAuth? = null
    private var mUser: FirebaseUser? = null
    var sUserName: String = ""
    var sLocation: Location? = null
    var sUserEmail: String = ""
    private lateinit var mGoogleApiClient: GoogleApiClient
    private lateinit var mLocationManager: LocationManager
    var sPermission = arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
    var sSellList: MutableList<Item> = mutableListOf()
    var itemsForSaleList: MutableList<Item> = mutableListOf()
    var sBottomNavBarPosition: Float = 0F
    lateinit var sMenu: Menu

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_buy -> {
                showBuyListFragment()
                showBottomNav()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_sell -> {
                checkIfUserIsSignedIn()
                showItemsForSaleFragment()
                showBottomNav()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_messages -> {
                checkIfUserIsSignedIn()
                showConvoFragment()
                showBottomNav()
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
        if (mUser != null) {
            sUserName = mUser!!.displayName!!
            sUserEmail = FirebaseAuth.getInstance().currentUser!!.email.toString()
            getItemsFromDb(sUserEmail)
        }
        getItemsFromDb()
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

    }

    fun checkIfUserIsSignedIn() {
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
        sMenu = menu!!
        if (sUserName == "") {
            menu.findItem(R.id.sign_out_menu).title = "Sign In"
        } else {
            menu.findItem(R.id.sign_out_menu).title = "Sign Out"
        }
        return true
    }


    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.sign_out_menu -> {
                if (sUserName != "") {
                    mAuth!!.signOut()
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient)
                    sUserName = ""
                    sUserEmail = ""
                    this.sMenu.findItem(R.id.sign_out_menu).title = "Sign Out"
                    showBuyListFragment()
                } else {
                    val intent = Intent(this, SignInActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        return true
    }


    private fun showBuyListFragment() {
        val transaction = mManager.beginTransaction()
        val fragment: Fragment?
        if (mManager.findFragmentByTag("buy") == null) {
            fragment = BuyListFragment()
            transaction.add(fragment, "buy")
            transaction.addToBackStack(null)
        } else {
            fragment = mManager.findFragmentByTag("buy")
        }
        transaction.replace(R.id.fragment_layout, fragment!!)
        transaction.commit()
    }


    fun showChatFragment(bundle: Bundle?) {
        val transaction = mManager.beginTransaction()
        val fragment: Fragment?
        if (mManager.findFragmentByTag("chat") == null) {
            fragment = ChatFragment()
            transaction.add(fragment, "chat")
            transaction.addToBackStack(null)
        } else {
            fragment = mManager.findFragmentByTag("chat")
        }
        transaction.replace(R.id.fragment_layout, fragment!!)
        transaction.commit()
        if (bundle != null) {
            fragment.arguments = bundle
        }
    }

    private fun showConvoFragment() {
        val transaction = mManager.beginTransaction()
        val fragment: Fragment?
        if (mManager.findFragmentByTag("convo") == null) {
            fragment = ConvosFragment()
            transaction.add(fragment, "convo")
            transaction.addToBackStack(null)
        } else {
            fragment = mManager.findFragmentByTag("convo")
        }
        transaction.replace(R.id.fragment_layout, fragment!!)
        transaction.commit()
    }

    fun showItemsForSaleFragment() {
        val transaction = mManager.beginTransaction()
        val fragment: Fragment?
        if (mManager.findFragmentByTag("itemsforsale") == null) {
            fragment = ItemsForSaleFragment()
            transaction.add(fragment, "itemsforsale")
            transaction.addToBackStack(null)
        } else {
            fragment = mManager.findFragmentByTag("itemsforsale")
        }
        transaction.replace(R.id.fragment_layout, fragment!!)
        transaction.commit()
    }

    fun showPostItemFragment(bundle: Bundle?) {
        val transaction = mManager.beginTransaction()
        val fragment: Fragment?
        if (mManager.findFragmentByTag("postitem") == null) {
            fragment = PostItemFragment()
            transaction.add(fragment, "postitem")
            transaction.addToBackStack(null)
        } else {
            fragment = mManager.findFragmentByTag("postitem")
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
        if (checkSelfPermission(sPermission[0]) == PackageManager.PERMISSION_DENIED || checkSelfPermission(sPermission[1]) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(sPermission, 0)
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
                    sSellList.clear()
                    if (data.exists()) {
                        for (i in data.children) {
                            sSellList.add(i.getValue(Item::class.java)!!)
                            val sItemsForSale = mManager.findFragmentByTag("itemsforsale")
                            if (sItemsForSale != null) {
                                (sItemsForSale as ItemsForSaleFragment).getItemsFromDb()
                            }
                        }
                    }
                }
            })
        }
        thread.run()
    }

    private fun getItemsFromDb(seller: String) {
        val query = FirebaseDatabase.getInstance().getReference("Items").orderByChild("seller").equalTo(seller)
        val thread = Thread {
            query.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(data: DataSnapshot) {
                    itemsForSaleList.clear()
                    Log.d("FUCK", data.toString())
                    if (data.exists()) {
                        for (i in data.children) {

                            itemsForSaleList.add(i.getValue(Item::class.java)!!)
                            val itemsForSale = mManager.findFragmentByTag("itemsforsale")
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

    class onScrollListener(private val mActivity: MainActivity) : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            when {
                dy > 0 && mActivity.sBottomNavBarPosition != 160F -> {
                    mActivity.sBottomNavBarPosition += 10
                    mActivity.navigation.translationY = mActivity.sBottomNavBarPosition
                }
                dy < 0 && mActivity.sBottomNavBarPosition != 0F -> {
                    mActivity.sBottomNavBarPosition -= 10
                    mActivity.navigation.translationY = mActivity.sBottomNavBarPosition
                }
            }


        }
    }

    fun showBottomNav() {
        sBottomNavBarPosition = 0F
        navigation.translationY = sBottomNavBarPosition
    }
}
