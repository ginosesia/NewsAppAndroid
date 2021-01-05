package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.adapters.MyAdapter
import kotlinx.android.synthetic.main.news_article_webpage.*

class NewsArticleActivity() : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.news_article_webpage)

        val articleLink = intent.getStringExtra(MyAdapter.ArticleViewHolder.NEWS_ARTICLE_LINK_KEY)

        webview_news_article.settings.javaScriptEnabled = true
        webview_news_article.settings.loadWithOverviewMode = true
        webview_news_article.settings.useWideViewPort = true

        if (articleLink != null) {
            webview_news_article.loadUrl(articleLink)
        }
    }
}