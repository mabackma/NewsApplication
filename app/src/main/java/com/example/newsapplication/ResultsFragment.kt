package com.example.newsapplication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
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

    val url = "https://news-project-backend.onrender.com/search"
    val gson = GsonBuilder().setPrettyPrinting().create()

    lateinit var userQuery: UserQuery
    var pages: Array<Int> = emptyArray()
    var selectedOption = ""
    var selectedPage = 0

    // get fragment parameters from previous fragment
    val args: ResultsFragmentArgs by navArgs()

    private lateinit var newsAdapter: NewsAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Sort by selections in spinner
        val options = arrayOf("Relevancy", "Popularity", "Published at")
        val sortByAdapter = ArrayAdapter<String>(requireContext(), R.layout.spinner_item, options)
        binding.spinnerSortBy.adapter = sortByAdapter
        binding.spinnerSortBy.setSelection(2);

        // Page selections in spinner
        // Call useQuery to fetch the data and update the UI after receiving the response
        userQuery = args.userQuery
        useQuery(userQuery) {pageCount ->
            // Update the UI with the received pageCount value
            pages = (1..pageCount).toList().toTypedArray()
            val pageAdapter = ArrayAdapter<String>(
                requireContext(),
                R.layout.spinner_item,
                pages.map { it.toString() })
            pageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerPage.adapter = pageAdapter
            binding.spinnerPage.setSelection(0)
        }

        // Set the listener for the spinnerSortBy
        binding.spinnerSortBy.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedOption = options[position]
                binding.spinnerPage.setSelection(0)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        // Set the listener for the spinnerPage
        binding.spinnerPage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedPage = pages[position]
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

        // Setting up the recycler view layout
        linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.scrollToPosition(0)
        binding.recyclerView.layoutManager = linearLayoutManager

        // Set empty adapter to recycler view
        newsAdapter = NewsAdapter(emptyList(), findNavController(), "results")
        binding.recyclerView.adapter = newsAdapter

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun useQuery(userQuery: UserQuery, callback: (Int) -> Unit)  {
        val url = "https://news-project-backend.onrender.com/search"
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
                // Response type is a tuple (news_count, all_news)
                var newsCount = response.getInt(0)
                val allNewsArray = response.getJSONArray(1)

                // Calculate how many pages of results there are
                var pageCount = 0
                if(newsCount >= 100) {
                    newsCount = 100
                    pageCount = floor(100.0 / userQuery.pageSize!!.toDouble()).toInt()
                }
                else {
                    pageCount = ceil(newsCount.toDouble() / userQuery.pageSize!!.toDouble()).toInt()
                }
                callback(pageCount)

                // Results info
                binding.textViewSearchResults.text = "Showing page ${userQuery.page}/${pageCount} in ${newsCount} results for \"${userQuery.query}\":"

                // Fill adapter with rows of news and set to recycler view
                val rows : List<NewsItem> = gson.fromJson(allNewsArray.toString(), Array<NewsItem>::class.java).toList()
                newsAdapter = NewsAdapter(rows, findNavController(), "results")
                binding.recyclerView.adapter = newsAdapter
            },
            { error ->
                Log.d("POST","Error getting POST response: ${error.message})")
            }
        )
        requestQueue.add(jsonArrayRequest)
    }
}