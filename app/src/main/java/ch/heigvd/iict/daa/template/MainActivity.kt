package ch.heigvd.iict.daa.template

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import ch.heigvd.iict.daa.template.databinding.ActivityMainBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date

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

        // set up clear and save buttons
        setupButtons()
    }

    private fun setupSectionVisibility() {
        val radioGroupOccupation = findViewById<RadioGroup>(R.id.radioGroupOccupation)

        val studentViews = listOf(
            findViewById<View>(R.id.studentSectionContainer),
            findViewById<TextView>(R.id.titleStudentInfo),
            findViewById<TextView>(R.id.labelSchool),
            findViewById<EditText>(R.id.inputSchool),
            findViewById<View>(R.id.lineSchool),
            findViewById<TextView>(R.id.labelGradYear),
            findViewById<EditText>(R.id.inputGradYear),
            findViewById<View>(R.id.lineGradYear)
        )

        val employeeViews = listOf(
            findViewById<View>(R.id.employeeSectionContainer),
            findViewById<TextView>(R.id.titleEmployeeInfo),
            findViewById<TextView>(R.id.labelCompany),
            findViewById<EditText>(R.id.inputCompany),
            findViewById<View>(R.id.lineCompany),
            findViewById<TextView>(R.id.labelSector),
            findViewById<Spinner>(R.id.spinnerSector),
            findViewById<View>(R.id.lineSector),
            findViewById<TextView>(R.id.labelExperience),
            findViewById<EditText>(R.id.inputExperience),
            findViewById<View>(R.id.lineExperience)
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
        // Basic information
        val lastName = binding.inputLastName.text.toString()
        val firstName = binding.inputFirstName.text.toString()
        val date = binding.inputDate.text.toString()
        val nationality = binding.spinnerNationality.selectedItem.toString()

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
        // Student information
        val school = binding.inputSchool.text.toString()
        val gradYear = binding.inputGradYear.text.toString()
    }

    private fun saveEmployee() {
        // Employee information
        val company = binding.inputCompany.text.toString()
        val sector = binding.spinnerSector.selectedItem.toString()
        val experience = binding.inputExperience.text.toString()
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
                    toast("Please fill in all the required fields for the student")
                    return false
                }
            }
            R.id.radioEmployee -> {
                val company = binding.inputCompany.text.toString()
                val experience = binding.inputExperience.text.toString()

                if (company.isEmpty() || experience.isEmpty()) {
                    toast("Please fill in all the required fields for the employee")
                    return false
                }
            }
        }
        return true

    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}