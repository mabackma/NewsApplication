package com.example.newsapplication

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

    // binding layerin muututjien alustaminen
    private var _binding: RecyclerviewItemRowBinding? = null
    private val binding get() = _binding!!

    // ViewHolderin onCreate-metodi. käytännössä tässä kytketään binding layer
    // osaksi CommentHolder-luokkaan (adapterin sisäinen luokka)
    // koska CommentAdapter pohjautuu RecyclerViewin perusadapteriin, täytyy tästä
    // luokasta löytyä metodi nimeltä onCreateViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsItemHolder {
        // binding layerina toimii yksitätinen recyclerview_item_row.xml -instanssi
        _binding = RecyclerviewItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsItemHolder(binding)
    }

    // tämä metodi kytkee yksittäisen Comment-objektin yksittäisen CommentHolder-instanssiin
    // koska CommentAdapter pohjautuu RecyclerViewin perusadapteriin, täytyy tästä
    // luokasta löytyä metodi nimeltä onBindViewHolder
    override fun onBindViewHolder(holder: NewsItemHolder, position: Int) {
        val itemNews = newsItems[position]
        holder.bindNewsItem(itemNews)
    }

    // Adapterin täytyy pysty tietämään sisältämänsä datan koko tämän metodin avulla
    // koska CommentAdapter pohjautuu RecyclerViewin perusadapteriin, täytyy tästä
    // luokasta löytyä metodi nimeltä getItemCount
    override fun getItemCount(): Int {
        return newsItems.size
    }

    // CommentHolder, joka määritettiin oman CommentAdapterin perusmäärityksessä (ks. luokan yläosa)
    // Holder-luokka sisältää logiikan, jolla data ja ulkoasu kytketään toisiinsa
    class NewsItemHolder(v: RecyclerviewItemRowBinding) : RecyclerView.ViewHolder(v.root), View.OnClickListener {

        // tämän kommentin ulkoasu ja varsinainen data
        private var view: RecyclerviewItemRowBinding = v
        private var newsItem: NewsItem? = null

        // mahdollistetaan yksittäisen itemin klikkaaminen tässä luokassa
        init {
            v.root.setOnClickListener(this)
        }

        // bind the details of the news item tot he recyclerview item
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

        // jos itemiä klikataan käyttöliittymässä, ajetaan tämä koodio
        override fun onClick(v: View) {
            // tässä esimerkiksi actionilla ja navControllerin avulla
            // navigoidaan toiseen fragmentiin, jonka tarkoitus on näyttää
            // yksityiskohdat tästä kommentista.

            // klikatun kommentin id:n saat haettua näin:
            // comment.id

            // parametrina tulee lähettää joko:
            // 1. pelkkä kommentin id argumenttina
            // 2. kaikki kommentin datat omina argumentteina
            // 3. kaikki data JSON-muodossa yhtenä argumenttina (GSON)

            // TOISESSA FRAGMENTISSA, voit ottaa navArgs / args-muuttujan
            // avulla lähetetyn id-parametrin, ja muodostaa sen avulla esim. uuden URLin:

            // JSON_URL = "https://jsonplaceholder.typicode.com/comments/" + args.id.toString()
        }
    }
}