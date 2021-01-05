package com.example.myapplication.adapters

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Constants
import com.example.myapplication.R
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_trending.*
import okhttp3.*
import java.io.IOException

class TrendingFragment : Fragment() {

    lateinit var spinner : Spinner
    private var countryList = arrayOf("Business","Entertainment","General","Health","Science","Sports","Technology","All")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.fragment_trending, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Bind views
        val list = view.findViewById<RecyclerView>(R.id.trendingRecyclerView)

        //create layout manager
        val layoutManager = LinearLayoutManager(context)
        list.layoutManager = layoutManager


        spinner = view.findViewById(R.id.sp_trending_options) as Spinner

        val adapter = ArrayAdapter<String>(view.context, android.R.layout.simple_list_item_1,countryList )
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                fetchNews(countryList[position])
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        val refresh = view.findViewById<Button>(R.id.refresh)
        refresh.setOnClickListener {
            val refreshCat = spinner.getItemAtPosition(0).toString()
            fetchNews(refreshCat)
            spinner.setSelection(0)
            val toast = Toast.makeText(view.context, "Headlines Feed Refreshed", Toast.LENGTH_LONG).show()

        }
    }

    private fun fetchNews(category: String) {
        var category = category
        var url = ""

        when (category) {
            "Business" -> {
                url = " http://newsapi.org/v2/top-headlines?country=gb&category=business&pageSize=40&apiKey=${Constants.API_KEY}"

            }
            "Entertainment" -> {
                url = " http://newsapi.org/v2/top-headlines?country=gb&category=entertainment&pageSize=40&apiKey=${Constants.API_KEY}"
            }
            "General" -> {
                url = " http://newsapi.org/v2/top-headlines?country=gb&category=general&pageSize=40&apiKey=${Constants.API_KEY}"

            }
            "Health" -> {
                url = " http://newsapi.org/v2/top-headlines?country=gb&category=health&pageSize=40&apiKey=${Constants.API_KEY}"

            }
            "Science" -> {
                url = " http://newsapi.org/v2/top-headlines?country=gb&category=science&pageSize=40&apiKey=${Constants.API_KEY}"

            }
            "Sports" -> {
                url = " http://newsapi.org/v2/top-headlines?country=gb&category=sports&pageSize=40&apiKey=${Constants.API_KEY}"

            }
            "Technology"-> {
                url = " http://newsapi.org/v2/top-headlines?country=gb&category=technology&pageSize=40&apiKey=${Constants.API_KEY}"
            }
            "All"  -> {
                url = "http://newsapi.org/v2/top-headlines?country=gb&pageSize=40&apiKey=${Constants.API_KEY}"

            }
        }


        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {

            override fun onResponse(call: Call, response: Response) {
                val body = response?.body?.string()
                val gson = GsonBuilder().create()
                val homeFeed = gson.fromJson(body, HomeFeed::class.java)

                activity?.runOnUiThread {
                    trendingRecyclerView.adapter = MyAdapter(homeFeed)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute request")
            }
        })
    }
}