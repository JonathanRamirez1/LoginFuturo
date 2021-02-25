package com.jonathan.loginfuturo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.google.firebase.auth.FirebaseAuth
import com.jonathan.loginfuturo.view.adapters.PagerAdapter
import com.jonathan.loginfuturo.view.fragments.ChatFragment
import com.jonathan.loginfuturo.view.fragments.InfoFragment
import com.jonathan.loginfuturo.view.fragments.RatesFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*

class MainActivity : AppCompatActivity() {

    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private var previewBottomSelected : MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /** Toolbar **/
        setSupportActionBar(findViewById(R.id.toolbarView))

       setUpViewPager(getPagerAdapter())
        setUpBottomNavigationBar()
    }

    private fun getPagerAdapter() : PagerAdapter {
        val pagerAdapter = PagerAdapter(supportFragmentManager)
        pagerAdapter.addFragment(InfoFragment())
        pagerAdapter.addFragment(RatesFragment())
        pagerAdapter.addFragment(ChatFragment())
        return pagerAdapter
    }

    private fun setUpViewPager(pagerAdapter: PagerAdapter) {
        viewPager.adapter = pagerAdapter
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {

                if (previewBottomSelected == null) {
                    bottomNavigation.menu.getItem(0).isChecked = false
                } else {
                    previewBottomSelected!!.isChecked = false
                }
                bottomNavigation.menu.getItem(position).isChecked = true
                previewBottomSelected = bottomNavigation.menu.getItem(position)
            }
        })
    }

    private fun setUpBottomNavigationBar() {

        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_nav_info -> {
                    viewPager.currentItem = 0;
                    true
                }
                R.id.bottom_nav_rates -> {
                    viewPager.currentItem = 1;
                    true
                }
                R.id.bottom_nav_chat -> {
                    viewPager.currentItem = 2;
                    true
                }
                else ->
                    false
            }
        }
    }
}