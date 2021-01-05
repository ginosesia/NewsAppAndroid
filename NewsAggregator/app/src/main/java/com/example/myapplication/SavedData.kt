package com.example.myapplication

import android.content.Context

class SavedData(context: Context) {


    private val sharedPreferences = context.getSharedPreferences("myPreferences", 0)

    fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getSting(key: String) : String? {
        return sharedPreferences.getString(key, null)
    }


}