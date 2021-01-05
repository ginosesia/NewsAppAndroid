package com.example.myapplication.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.NewsArticleActivity
import com.example.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_source_list.view.*

class MyAdapter(private val homeFeed: HomeFeed) : RecyclerView.Adapter<MyAdapter.ArticleViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.ArticleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.row_source_list, parent, false)
        return ArticleViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyAdapter.ArticleViewHolder, position: Int) {
        val article = homeFeed.articles[position]

        val image = holder.itemView.articleImage
        Picasso.get().load(article.urlToImage).into(image)

        holder.itemView.articleTitle.text = article.title
        holder.itemView.articlePublisher.text = article.source.name
        holder.itemView.articleSummary.text = article.description
        holder.itemView.articleTimeStamp.text = article.publishedAt
        holder.itemView.articleUrl.text = article.url
        holder.currentArticle = article
    }

    override fun getItemCount(): Int {
        return homeFeed.articles.count()
    }

    class ArticleViewHolder(itemView: View, var currentArticle: Articles? = null) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    companion object {
        const val NEWS_ARTICLE_LINK_KEY = "NEWS_ARTICLE_LINK"
    }

        var cell = itemView.findViewById<View>(R.id.news_cell)
        var options = itemView.findViewById<Button>(R.id.save_button)
        var title = itemView.findViewById<TextView>(R.id.articleTitle)
        var author = itemView.findViewById<TextView>(R.id.articlePublisher)
        var description = itemView.findViewById<TextView>(R.id.articleSummary)
        var url = itemView.findViewById<TextView>(R.id.articleUrl)
        var time = itemView.findViewById<TextView>(R.id.articleTimeStamp)

    init {
        cell.setOnClickListener(this)
        options.setOnClickListener {

            val popupMenu = PopupMenu(itemView.context, options)
            popupMenu.inflate(R.menu.popup_menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->

                val id = menuItem.itemId

                if (id == R.id.share) {
                    val link = url.text
                    val title = title.text
                    val description = description.text
                    val author = author.text
                    val body = "$title \n\n$description \n\n Read the full article on: \n $link "
                    val subject = "News Article from: $author"
                    val shareTitle = "Share: $title"

                    val intent = Intent()
                    intent.action = Intent.ACTION_SEND
                    intent.type = "text/plain"
                    intent.putExtra(Intent.EXTRA_TEXT, body)
                    intent.putExtra(Intent.EXTRA_SUBJECT, subject)
                    itemView.context.startActivity(Intent.createChooser(intent, shareTitle))

                } else if (id == R.id.save) {
                    addStoryToDatabase(title, author, description,time,url)
                    Toast.makeText(itemView.context, "Added to saved articles", Toast.LENGTH_LONG).show()
                }
                true
            }
            popupMenu.show()
        }

        title.setOnClickListener {
            val intent = Intent(itemView.context, NewsArticleActivity::class.java)
            intent.putExtra(NEWS_ARTICLE_LINK_KEY, url.text)
            itemView.context.startActivity(intent)
        }
    }

        override fun onClick(v: View) {
        }

        private fun addStoryToDatabase(title: TextView, publisher: TextView, description: TextView, time: TextView, url: TextView) {

            val mAuth = FirebaseAuth.getInstance()
            val database = FirebaseDatabase.getInstance()
            val currentUid = mAuth.currentUser?.uid
            val dbSavedStories = database.getReference("User-Saved-Stories")

            val ref = dbSavedStories.child(currentUid!!).push()
            ref.child("Title").setValue(title.text)
            ref.child("Publisher").setValue(publisher.text)
            ref.child("Summary").setValue(description.text)
            ref.child("Time").setValue(time.text)
            ref.child("ArticleUrl").setValue(url.text)
        }
    }
}