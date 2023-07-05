package com.example.newsapplication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.navigation.fragment.navArgs
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.newsapplication.databinding.FragmentResultsBinding
import com.example.newsapplication.datatypes.NewsItem
import com.example.newsapplication.datatypes.UserQuery
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.Math.ceil
import java.lang.Math.floor

class ResultsFragment : Fragment() {

    private var _binding: FragmentResultsBinding? = null
    private val binding get() = _binding!!

    val url = "http://10.0.2.2:5000/search"
    val gson = GsonBuilder().setPrettyPrinting().create()

    lateinit var userQuery: UserQuery
    var pages: Array<Int> = emptyArray()
    var selectedOption = ""
    var selectedPage = 0

    // get fragment parameters from previous fragment
    val args: ResultsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val options = arrayOf("Relevancy", "Popularity", "Published at")
        val sortByAdapter = ArrayAdapter<String>(requireContext(), R.layout.spinner_item, options)
        binding.spinnerSortBy.adapter = sortByAdapter

        // Call useQuery to fetch the data and update the UI after receiving the response
        userQuery = args.userQuery
        useQuery(userQuery) {pageCount ->
            Log.d("PAGE COUNT", pageCount.toString())
            // Update the UI with the received pageCount value
            pages = (1..pageCount).toList().toTypedArray()
            val pageAdapter = ArrayAdapter<String>(
                requireContext(),
                R.layout.spinner_item,
                pages.map { it.toString() })
            pageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerPage.adapter = pageAdapter
        }

        // Set the listener for the spinnerSortBy
        binding.spinnerSortBy.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedOption = options[position]
                Log.d("SELECTED OPTION", selectedOption)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        // Set the listener for the spinnerPage
        binding.spinnerPage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedPage = pages[position]
                Log.d("SELECTED PAGE", selectedPage.toString())
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        binding.buttonRefresh.setOnClickListener {
            when (selectedOption) {
                "Relevancy" -> userQuery.sortItems = "relevancy"
                "Popularity" -> userQuery.sortItems = "popularity"
                "Published at" -> userQuery.sortItems = "publishedAt"
            }
            userQuery.page = selectedPage

            useQuery(userQuery) {pageCount ->}
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun useQuery(userQuery: UserQuery, callback: (Int) -> Unit)  {
        val url = "http://10.0.2.2:5000/search"
        val gson = GsonBuilder().setPrettyPrinting().create()

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
                val newsItems: List<NewsItem> = gson.fromJson(
                    response.toString(),
                    object : TypeToken<List<NewsItem>>() {}.type
                )
                Log.d("NEWS COUNT", "100")
                Log.d("NEWS PER PAGE", newsItems.size.toString())

                // Calculate how many pages of results there are
                val pageCount = floor(100.0 / userQuery.pageSize!!.toDouble()).toInt()
                callback(pageCount)

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