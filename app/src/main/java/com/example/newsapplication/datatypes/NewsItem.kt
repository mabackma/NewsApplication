package com.example.newsapplication.datatypes

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class NewsItem (

  @SerializedName("news_image"   ) var newsImage   : String? = null,
  @SerializedName("news_url"     ) var newsUrl     : String? = null,
  @SerializedName("published_at" ) var publishedAt : String? = null,
  @SerializedName("publisher"    ) var publisher   : String? = null,
  @SerializedName("title"        ) var title       : String? = null

) : Parcelable {

  // data class NewsItem implements Parcelable interface
  // Override the interface
  constructor(parcel: Parcel) : this(
    parcel.readString() ?: "",
    parcel.readString() ?: "",
    parcel.readString() ?: "",
    parcel.readString() ?: "",
    parcel.readString() ?: ""
  )

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeString(title)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<NewsItem> {
    override fun createFromParcel(parcel: Parcel): NewsItem {
      return NewsItem(parcel)
    }

    override fun newArray(size: Int): Array<NewsItem?> {
      return arrayOfNulls(size)
    }
  }
}