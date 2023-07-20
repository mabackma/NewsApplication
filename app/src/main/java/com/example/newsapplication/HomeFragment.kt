package com.example.newsapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
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

        var yearDifference = 0
        if(currentMonth == 1) {
            yearDifference = 1
        }

        // Set data for the year Spinner.
        val years = (currentYear - yearDifference..currentYear).toList().toTypedArray()
        val yearAdapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.spinner_item,
            years.map { it.toString() })
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        yearSpinnerStart!!.setAdapter(yearAdapter)
        yearSpinnerStart!!.setSelection(0)
        yearSpinnerEnd!!.setAdapter(yearAdapter)
        yearSpinnerEnd!!.setSelection(currentYear - (currentYear - yearDifference))

        // Set data for the month Spinner
        var months = arrayOf(currentMonth - 1, currentMonth)
        if(yearDifference == 1) {
            months = arrayOf(12, 1)
        }
        val monthAdapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.spinner_item,
            months.map { it.toString() })
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        monthSpinnerStart!!.setAdapter(monthAdapter)
        monthSpinnerStart!!.setSelection(0)
        monthSpinnerEnd!!.setAdapter(monthAdapter)
        monthSpinnerEnd!!.setSelection(1)

        // Set data for the day Spinner
        val daysStart = (currentDay + 1 ..31).toList().toTypedArray()
        val dayAdapterStart = ArrayAdapter<String>(
            requireContext(),
            R.layout.spinner_item,
            daysStart.map { it.toString() })
        dayAdapterStart.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        daySpinnerStart!!.setAdapter(dayAdapterStart)
        daySpinnerStart!!.setSelection(0)

        val daysEnd = (1..currentDay).toList().toTypedArray()
        val dayAdapterEnd = ArrayAdapter<String>(
            requireContext(),
            R.layout.spinner_item,
            daysEnd.map { it.toString() })
        dayAdapterEnd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        daySpinnerEnd!!.setAdapter(dayAdapterEnd)
        daySpinnerEnd!!.setSelection(currentDay - 1)

        // Set the listener for the start month
        binding.monthSpinnerStart.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position == 1) {
                    binding.monthSpinnerEnd.setSelection(1)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        // Set the listener for the end month
        binding.monthSpinnerEnd.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position == 0) {
                    binding.monthSpinnerStart.setSelection(0)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

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

        val chosenResultsPpId = binding.radioGroupResultsPerPage.checkedRadioButtonId
        val chosenResultsPpRadioButton: RadioButton = view!!.findViewById(chosenResultsPpId)
        var resultsPp = chosenResultsPpRadioButton.text.toString()

        // Make the query object
        val userQuery: UserQuery = UserQuery(
            query = queryWords,
            searchIn = searchIn,
            sortItems = "publishedAt",
            language = language,
            pageSize = resultsPp.toInt(),
            page = 1,
            start = startDate,
            end = endDate
        )
        return userQuery
    }
}