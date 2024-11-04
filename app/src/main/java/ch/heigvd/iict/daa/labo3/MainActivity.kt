package ch.heigvd.iict.daa.labo3

import android.os.Bundle
import android.util.Log
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import ch.heigvd.iict.daa.labo3.databinding.ActivityMainBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.DateFormat
import java.util.Calendar
import java.util.TimeZone

val UTC: TimeZone = TimeZone.getTimeZone("UTC")

/**
 * Main activity for our application, it handles the controller part of the MVC pattern, which
 * binds the input fields to the model and handles the user interactions.
 *
 * @author Emilie Bressoud
 * @author Loïc Herman
 * @author Sacha Butty
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // link and inflate the layout
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Show default values in spinners
        setupSpinners()

        // Setup the radio group to show/hide the student/worker specific sections
        setupSectionVisibility();

        // Setup the date picker
        setupDatePicker()

        // Setup clear and save buttons event listeners
        setupButtons()

        // OPTIONAL: Restore the data from the view model
        restoreData(Person.exampleStudent)
        // restoreData(Person.exampleWorker)
    }

    /**
     * Sets up the spinner with default values
     */
    private fun setupSpinners() {
        binding.inputNationality.adapter = SpinnerDefaultValueAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            resources.getString(R.string.nationality_empty),
            resources.getStringArray(R.array.nationalities)
        )
        binding.inputSector.adapter = SpinnerDefaultValueAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            resources.getString(R.string.sectors_empty),
            resources.getStringArray(R.array.sectors)
        )
    }

    /**
     * Sets up an event listener on the radio group to
     * show/hide the student/worker specific sections
     */
    private fun setupSectionVisibility() {
        binding.inputOccupation.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioStudent -> {
                    binding.studentSpecificGroup.visibility = Group.VISIBLE
                    binding.workerSpecificGroup.visibility = Group.GONE
                }

                R.id.radioEmployee -> {
                    binding.studentSpecificGroup.visibility = Group.GONE
                    binding.workerSpecificGroup.visibility = Group.VISIBLE
                }

                else -> {
                    // Should theoretically never happen
                    binding.studentSpecificGroup.visibility = Group.GONE
                    binding.workerSpecificGroup.visibility = Group.GONE
                }
            }
        }
    }

    /**
     * Sets up the date picker by adding a click listener on the date button,
     * and ensuring the result is properly formatted and displayed in the input field
     */
    private fun setupDatePicker() {
        // Open the date picker when the button is clicked
        binding.birthDateButton.setOnClickListener {
            openDatePicker()
        }
    }

    /**
     * Sets up the buttons by adding click listeners to the OK and Cancel buttons
     */
    private fun setupButtons() {
        binding.buttonOk.setOnClickListener {
            saveData()
        }

        binding.buttonCancel.setOnClickListener {
            clearForm()
        }
    }

    /**
     * Opens the date picker dialog and sets the selected date in the input field
     */
    private fun openDatePicker() {
        val utcCalendar = Calendar.getInstance(UTC)
        val dateFormatter = DateFormat.getDateInstance(
            DateFormat.LONG,
            resources.configuration.locales.get(0)
        ).apply {
            timeZone = UTC
        }

        var currentTimestamp = MaterialDatePicker.todayInUtcMilliseconds()
        if (binding.inputBirthdate.text.isNotEmpty()) {
            val date = dateFormatter.parse(binding.inputBirthdate.text.toString())!!
            currentTimestamp = date.time
        }

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(R.string.main_base_birthdate_dialog_title)
            .setCalendarConstraints(
                CalendarConstraints.Builder()
                    // Make the date picker start at the current date
                    .setOpenAt(currentTimestamp)
                    // Validate the date is in the past
                    .setValidator(DateValidatorPointBackward.now())
                    // Restrict the date picker from 110 years ago to today
                    .setStart(utcCalendar.also { it.add(Calendar.YEAR, -110) }.timeInMillis)
                    .setEnd(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()
            )
            // Select the current date if the input field is not empty
            .setSelection(currentTimestamp)
            .build()

        datePicker.show(supportFragmentManager, "birthDate")
        datePicker.addOnPositiveButtonClickListener {
            binding.birthDateButton.clearFocus()

            utcCalendar.timeInMillis = it
            binding.inputBirthdate.setText(dateFormatter.format(utcCalendar.time))
        }
    }

    /**
     * Restores the generic data from the view model
     */
    private fun restoreData(person: Person) {
        binding.inputLastName.setText(person.name)
        binding.inputFirstName.setText(person.firstName)
        binding.inputBirthdate.setText(Person.dateFormatter.format(person.birthDay.time))
        setSpinner(binding.inputNationality, person.nationality)
        binding.inputEmail.setText(person.email)
        binding.inputComments.setText(person.remark)
    }

    /**
     * Restores the student data from the view model
     */
    private fun restoreData(student: Student) {
        restoreData(student as Person)
        binding.inputOccupation.check(R.id.radioStudent)
        binding.inputSchool.setText(student.university)
        binding.inputGradYear.setText("%d".format(student.graduationYear))
    }

    /**
     * Restores the worker data from the view model
     */
    private fun restoreData(worker: Worker) {
        restoreData(worker as Person)
        binding.inputOccupation.check(R.id.radioEmployee)
        binding.inputCompany.setText(worker.company)
        setSpinner(binding.inputSector, worker.sector)
        binding.inputExperience.setText("%d".format(worker.experienceYear))
    }

    /**
     * Saves the data from the form and displays it in the logs
     */
    private fun saveData() {
        if (!validateInput()) {
            return
        }

        when (binding.inputOccupation.checkedRadioButtonId) {
            R.id.radioStudent -> {
                saveStudent()
            }

            R.id.radioEmployee -> {
                saveWorker()
            }
        }
    }

    /**
     * Validates the input fields and displays a toast message if any of the fields are invalid
     *
     * @return true if the input is valid, false otherwise
     */
    private fun validateInput(): Boolean {
        // Basic information
        val lastName = binding.inputLastName.text.toString()
        val firstName = binding.inputFirstName.text.toString()
        val date = binding.inputBirthdate.text.toString()

        if (lastName.isEmpty() || firstName.isEmpty() || date.isEmpty()) {
            toast(getString(R.string.toast_validation_error_required_fields))
            return false
        }

        if (binding.inputNationality.selectedItemPosition == 0) {
            toast(getString(R.string.toast_validation_error_nationality))
        }

        when (binding.inputOccupation.checkedRadioButtonId) {
            // Student specific fields
            R.id.radioStudent -> {
                val school = binding.inputSchool.text.toString()
                val gradYear = binding.inputGradYear.text.toString()

                if (school.isEmpty() || gradYear.isEmpty()) {
                    toast(getString(R.string.toast_validation_error_student_required_fields))
                    return false
                }
            }

            // Employee specific fields
            R.id.radioEmployee -> {
                val company = binding.inputCompany.text.toString()
                val experience = binding.inputExperience.text.toString()
                if (company.isEmpty() || experience.isEmpty()) {
                    toast(getString(R.string.toast_validation_error_worker_required_fields))
                    return false
                }

                if (binding.inputSector.selectedItemPosition == 0) {
                    toast(getString(R.string.toast_validation_error_sector))
                    return false
                }
            }

            else -> {
                toast(getString(R.string.toast_validation_error_occupation))
                return false
            }
        }

        return true
    }

    /**
     * Saves the student information and displays it in the logs
     */
    private fun saveStudent(): Student {
        val student = Student(
            binding.inputLastName.text.toString(),
            binding.inputFirstName.text.toString(),
            binding.inputBirthdate.text.toString().toCalendar(),
            binding.inputNationality.selectedItem.toString(),
            binding.inputSchool.text.toString(),
            binding.inputGradYear.text.toString().toInt(),
            binding.inputEmail.text.toString(),
            binding.inputComments.text.toString()
        )

        Log.i("[SAVED STUDENT]", student.toString())
        toast(getString(R.string.toast_save_student_success))

        return student
    }

    /**
     * Saves the employee information and displays it in the logs
     */
    private fun saveWorker(): Worker {
        val worker = Worker(
            binding.inputLastName.text.toString(),
            binding.inputFirstName.text.toString(),
            binding.inputBirthdate.text.toString().toCalendar(),
            binding.inputNationality.selectedItem.toString(),
            binding.inputCompany.text.toString(),
            binding.inputSector.selectedItem.toString(),
            binding.inputExperience.text.toString().toInt(),
            binding.inputEmail.text.toString(),
            binding.inputComments.text.toString()
        )

        Log.i("[SAVED WORKER]", worker.toString())
        toast(getString(R.string.toast_save_worker_success))

        return worker
    }

    /**
     * Clears the form by resetting all the input fields
     */
    private fun clearForm() {
        // Clear basic information
        binding.inputLastName.text.clear()
        binding.inputFirstName.text.clear()
        binding.inputBirthdate.text.clear()
        binding.inputNationality.setSelection(0)
        binding.inputOccupation.clearCheck()

        // Clear student information
        binding.inputSchool.text.clear()
        binding.inputGradYear.text.clear()

        // Clear employee information
        binding.inputCompany.text.clear()
        binding.inputSector.setSelection(0)
        binding.inputExperience.text.clear()

        // Clear additional information
        binding.inputEmail.text.clear()
        binding.inputComments.text.clear()
    }

    /**
     * Extension function to convert a string to a calendar
     */
    private fun String.toCalendar(): Calendar {
        val calendar = Calendar.getInstance(UTC)
        val dateFormatter = DateFormat.getDateInstance(
            DateFormat.LONG,
            resources.configuration.locales.get(0)
        ).apply {
            timeZone = UTC
        }

        val date = dateFormatter.parse(this)
        if (date != null) {
            calendar.time = date
        }

        return calendar
    }

    /**
     * Helper function to sets the spinner to the given value
     */
    private fun setSpinner(spinner: Spinner, value: Any) {
        val adapter = spinner.adapter
        for (i in 0 until adapter.count) {
            if (adapter.getItem(i) == value) {
                spinner.setSelection(i)
                return
            }
        }
    }

    /**
     * Displays a toast message
     *
     * @param message the message to display
     * @param length the length of the toast message
     */
    private fun toast(message: String, length: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, message, length).show()
    }
}
