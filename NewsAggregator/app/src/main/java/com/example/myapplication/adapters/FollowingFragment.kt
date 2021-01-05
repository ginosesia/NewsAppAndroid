package com.example.myapplication.adapters

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Constants
import com.example.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_following.*
import okhttp3.*
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class FollowingFragment: Fragment() {

    private val database = FirebaseDatabase.getInstance()
    private val mAuth = FirebaseAuth.getInstance()
    private var currentUid = mAuth.currentUser?.uid

    val categories: ArrayList<String> = ArrayList()

    lateinit var spinner : Spinner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) =
        inflater.inflate(R.layout.fragment_following, container, false)!!

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchUserFollowings(object:FollowingCallback {
            override fun onCallback(value: ArrayList<String>) {

                //Bind views
                val list = view.findViewById<RecyclerView>(R.id.followingRecyclerView)

                //create layout manager
                val layoutManager = LinearLayoutManager(context)
                list.layoutManager = layoutManager

                spinner = view.findViewById(R.id.sp_option) as Spinner
                //Set Dropdown list
                    val adapter = ArrayAdapter<String>(view.context, android.R.layout.simple_list_item_1, categories)

                spinner.adapter = adapter

                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        fetchNews(categories[position])
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        TODO("Not yet implemented")
                    }
                }
            }
        })

            val refresh = view.findViewById<Button>(R.id.refresh)
            refresh.setOnClickListener {
                val refreshCat = spinner.getItemAtPosition(0).toString()
                fetchNews(refreshCat)
                spinner.setSelection(0)
                val toast = Toast.makeText(view.context, "Following Feed Refreshed", Toast.LENGTH_LONG).show()
            }
    }

    private fun fetchNews(category: String) {

        val c = Calendar.getInstance()

        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val date = "$year-${month+1}-$day"
        val dateBefore = "$year-${month+1}-${day-1}"

        val url = "http://newsapi.org/v2/everything?q=$category&from=$dateBefore&to=$date&sortBy=popularity&apiKey=${Constants.API_KEY}"

        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {

            override fun onResponse(call: Call, response: Response) {
                val body = response?.body?.string()
                val gson = GsonBuilder().create()
                val homeFeed = gson.fromJson(body, HomeFeed::class.java)

                activity?.runOnUiThread {
                    followingRecyclerView.adapter = MyAdapter(homeFeed)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute request")
            }
        })
    }

    interface FollowingCallback {
        fun onCallback(value:ArrayList<String>)
    }

    private fun fetchUserFollowings(myCallback:FollowingCallback) {

        val myRef = database.getReference("User-following").child(currentUid!!)

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                categories.clear()
                for (childSnapshot in dataSnapshot.children) {
                    if (childSnapshot.hasChildren()) {
                        var test = childSnapshot.child("category").value
                        categories.add(test as String)
                    }
                }
                myCallback.onCallback(categories)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Database Error", error.toString())
            }
        })
    }
}
