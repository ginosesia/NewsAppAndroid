package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.adapters.SavedStoriesAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SavedStories : AppCompatActivity() {

    private val mAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val currentUid = mAuth.currentUser?.uid
    private val dbSavedStories = database.getReference("User-Saved-Stories")
    val list = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_stories)

        getCount(object:GetCountCallback {
            override fun onCallback(list: ArrayList<String>) {

                //set recycler view
                val recyclerView = findViewById<RecyclerView>(R.id.saved_stories_recyclerview)
                val layoutManager = LinearLayoutManager(applicationContext)
                recyclerView.layoutManager = layoutManager

                val adapter = SavedStoriesAdapter(list)
                recyclerView.adapter = adapter

            }
        })
    }

    private fun getCount(myCallback: GetCountCallback) {

        val ref = dbSavedStories.child(currentUid!!)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    val key = childSnapshot.key
                    list.add(key.toString())
                }
                myCallback.onCallback(list)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    interface GetCountCallback {
        fun onCallback(list: ArrayList<String>)
    }

}