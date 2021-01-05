package com.example.myapplication

import android.util.Log
import com.koushikdutta.ion.Ion
import org.json.JSONObject
import javax.xml.transform.Source

class ModelList(source: Source, author:String, title:String,
                description:String, url:String, imageUrl:String, date:String) {

    var source:Source = source
    var author:String = author
    var title:String = title
    var description:String = description
    var url:String = url
    var imageUrl:String = imageUrl
    var date:String = date



}