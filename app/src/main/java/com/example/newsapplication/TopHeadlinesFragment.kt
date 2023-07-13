package com.example.newsapplication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.newsapplication.databinding.FragmentTopHeadlinesBinding
import com.example.newsapplication.datatypes.NewsItem
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject


class TopHeadlinesFragment : Fragment() {

    private var _binding: FragmentTopHeadlinesBinding? = null
    private val binding get() = _binding!!

    var selectedCountry = ""
    var selectedCategory = ""

    private lateinit var newsAdapter: NewsAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTopHeadlinesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Country selections in spinner
        val countries = arrayOf(
            "ae", "ar", "at", "au", "be", "bg", "br", "ca", "ch", "cn", "co", "cu", "cz",
            "de", "eg", "fr", "gb", "gr", "hk", "hu", "id", "ie", "il", "in", "it", "jp",
            "kr", "lt", "lv", "ma", "mx", "my", "ng", "nl", "no", "nz", "ph", "pl", "pt",
            "ro", "rs", "ru", "sa", "se", "sg", "si", "sk", "th", "tr", "tw", "ua", "us",
            "ve", "za"
        )
        val countryAdapter = ArrayAdapter<String>(requireContext(), R.layout.spinner_item, countries)
        binding.spinnerCountries.adapter = countryAdapter
        binding.spinnerCountries.setSelection(51);

        binding.spinnerCountries.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCountry = countries[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        // Category selections in spinner
        val categories = arrayOf("business", "entertainment", "general", "health", "science", "sports", "technology")
        val categoryAdapter = ArrayAdapter<String>(requireContext(), R.layout.spinner_item, categories)
        binding.spinnerCategories.adapter = categoryAdapter

        binding.spinnerCategories.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCategory = categories[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        binding.buttonHeadlines.setOnClickListener {
            headlinesSearch()
        }

        // Setting up the recycler view layout
        linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.scrollToPosition(0)
        binding.recyclerViewHeadlines.layoutManager = linearLayoutManager

        // Set empty adapter to recycler view
        newsAdapter = NewsAdapter(emptyList())
        binding.recyclerViewHeadlines.adapter = newsAdapter

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun headlinesSearch()  {
        val url = "http://10.0.2.2:5000/headlines"
        val gson = GsonBuilder().setPrettyPrinting().create()

        val requestQueue: RequestQueue = Volley.newRequestQueue(context)

        // Request object
        val requestObject = JSONObject()
        requestObject.put("country", selectedCountry)
        requestObject.put("category", selectedCategory)

        // Make a JSONArray for the request jsonObject
        // This is necessary because the response will also be a JSONArray
        val jsonArray = JSONArray()
        jsonArray.put(requestObject)
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.POST, url, jsonArray,
            { response ->
                // Response type is a tuple (news_count, all_news)
                val allNewsArray = response.getJSONArray(1)
                val newsItems: List<NewsItem> = gson.fromJson(
                    allNewsArray.toString(),
                    object : TypeToken<List<NewsItem>>() {}.type
                )

                // Fill adapter with rows of news and set to recycler view
                val rows : List<NewsItem> = gson.fromJson(allNewsArray.toString(), Array<NewsItem>::class.java).toList()
                newsAdapter = NewsAdapter(rows)
                binding.recyclerViewHeadlines.adapter = newsAdapter
            },
            { error ->
                Log.d("POST","Error getting POST response: ${error.message})")
            }
        )
        requestQueue.add(jsonArrayRequest)
    }
}