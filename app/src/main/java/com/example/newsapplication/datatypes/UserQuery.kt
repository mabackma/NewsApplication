package com.example.newsapplication.datatypes

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName


data class UserQuery (

  @SerializedName("query"      ) var query     : String? = null,
  @SerializedName("search_in"  ) var searchIn  : String? = null,
  @SerializedName("sort_items" ) var sortItems : String? = null,
  @SerializedName("language"   ) var language  : String? = null,
  @SerializedName("page_size"  ) var pageSize  : Int?    = null,
  @SerializedName("page"       ) var page      : Int?    = null,
  @SerializedName("start"      ) var start     : String? = null,
  @SerializedName("end"        ) var end       : String? = null

) : Parcelable  {

  // data class NewsItem implements Parcelable interface
  // Override the interface
  constructor(parcel: Parcel) : this(
    parcel.readString() ?: "",
    parcel.readString() ?: "",
    parcel.readString() ?: "",
    parcel.readString() ?: "",
    parcel.readInt() ?: 0,
    parcel.readInt() ?: 0,
    parcel.readString() ?: "",
    parcel.readString() ?: ""
  )

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeString(query)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<UserQuery> {
    override fun createFromParcel(parcel: Parcel): UserQuery {
      return UserQuery(parcel)
    }

    override fun newArray(size: Int): Array<UserQuery?> {
      return arrayOfNulls(size)
    }
  }
}