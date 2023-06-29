package com.example.newsapplication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.newsapplication.databinding.FragmentHomeBinding
import com.example.newsapplication.datatypes.NewsItem
import com.example.newsapplication.datatypes.UserQuery
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    val url = "http://10.0.2.2:5000/search"
    val gson = GsonBuilder().setPrettyPrinting().create()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.buttonMakeQuery.setOnClickListener{
            makeQueryWithPost()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun makeQueryWithPost() {
        val userQuery: UserQuery = UserQuery(
            query = "conspiracy theory wagner",
            searchIn = "title,description,content",
            sortItems = "publishedAt",
            language = "en",
            pageSize = 10,
            page = 1,
            start = "2023-06-25",
            end = "2023-06-26")
        val jsonItem = gson.toJson(userQuery)

        val requestQueue: RequestQueue = Volley.newRequestQueue(context)
        val jsonObject = JSONObject(jsonItem)
        try {
            jsonObject.put("query", userQuery.query)
            jsonObject.put("search_in", userQuery.searchIn)
            jsonObject.put("sort_items", userQuery.sortItems)
            jsonObject.put("language", userQuery.language)
            jsonObject.put("page_size", userQuery.pageSize)
            jsonObject.put("page", userQuery.page)
            jsonObject.put("start", userQuery.start)
            jsonObject.put("end", userQuery.end)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        // Make a JSONArray for the request jsonObject
        // This is necessary because the response will also be a JSONArray
        val jsonArray = JSONArray().put(jsonObject)
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.POST, url, jsonArray,
            { response ->
                binding.textViewResponse.text = "Got response from POST."
                Log.d("POST", "Response: $response")

                // Parse the JSON array into a list of NewsItem objects
                val newsItems: List<NewsItem> = gson.fromJson(
                    response.toString(),
                    object : TypeToken<List<NewsItem>>() {}.type
                )

                // Access the properties of each NewsItem
                for (newsItem in newsItems) {
                    Log.d("POST", "News Item - Title: ${newsItem.title}, Publisher: ${newsItem.publisher}")
                }
            },
            { error ->
                Log.d("POST","Error getting POST response: ${error.message})")
            }
        )

        requestQueue.add(jsonArrayRequest)
    }
}