package com.example.newsapplication

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapplication.databinding.RecyclerviewItemRowBinding
import com.example.newsapplication.datatypes.NewsItem
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import java.text.SimpleDateFormat
import java.util.*

class NewsAdapter(private val newsItems: List<NewsItem>) : RecyclerView.Adapter<NewsAdapter.NewsItemHolder>() {

    // binding layer variable
    private var _binding: RecyclerviewItemRowBinding? = null
    private val binding get() = _binding!!

    // ViewHolderin onCreate-method. Attaching binding layer
    // to NewsItemHolder-class (adapter inner class)
    // because NewsAdapter is based on RecyclerView's standard adapter,
    // we need a method called onCreateViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsItemHolder {
        // binding layer is one recyclerview_item_row.xml -instance
        _binding = RecyclerviewItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsItemHolder(binding)
    }

    // Attaches one NewsItem-object to one NewsItemHolder-instance
    // because NewsAdapter is based on RecyclerView's standard adapter,
    // we need a method called onBindViewHolder
    override fun onBindViewHolder(holder: NewsItemHolder, position: Int) {
        val itemNews = newsItems[position]
        holder.bindNewsItem(itemNews)
    }

    // The adapter has to know the size fo the data with this method
    // because NewsItemAdapter is based on RecyclerView's standard adapter,
    // we need a method called getItemCount
    override fun getItemCount(): Int {
        return newsItems.size
    }

    // NewsItemHolder is defined in NewsAdapter's basic definition
    // Holder-class contains the logic for attaching it to the layout
    class NewsItemHolder(v: RecyclerviewItemRowBinding) : RecyclerView.ViewHolder(v.root), View.OnClickListener {

        // Binding for one newsItem
        private var view: RecyclerviewItemRowBinding = v
        private var newsItem: NewsItem? = null

        // Set image to be clickable
        init {
            v.imageViewNews.setOnClickListener(this)
        }

        // bind the details of the news item to the recyclerview item
        fun bindNewsItem(newsItem: NewsItem)
        {
            // Display title
            this.newsItem = newsItem
            view.textViewNewsTitle.text = newsItem.title

            // Display date
            val inputFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH)
            val outputFormat = SimpleDateFormat("EEE, dd MMM yyyy", Locale.ENGLISH)
            val inputDate = inputFormat.parse(newsItem.publishedAt.toString())
            val outputDateString = outputFormat.format(inputDate)
            view.textViewPublisher.text = newsItem.publisher + "\n" + outputDateString

            // Display image
            Glide.with(view.root)
                .load(newsItem.newsImage)
                .placeholder(R.drawable.ic_menu_camera) // Optional placeholder image
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(view.imageViewNews)
        }

        // If image is clicked, run this
        override fun onClick(v: View) {
            Log.d("CLICK", "news item clicked! " + newsItem!!.newsUrl.toString())
            openNewsUrlInBrowser(newsItem!!.newsUrl.toString())
        }

        // Open link in browser
        private fun openNewsUrlInBrowser(newsUrl: String) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(newsUrl))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            itemView.context.startActivity(intent)
        }
    }
}