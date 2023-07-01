package com.example.newsapplication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.newsapplication.databinding.FragmentHomeBinding
import com.example.newsapplication.datatypes.NewsItem
import com.example.newsapplication.datatypes.UserQuery
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.time.LocalDate


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

        val yearSpinnerStart = binding.yearSpinnerStart
        val monthSpinnerStart = binding.monthSpinnerStart
        val daySpinnerStart = binding.daySpinnerStart
        val yearSpinnerEnd = binding.yearSpinnerEnd
        val monthSpinnerEnd = binding.monthSpinnerEnd
        val daySpinnerEnd = binding.daySpinnerEnd

        val currentDate = LocalDate.now()
        val currentYear = currentDate.year
        val currentMonth = currentDate.monthValue
        val currentDay = currentDate.dayOfMonth

        // Set data for the year Spinner
        val years = (2000..currentYear).toList().toTypedArray()
        val yearAdapter = ArrayAdapter<String>(requireContext(), R.layout.spinner_item, years.map { it.toString() })
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        yearSpinnerStart!!.setAdapter(yearAdapter)
        yearSpinnerStart!!.setSelection(currentYear - 2000)
        yearSpinnerEnd!!.setAdapter(yearAdapter)
        yearSpinnerEnd!!.setSelection(currentYear - 2000)

        // Set data for the month Spinner
        val months = (1..12).toList().toTypedArray()
        val monthAdapter = ArrayAdapter<String>(requireContext(), R.layout.spinner_item, months.map { it.toString() })
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        monthSpinnerStart!!.setAdapter(monthAdapter)
        monthSpinnerEnd!!.setAdapter(monthAdapter)
        monthSpinnerEnd!!.setSelection(currentMonth - 1)

        // Set data for the day Spinner
        val days = (1..31).toList().toTypedArray()
        val dayAdapter = ArrayAdapter<String>(requireContext(), R.layout.spinner_item, days.map { it.toString() })
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        daySpinnerStart!!.setAdapter(dayAdapter)
        daySpinnerEnd!!.setAdapter(dayAdapter)
        daySpinnerEnd!!.setSelection(currentDay - 1)

        binding.buttonMakeQuery.setOnClickListener{
            if(binding.editTextQuery.text.toString().isBlank()) {
                Toast.makeText(context, "Please enter search keywords", Toast.LENGTH_SHORT).show()
            }
            else
                makeQueryWithPost()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun makeQueryWithPost() {
        val queryWords = binding.editTextQuery.text.toString()
        var searchIn = ""
        if(binding.checkboxTitle.isChecked){
            searchIn += "title"
        }
        if(binding.checkboxDescription.isChecked){
            searchIn += ",description"
        }
        if(binding.checkboxContent.isChecked){
            searchIn += ",content"
        }
        if(searchIn.startsWith(",")) {
            searchIn = searchIn.substring(1)
        }

        val chosenLanguageId = binding.radioGroupLanguages.checkedRadioButtonId
        val chosenLanguageRadioButton: RadioButton = view!!.findViewById(chosenLanguageId)
        var language = chosenLanguageRadioButton.text.toString()

        if(language == "all") {
            language = ""
        }

        val yearSpinnerStart = binding.yearSpinnerStart
        val monthSpinnerStart = binding.monthSpinnerStart
        val daySpinnerStart = binding.daySpinnerStart
        val yearSpinnerEnd = binding.yearSpinnerEnd
        val monthSpinnerEnd = binding.monthSpinnerEnd
        val daySpinnerEnd = binding.daySpinnerEnd

        val selectedYearStart = yearSpinnerStart?.selectedItem.toString()
        val selectedMonthStart = monthSpinnerStart?.selectedItem.toString()
        val selectedDayStart = daySpinnerStart?.selectedItem.toString()
        val selectedYearEnd = yearSpinnerEnd?.selectedItem.toString()
        val selectedMonthEnd = monthSpinnerEnd?.selectedItem.toString()
        val selectedDayEnd = daySpinnerEnd?.selectedItem.toString()

        val startDate = selectedYearStart + "-" + selectedMonthStart + "-" + selectedDayStart
        val endDate = selectedYearEnd + "-" + selectedMonthEnd + "-" + selectedDayEnd

        Log.d("BETWEEN", startDate + " and " + endDate)
        // Make the query object
        val userQuery: UserQuery = UserQuery(
            query = queryWords,
            searchIn = searchIn,
            sortItems = "publishedAt",
            language = language,
            pageSize = 10,
            page = 1,
            start = startDate,
            end = endDate)

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