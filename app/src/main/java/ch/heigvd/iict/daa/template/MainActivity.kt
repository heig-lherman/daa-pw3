package ch.heigvd.iict.daa.template

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.core.view.updateLayoutParams
import ch.heigvd.iict.daa.labo3.CustomSpinnerAdapter
import ch.heigvd.iict.daa.labo3.Student
import ch.heigvd.iict.daa.template.databinding.ActivityMainBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

class MainActivity : AppCompatActivity() {
    // for binding (viewBinding = true)
    private lateinit var binding: ActivityMainBinding
    private lateinit var datePicker: MaterialDatePicker<Long>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // link and inflate the layout
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // set up the visibility of the sections
        setupSectionVisibility();

        // set up the date picker
        setupDatePicker()

        // set up the spinners
        setupSpinner()

        // set up clear and save buttons
        setupButtons()
    }

    private fun setupSectionVisibility() {
        val radioGroupOccupation = findViewById<RadioGroup>(R.id.radioGroupOccupation)

        val studentViews = listOf(
            binding.studentSectionContainer,
            binding.titleStudentInfo,
            binding.labelSchool,
            binding.inputSchool,
            binding.lineSchool,
            binding.labelGradYear,
            binding.inputGradYear,
            binding.lineGradYear
        )

        val employeeViews = listOf(
            binding.employeeSectionContainer,
            binding.titleEmployeeInfo,
            binding.labelCompany,
            binding.inputCompany,
            binding.lineCompany,
            binding.labelSector,
            binding.spinnerSector,
            binding.lineSector,
            binding.labelExperience,
            binding.inputExperience,
            binding.lineExperience
        )

        radioGroupOccupation.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioStudent -> {
                    studentViews.forEach { it.visibility = View.VISIBLE }
                    employeeViews.forEach { it.visibility = View.GONE }

                    findViewById<View>(R.id.titleAdditionalInfo).updateLayoutParams<ConstraintLayout.LayoutParams> {
                        topToBottom = R.id.lineGradYear
                    }
                }

                R.id.radioEmployee -> {
                    studentViews.forEach { it.visibility = View.GONE }
                    employeeViews.forEach { it.visibility = View.VISIBLE }

                    findViewById<View>(R.id.titleAdditionalInfo).updateLayoutParams<ConstraintLayout.LayoutParams> {
                        topToBottom = R.id.lineExperience
                    }
                }
            }
        }
    }


    private fun saveData() {
        if (!validateInput()) {
            return
        }

        when (binding.radioGroupOccupation.checkedRadioButtonId) {
            R.id.radioStudent -> {
                saveStudent()
            }

            R.id.radioEmployee -> {
                saveEmployee()
            }
        }
    }

    private fun saveStudent() {

        // Create a student object
        val student = Student(
            binding.inputLastName.text.toString(),
            binding.inputFirstName.text.toString(),
            // get date as a Calendar object
            binding.inputDate.text.toString().toCalendar(),
            binding.spinnerNationality.selectedItem.toString(),
            binding.inputSchool.text.toString(),
            binding.inputGradYear.text.toString().toInt(),
            binding.inputEmail.text.toString(),
            binding.inputComments.text.toString()
        )
        toast(student.toString(), Toast.LENGTH_LONG)
    }

    private fun String.toCalendar(): Calendar {
        val date = SimpleDateFormat.getDateInstance().parse(this)
        val calendar = Calendar.getInstance()
        if (date != null) {
            calendar.time = date
        }
        return calendar
    }

    private fun saveEmployee() {
        // Create an employee object
        val employee = Student(
            binding.inputLastName.text.toString(),
            binding.inputFirstName.text.toString(),
            // get date as a Calendar object
            binding.inputDate.text.toString().toCalendar(),
            binding.spinnerNationality.selectedItem.toString(),
            binding.inputCompany.text.toString(),
            binding.inputExperience.text.toString().toInt(),
            binding.inputEmail.text.toString(),
            binding.inputComments.text.toString()
        )
        toast(employee.toString(), Toast.LENGTH_LONG)
    }

    private fun clearForm() {
        // Clear basic information
        binding.inputLastName.text.clear()
        binding.inputFirstName.text.clear()
        binding.inputDate.text.clear()
        binding.spinnerNationality.setSelection(0)

        // Clear student information
        binding.inputSchool.text.clear()
        binding.inputGradYear.text.clear()

        // Clear employee information
        binding.inputCompany.text.clear()
        binding.spinnerSector.setSelection(0)
        binding.inputExperience.text.clear()

        // Clear additional information
        binding.inputEmail.text.clear()
        binding.inputComments.text.clear()

    }

    private fun setupDatePicker() {
        // Create the date picker
        datePicker = MaterialDatePicker.Builder.datePicker()
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setCalendarConstraints(calendarConstraint())
            .setTitleText("Select a date")
            .build()

        binding.dateButton.setOnClickListener {
            datePicker.show(supportFragmentManager, "DATE_PICKER")
        }

        binding.inputDate.setOnClickListener {
            datePicker.show(supportFragmentManager, "DATE_PICKER")
        }

        datePicker.addOnPositiveButtonClickListener { selection ->
            val dateFormat = SimpleDateFormat.getDateInstance()
            binding.inputDate.setText(dateFormat.format(Date(selection)))
        }

    }

    /**
     * Set the constraint for the calendar, the date should be between today and 110 years ago
     */
    private fun calendarConstraint(): CalendarConstraints {
        val today = MaterialDatePicker.todayInUtcMilliseconds()
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

        calendar.timeInMillis = today
        calendar.add(Calendar.YEAR, -110)
        val minDate = calendar.timeInMillis

        return CalendarConstraints.Builder()
            .setStart(minDate)
            .setEnd(today)
            .build()
    }

    private fun setupButtons() {
        binding.buttonOk.setOnClickListener {
            saveData()
        }

        binding.buttonCancel.setOnClickListener {
            clearForm()
        }
    }

    private fun validateInput(): Boolean {
        // Basic information
        val lastName = binding.inputLastName.text.toString()
        val firstName = binding.inputFirstName.text.toString()
        val date = binding.inputDate.text.toString()

        if (lastName.isEmpty() || firstName.isEmpty() || date.isEmpty()) {
            toast("Please fill in all the required fields")
            return false
        }

        when (binding.radioGroupOccupation.checkedRadioButtonId) {
            R.id.radioStudent -> {
                val school = binding.inputSchool.text.toString()
                val gradYear = binding.inputGradYear.text.toString()

                if (school.isEmpty() || gradYear.isEmpty()) {
                    toast("Please fill in all the required fields for student")
                    return false
                }
            }

            R.id.radioEmployee -> {
                val company = binding.inputCompany.text.toString()
                val experience = binding.inputExperience.text.toString()

                if (company.isEmpty() || experience.isEmpty()) {
                    toast("Please fill in all the required fields for employee")
                    return false
                }
            }
        }
        return true

    }

    private fun toast(message: String, length: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, message, length).show()
    }

    /**
     * Set up spinners when the activity is created
     * It initializes the spinners with 'Select' as the default value when null
     */
    private fun setupSpinner() {
        // set up the spinner
        val spinnerNationality = binding.spinnerNationality
        val spinnerSector = binding.spinnerSector

        // Setup the adapter to display the list of nationalities

        val arrayAdapter = CustomSpinnerAdapter(
            this,
            android.R.layout.simple_spinner_item,
            listOf(getString(R.string.nationality_empty)) + resources.getStringArray(R.array.nationalities)
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinnerNationality.adapter = arrayAdapter

        // Setup the adapter to display the list of sectors

        val arrayAdapterSector = CustomSpinnerAdapter(
            this,
            android.R.layout.simple_spinner_item,
            listOf(getString(R.string.sectors_empty)) + resources.getStringArray(R.array.sectors)
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinnerSector.adapter = arrayAdapterSector

    }

}