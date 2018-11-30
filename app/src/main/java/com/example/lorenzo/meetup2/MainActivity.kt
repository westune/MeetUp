package com.example.lorenzo.meetup2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import com.google.android.gms.common.api.GoogleApiClient
import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.ConnectionResult

class MainActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    //Fragment Manager
    private val manager = supportFragmentManager
    //Firebase Auth
    private var mAuth:FirebaseAuth? = null
    private var mUser:FirebaseUser? = null
    private var mUserName:String = ""
    private var mPhotoUrl:String = ""
    private var mGoogleApiClient:GoogleApiClient? = null
    private var permission = arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_buy -> {
                showBuyList()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_sell -> {
                showItemsForSale()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_messages -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth!!.currentUser
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
            mUserName = mUser!!.displayName!!
            if(mUser!!.photoUrl != null){
                mPhotoUrl = mUser!!.photoUrl.toString()
            }
        }

        showBuyList()
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

    private fun showBuyList(){
        val transaction = manager.beginTransaction()
        val fragment = BuyListFragment()
        transaction.replace(R.id.fragment_layout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun showItemInfo(){
        val transaction = manager.beginTransaction()
        val fragment = ItemInfoFragment()
        transaction.replace(R.id.fragment_layout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun showItemsForSale(){
        val transaction = manager.beginTransaction()
        val fragment = ItemsForSaleFragment()
        transaction.replace(R.id.fragment_layout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}
