package com.example.myapplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.adapters.TabAdapter
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlin.collections.ArrayList

class Dashboard : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    //Variables
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    private lateinit var auth: FirebaseAuth
    private val mAuth = FirebaseAuth.getInstance()
    var list: ArrayList<String> = ArrayList()

    var database = FirebaseDatabase.getInstance()

    private val CHANNEL_ID = "CHANNEL ID"
    private val notificationId = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        createNotificationChannel()
        sendNotification()

        checkTheme()

        //Initialize Firebase Auth instance.
        auth = FirebaseAuth.getInstance()

        drawerLayout = findViewById<DrawerLayout>(R.id.drawer)
        navigationView = findViewById<NavigationView>(R.id.nav_view)

        val myToolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(myToolbar)

        navigationView.bringToFront()
        val toggle = ActionBarDrawerToggle(this,
            drawerLayout,
            myToolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener(this)
        navigationView.setCheckedItem(R.id.nav_home)

        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        val viewPager = findViewById<ViewPager2>(R.id.pager)
        val tabTitles = resources.getStringArray(R.array.tabTitles)

        viewPager.adapter = TabAdapter(this)
        TabLayoutMediator(tabLayout,
            viewPager,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                when (position) {
                    0 -> tab.text = tabTitles[0]
                    1 -> tab.text = tabTitles[1]
                    2 -> tab.text = tabTitles[2]
                }
            }).attach()
    }


    //Create the notifications channel
    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notification Title"
            val descriptionText = "Notification Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID,name,importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    //Send notification to the user.
    private fun sendNotification() {

        val intent = Intent(this, Dashboard::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent,0)
        val bitmap = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.profile_icon)
        val bitmapLargeIcon = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.profile_icon)

        getInterests(object:InterestsCallback {
            override fun onCallback(value: ArrayList<String>) {

                val list = generateList(value)
                val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID).setSmallIcon(R.drawable.profile_icon)
                    .setContentTitle("Don't forget to read the latest articles related to:")
                    .setContentText("Click for more info")
                    .setLargeIcon(bitmapLargeIcon)
                    .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                    .setStyle(NotificationCompat.BigTextStyle().bigText(list))
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                with(NotificationManagerCompat.from(applicationContext)) {
                    notify(notificationId, builder.build())
                }
            }
        })
    }

    /*
    *
    * generateList: Creates the list of categories to display
    * in a readable state when pushing notifications from  the list passed in as a parameter
    *
    * */
    private fun generateList(list: ArrayList<String>) : String {

        var stringList = ""

        for (category in list.iterator()) {
            stringList += if (stringList == "") {
                "$category"
            } else {
                ", $category"
            }
        }

        return "$stringList. \nTap to view articles."
    }


    private fun getInterests(myCallback: InterestsCallback)  {

        val currentUid = mAuth.currentUser?.uid
        val myRef = database.getReference("User-following").child(currentUid!!)

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for (childSnapshot in snapshot.children) {
                    if (childSnapshot.hasChildren()) {
                        if (childSnapshot.child("notifications").value.toString() == "true") {
                            val category = childSnapshot.child("category").value.toString()
                            list.add(category)
                        }
                    }
                }
                myCallback.onCallback(list)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Database Error", error.toString())
            }
        })
    }

    interface InterestsCallback {
        fun onCallback(value:ArrayList<String>)
    }


    override fun onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer((GravityCompat.START))
        } else {
            super.onBackPressed()
        }
    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_home -> {

            }
            R.id.nav_profile -> {
                val intent = Intent(this, Profile::class.java)
                startActivity(intent)
            }
            R.id.nav_saved -> {
                val intent = Intent(this, SavedStories::class.java)
                startActivity(intent)
            }
            R.id.nav_settings -> {
                val intent = Intent(this, Settings::class.java)
                startActivity(intent)
            }

            R.id.nav_signout -> {
                auth.signOut()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                Toast.makeText(this, "Signed Out", Toast.LENGTH_SHORT).show()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


    private fun checkTheme() {
        when (MyPreferences(this).darkMode) {
            0 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                delegate.applyDayNight()
            }
            1 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                delegate.applyDayNight()
            }
            2 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                delegate.applyDayNight()
            }
        }
    }
}
