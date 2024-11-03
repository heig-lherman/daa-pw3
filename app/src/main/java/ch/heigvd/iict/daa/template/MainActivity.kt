package ch.heigvd.iict.daa.template

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupSectionVisibility();
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

    }

    private fun clearForm() {


    }

}