package com.example.lorenzo.meetup2

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val manager = supportFragmentManager

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        showBuyList()
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
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
