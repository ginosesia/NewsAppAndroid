package com.example.myapplication.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Model.CategoryModel
import com.example.myapplication.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Category(private val listCategory: MutableList<CategoryModel>) : RecyclerView.Adapter<Category.ViewHolder>() {

    val mAuth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance()
    var currentUid = mAuth.currentUser?.uid
    val myRef = database.getReference("User-following").child(currentUid!!)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.category_layout, parent, false)
        return ViewHolder(v)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val info = listCategory[position]
        val category = info.getCategories()
        holder.category.text = category

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    if (childSnapshot.child("category").value == category) {
                        val status = childSnapshot.child("notifications").value
                        holder.alert.isChecked = status == "true"
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


    override fun getItemCount(): Int {
        return listCategory.size
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var category: TextView = itemView.findViewById(R.id.category)
        var remove: Button = itemView.findViewById(R.id.remove)
        val alert: CheckBox = itemView.findViewById(R.id.alert)


        init {
            remove.setOnClickListener {

                val msg = category.text.toString()
                val snackbar = Snackbar.make(category, "Remove $msg", Snackbar.LENGTH_INDEFINITE)
                snackbar.setAction(R.string.remove) {
                    removeUsersFollowing(msg)
                    val snackbar1 =
                        Snackbar.make(category, "$msg has been removed", Snackbar.LENGTH_SHORT)
                    snackbar1.show()
                }
                snackbar.show()
            }
            alert.setOnCheckedChangeListener { _: CompoundButton, _: Boolean ->
                val cat = category.text
                if (alert.isChecked) {
                     Snackbar.make(itemView,
                        "${category.text} has been added to your notifications list",
                        Snackbar.LENGTH_LONG)
                        .show()
                    fetchNotificationStatus(cat as String, true)
                } else {
                    Snackbar.make(itemView,
                        "${category.text} has been removed from your notifications list",
                        Snackbar.LENGTH_LONG)
                        .show()
                    fetchNotificationStatus(cat as String, false)
                }
            }
        }

        private fun fetchNotificationStatus(category: String, b: Boolean) {

            var status = if (b) {
                "true"
            } else {
                "false"
            }

            val query = myRef.orderByChild("category").equalTo(category);

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        childSnapshot.ref.child("notifications").setValue(status)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Database Error", error.toString())
                }
            })
        }

        private fun removeUsersFollowing(category: String) {

            println("Test is remove is working")

            myRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (childSnapshot in dataSnapshot.children) {
                        if (childSnapshot.hasChildren()) {
                            var value = childSnapshot.child("category").value

                            if (value == category) {
                                val key = childSnapshot.key
                                myRef.child(key!!).removeValue()

                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Database Error", error.toString())
                }
            })
        }
    }
}
