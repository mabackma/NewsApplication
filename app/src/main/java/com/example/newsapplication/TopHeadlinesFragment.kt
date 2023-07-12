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
import com.example.newsapplication.databinding.FragmentTopHeadlinesBinding
import com.example.newsapplication.datatypes.UserQuery
import com.google.gson.GsonBuilder


class TopHeadlinesFragment : Fragment() {

    private var _binding: FragmentTopHeadlinesBinding? = null
    private val binding get() = _binding!!

    val url = "http://10.0.2.2:5000/search"
    val gson = GsonBuilder().setPrettyPrinting().create()

    var pages: Array<Int> = emptyArray()
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
                Log.d("SELECTED OPTION", selectedCountry)
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
                Log.d("SELECTED OPTION", selectedCategory)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        binding.buttonHeadlines.setOnClickListener {

        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun healinesSearch(userQuery: UserQuery)  {
        val url = "http://10.0.2.2:5000/headlines"
        val gson = GsonBuilder().setPrettyPrinting().create()
    }

}