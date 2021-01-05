package com.example.myapplication.adapters

import android.app.Activity
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Constants
import com.example.myapplication.Dashboard
import com.example.myapplication.R
import com.example.myapplication.Settings
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_today.*
import kotlinx.android.synthetic.main.fragment_trending.*
import okhttp3.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest

class TodayFragment : Fragment() {

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var spinner : Spinner
    private var countryList = arrayOf("United Kingdom","Argentina","Australia","France","New Zealand","South Africa","United States","United Arab Emirates")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.fragment_today, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Bind views
        val list = view.findViewById<RecyclerView>(R.id.todayRecyclerView)

        //create layout manager
        val layoutManager = LinearLayoutManager(context)
        list.layoutManager = layoutManager

        spinner = view.findViewById(R.id.sp_today_options) as Spinner

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
            val toast = Toast.makeText(view.context, "Today Feed Refreshed", Toast.LENGTH_LONG).show()
        }

    }


    private fun fetchNews(location: String) {
        var location = location

        when (location) {
            "United States" -> {
                location = "us"
            }
            "United Kingdom" -> {
                location = "gb"
            }
            "Australia" -> {
                location = "au"
            }
            "South Africa" -> {
                location = "za"
            }
            "France" -> {
                location = "fr"
            }
            "United Arab Emirates" -> {
                location = "ae"
            }
            "New Zealand" -> {
                location = "nz"
            }
            "Argentina" -> {
                location = "ar"
            }
        }

        val c = Calendar.getInstance()

        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val date = "$year-${month+1}-$day"
        val dateBefore = "$year-${month+1}-${day-1}"

        val url =
            "http://newsapi.org/v2/top-headlines?country=$location&from=${dateBefore}&to=$date&sortBy=popularity&apiKey=${Constants.API_KEY}"

        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {

            override fun onResponse(call: Call, response: Response) {
                val body = response?.body?.string()
                val gson = GsonBuilder().create()
                val homeFeed = gson.fromJson(body, HomeFeed::class.java)

                activity?.runOnUiThread {
                    todayRecyclerView.adapter = MyAdapter(homeFeed)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute request")
            }
        })
    }
}

class HomeFeed(val articles: List<Articles>)
class Articles(val source: Source, val author: String, val title: String, val description: String, val url: String, val urlToImage: String, val publishedAt: String, val content: String)
class Source(val id: String, val name: String)
