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
import androidx.navigation.fragment.findNavController
import com.example.newsapplication.databinding.FragmentHomeBinding
import com.example.newsapplication.datatypes.UserQuery
import java.time.LocalDate

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

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

        // Set data for the year Spinner.
        val years = (currentYear - 5..currentYear).toList().toTypedArray()
        val yearAdapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.spinner_item,
            years.map { it.toString() })
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        yearSpinnerStart!!.setAdapter(yearAdapter)
        yearSpinnerStart!!.setSelection(currentYear - (currentYear - 5))
        yearSpinnerEnd!!.setAdapter(yearAdapter)
        yearSpinnerEnd!!.setSelection(currentYear - (currentYear - 5))

        // Set data for the month Spinner
        val months = (1..12).toList().toTypedArray()
        val monthAdapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.spinner_item,
            months.map { it.toString() })
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        monthSpinnerStart!!.setAdapter(monthAdapter)
        monthSpinnerEnd!!.setAdapter(monthAdapter)
        monthSpinnerEnd!!.setSelection(currentMonth - 1)

        // Set data for the day Spinner
        val days = (1..31).toList().toTypedArray()
        val dayAdapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.spinner_item,
            days.map { it.toString() })
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        daySpinnerStart!!.setAdapter(dayAdapter)
        daySpinnerEnd!!.setAdapter(dayAdapter)
        daySpinnerEnd!!.setSelection(currentDay - 1)

        binding.buttonMakeQuery.setOnClickListener {
            if (binding.editTextQuery.text.toString().isBlank()) {
                Toast.makeText(context, "Please enter search keywords", Toast.LENGTH_SHORT).show()
            } else {
                val userQuery = makeQuery()

                // Move to results fragment
                val action = HomeFragmentDirections.actionHomeFragmentToResultsFragment(userQuery)
                this.findNavController().navigate(action)
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun makeQuery(): UserQuery {
        val queryWords = binding.editTextQuery.text.toString()
        var searchIn = ""
        if (binding.checkboxTitle.isChecked) {
            searchIn += "title"
        }
        if (binding.checkboxDescription.isChecked) {
            searchIn += ",description"
        }
        if (binding.checkboxContent.isChecked) {
            searchIn += ",content"
        }
        if (searchIn.startsWith(",")) {
            searchIn = searchIn.substring(1)
        }

        val chosenLanguageId = binding.radioGroupLanguages.checkedRadioButtonId
        val chosenLanguageRadioButton: RadioButton = view!!.findViewById(chosenLanguageId)
        var language = chosenLanguageRadioButton.text.toString()

        if (language == "all") {
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
            pageSize = 20,
            page = 1,
            start = startDate,
            end = endDate
        )
        return userQuery
    }
}