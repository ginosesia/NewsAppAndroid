package com.example.myapplication.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.NewsArticleActivity
import com.example.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.row_source_list_saved_story.view.*

class SavedStoriesAdapter(private val list: ArrayList<String>) :
    RecyclerView.Adapter<SavedStoriesAdapter.ViewHolder>() {

    private val mAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val currentUid = mAuth.currentUser?.uid
    private val dbSavedStories = database.getReference("User-Saved-Stories")
    val publishers = ArrayList<String>()
    val summaries = ArrayList<String>()
    val times = ArrayList<String>()
    val titles = ArrayList<String>()
    val urls = ArrayList<String>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.row_source_list_saved_story, parent, false)
        return ViewHolder(v)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getArticles(object: SavedStoriesCallback {
            override fun onCallback(publishers:ArrayList<String>, summaries:ArrayList<String>, times:ArrayList<String>, titles:ArrayList<String>,urls:ArrayList<String>) {
                holder.author.text = publishers[position]
                holder.title.text = titles[position]
                holder.description.text = summaries[position]
                holder.itemView.articleTimeStamp.text = times[position]
                holder.itemView.articleUrl.text = urls[position]
            }
        })
    }

    interface SavedStoriesCallback {
        fun onCallback(publishers:ArrayList<String>,summaries:ArrayList<String>,times:ArrayList<String>,titles:ArrayList<String>,urls:ArrayList<String>)
    }

    private fun getArticles(myCallback: SavedStoriesCallback) {
        val ref = dbSavedStories.child(currentUid!!)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                        val publisher = childSnapshot.child("Publisher").value.toString()
                        publishers.add(publisher)
                        val summary = childSnapshot.child("Summary").value.toString()
                        summaries.add(summary)
                        val time = childSnapshot.child("Time").value.toString()
                        times.add(time)
                        val title = childSnapshot.child("Title").value.toString()
                        titles.add(title)
                        val url = childSnapshot.child("ArticleUrl").value.toString()
                        urls.add(url)
                }
                myCallback.onCallback(publishers,summaries,times,titles, urls)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        var cell = itemView.findViewById<View>(R.id.news_cell)
        var options = itemView.findViewById<Button>(R.id.save_button)
        var title = itemView.findViewById<TextView>(R.id.articleTitle)
        var author = itemView.findViewById<TextView>(R.id.articlePublisher)
        var description = itemView.findViewById<TextView>(R.id.articleSummary)
        var url = itemView.findViewById<TextView>(R.id.articleUrl)

        init {
            cell.setOnClickListener(this)
            options.setOnClickListener {

                val popupMenu = PopupMenu(itemView.context, options)
                popupMenu.inflate(R.menu.popup_menu_saved)
                popupMenu.setOnMenuItemClickListener { menuItem ->

                    val id = menuItem.itemId
                    val link = url.text

                    if (id == R.id.share) {
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

                    } else if (id == R.id.remove) {
                        removeArticle(link)
                    }
                    true
                }
                popupMenu.show()
            }

            title.setOnClickListener {
                val link = url.text
                val intent = Intent(itemView.context, NewsArticleActivity::class.java)
                intent.putExtra(MyAdapter.ArticleViewHolder.NEWS_ARTICLE_LINK_KEY, link)
                itemView.context.startActivity(intent)
            }
        }

        override fun onClick(v: View?) {

        }
    }

    private fun removeArticle(link: CharSequence) {

        val ref = dbSavedStories.child(currentUid!!)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    if (childSnapshot.child("ArticleUrl").value == link) {
                        val key = childSnapshot.key
                        ref.child(key!!).removeValue()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}