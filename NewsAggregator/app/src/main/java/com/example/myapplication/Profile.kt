package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Model.CategoryModel
import com.example.myapplication.adapters.Category
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class Profile : AppCompatActivity() {

    lateinit var category: EditText
    lateinit var add: Button
    lateinit var categoryList: RecyclerView
    val categorysList = ArrayList<String>()
    val mAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    var currentUid = mAuth.currentUser?.uid
    val dbfollowing = database.getReference("User-following")
    val dbcategories = database.getReference("categories")
    val list = ArrayList<CategoryModel>()
    val myRef = database.getReference("User-following").child(currentUid!!)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        category = findViewById<EditText>(R.id.add_category)
        add = findViewById<Button>(R.id.add_button)

        fetchUsersFollowing()

        val categoryModelArrayList = populateList()

        //Set Recycler view
        val recyclerView =  findViewById<RecyclerView>(R.id.categoryLists)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        val adapter = Category(categoryModelArrayList)
        recyclerView.adapter = adapter

        add.setOnClickListener {
            addCategory()
        }
    }

    private fun addCategory() {
        val newCategory = category.text.toString().trim()
        val isValid = validateCategory(newCategory)

        if (isValid) {
            dismissKeyBoard()
            categorysList.add(newCategory)
            category.text.clear()
            sendDataToDatabase(newCategory)
        }
    }


    private fun sendDataToDatabase(category: String) {
        val categoryId = dbcategories.push()
        val categoryKey = categoryId.key

        if (categoryKey != null) {
            dbfollowing.child(currentUid!!).child(categoryKey).child("category").setValue(category)
        }
        if (categoryKey != null) {
            dbfollowing.child(currentUid!!).child(categoryKey).child("notifications").setValue("true")
        }

    }

    private fun populateList() : MutableList<CategoryModel> {

        for (i in categorysList.indices) {
            val categoryModel = CategoryModel()
            categoryModel.setCategories(categorysList.get(i))
            list.add(categoryModel)
        }
        return list
    }

    private fun fetchUsersFollowing() {

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for (childSnapshot in snapshot.children) {
                    if (childSnapshot.hasChildren()) {
                        var test = childSnapshot.child("category").value
                        val myCategory = CategoryModel()
                        myCategory.setCategories(test.toString())
                        list.add(myCategory)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Database Error", error.toString())
            }
        })
    }

    private fun validateCategory(newCategory: String): Boolean {

        if(newCategory.isEmpty()) {
            category.error = "Category is required"
            return false
        }
        return true
    }

    private fun dismissKeyBoard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}