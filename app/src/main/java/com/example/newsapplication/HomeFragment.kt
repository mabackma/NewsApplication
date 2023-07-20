package com.example.newsapplication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.newsapplication.databinding.FragmentHomeBinding
import com.example.newsapplication.datatypes.UserQuery
import java.time.LocalDate

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    var selectedYearStartStr: String = ""
    var selectedYearStart: Int = 0
    var selectedMonthStartStr: String = ""
    var selectedMonthStart: Int = 0
    var selectedDayStartStr: String = ""
    var selectedDayStart: Int = 0
    var selectedYearEndStr: String = ""
    var selectedYearEnd: Int = 0
    var selectedMonthEndStr: String = ""
    var selectedMonthEnd: Int = 0
    var selectedDayEndStr: String = ""
    var selectedDayEnd: Int = 0

    val currentDate = LocalDate.now()
    val currentYear = currentDate.year
    val currentMonth = currentDate.monthValue
    val currentDay = currentDate.dayOfMonth

    lateinit var yearSpinnerStart: Spinner
    lateinit var monthSpinnerStart: Spinner
    lateinit var daySpinnerStart: Spinner
    lateinit var yearSpinnerEnd: Spinner
    lateinit var monthSpinnerEnd: Spinner
    lateinit var daySpinnerEnd: Spinner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        yearSpinnerStart = binding.yearSpinnerStart
        monthSpinnerStart = binding.monthSpinnerStart
        daySpinnerStart = binding.daySpinnerStart
        yearSpinnerEnd = binding.yearSpinnerEnd
        monthSpinnerEnd = binding.monthSpinnerEnd
        daySpinnerEnd = binding.daySpinnerEnd

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
        val daysStart = (1 ..31).toList().toTypedArray()
        val dayAdapterStart = ArrayAdapter<String>(
            requireContext(),
            R.layout.spinner_item,
            daysStart.map { it.toString() })
        dayAdapterStart.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        daySpinnerStart!!.setAdapter(dayAdapterStart)
        daySpinnerStart!!.setSelection(currentDay - 1)

        val daysEnd = (1..31).toList().toTypedArray()
        val dayAdapterEnd = ArrayAdapter<String>(
            requireContext(),
            R.layout.spinner_item,
            daysEnd.map { it.toString() })
        dayAdapterEnd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        daySpinnerEnd!!.setAdapter(dayAdapterEnd)
        daySpinnerEnd!!.setSelection(currentDay - 1)

        getSpinnerValues()

        // Set the listener for the start month
        binding.monthSpinnerStart.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position == 1) {
                    binding.monthSpinnerEnd.setSelection(1)
                }
                checkStartDate(selectedDayStart-1)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        // Set the listener for the end month.
        binding.monthSpinnerEnd.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position == 0) {
                    binding.monthSpinnerStart.setSelection(0)
                }
                checkEndDate(selectedDayEnd-1)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        // Set the listener for the start day
        binding.daySpinnerStart.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                checkStartDate(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        // Set the listener for the end day
        binding.daySpinnerEnd.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                checkEndDate(position)
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

    // Corrects the start day if it's out of bounds
    fun checkStartDate(position: Int) {
        getSpinnerValues()
        if((selectedMonthStart == currentMonth-1 || selectedMonthStart == 12) && position < currentDay-1) {
            binding.daySpinnerStart.setSelection(currentDay-1)
        }
        if(selectedMonthStart == currentMonth && position >= currentDay) {
            binding.daySpinnerStart.setSelection(currentDay-1)
        }
        if(selectedMonthStart == selectedMonthEnd && position >= selectedDayEnd) {
            binding.daySpinnerStart.setSelection(selectedDayEnd-1)
        }
    }

    // Corrects the end day if it's out of bounds
    fun checkEndDate(position: Int) {
        getSpinnerValues()
        if((selectedMonthEnd == currentMonth-1 || selectedMonthEnd == 12) && position < currentDay-1) {
            binding.daySpinnerEnd.setSelection(currentDay-1)
        }
        if(selectedMonthEnd == currentMonth && position >= currentDay) {
            binding.daySpinnerEnd.setSelection(currentDay-1)
        }
        if(selectedMonthEnd == selectedMonthStart && position < selectedDayStart-1) {
            binding.daySpinnerEnd.setSelection(selectedDayStart-1)
        }
    }

    // Gets all the values from the spinners
    fun getSpinnerValues() {
        selectedYearStartStr = binding.yearSpinnerStart.selectedItem as String
        selectedYearStart = selectedYearStartStr.toInt()
        selectedMonthStartStr = binding.monthSpinnerStart.selectedItem as String
        selectedMonthStart = selectedMonthStartStr.toInt()
        selectedDayStartStr = binding.daySpinnerStart.selectedItem as String
        selectedDayStart = selectedDayStartStr.toInt()
        selectedYearEndStr = binding.yearSpinnerStart.selectedItem as String
        selectedYearEnd = selectedYearEndStr.toInt()
        selectedMonthEndStr = binding.monthSpinnerEnd.selectedItem as String
        selectedMonthEnd = selectedMonthEndStr.toInt()
        selectedDayEndStr = binding.daySpinnerEnd.selectedItem as String
        selectedDayEnd = selectedDayEndStr.toInt()
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

        getSpinnerValues()

        val startDate = selectedYearStart.toString() + "-" + selectedMonthStart.toString() + "-" + selectedDayStart.toString()
        val endDate = selectedYearEnd.toString() + "-" + selectedMonthEnd.toString() + "-" + selectedDayEnd.toString()

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