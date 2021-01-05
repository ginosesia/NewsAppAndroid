package com.example.myapplication.Model

class CategoryModel {

    var category: String? = null

    fun getCategories(): String {
        return category.toString()

    }

    fun setCategories(category: String) {
        this.category = category
    }

}