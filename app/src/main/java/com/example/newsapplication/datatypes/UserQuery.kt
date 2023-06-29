package com.example.newsapplication.datatypes

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

)