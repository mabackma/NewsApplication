package com.example.newsapplication.datatypes

import com.google.gson.annotations.SerializedName


data class NewsItem (

  @SerializedName("news_image"   ) var newsImage   : String? = null,
  @SerializedName("news_url"     ) var newsUrl     : String? = null,
  @SerializedName("published_at" ) var publishedAt : String? = null,
  @SerializedName("publisher"    ) var publisher   : String? = null,
  @SerializedName("title"        ) var title       : String? = null

)