package com.example.pocketwallet

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class BudgetActivity : AppCompatActivity() {

    private lateinit var minGoalInput: EditText
    private lateinit var maxGoalInput: EditText
    private lateinit var saveButton: Button
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.budget)

        sharedPrefs = getSharedPreferences("BudgetPrefs", MODE_PRIVATE)

        minGoalInput = findViewById(R.id.inputMinGoal)
        maxGoalInput = findViewById(R.id.inputMaxGoal)
        saveButton = findViewById(R.id.buttonSaveBudget)

        // Load previous goals if they exist
        val savedMin = sharedPrefs.getFloat("minGoal", 0f)
        val savedMax = sharedPrefs.getFloat("maxGoal", 0f)

        if (savedMin > 0) minGoalInput.setText(savedMin.toString())
        if (savedMax > 0) maxGoalInput.setText(savedMax.toString())

        saveButton.setOnClickListener {
            saveBudgetGoals()
        }
    }

    private fun saveBudgetGoals() {
        val minGoal = minGoalInput.text.toString().toFloatOrNull()
        val maxGoal = maxGoalInput.text.toString().toFloatOrNull()

        if (minGoal == null || maxGoal == null) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show()
            return
        }

        if (minGoal >= maxGoal) {
            Toast.makeText(this, "Minimum goal must be less than maximum goal", Toast.LENGTH_SHORT).show()
            return
        }

        sharedPrefs.edit()
            .putFloat("minGoal", minGoal)
            .putFloat("maxGoal", maxGoal)
            .apply()

        Toast.makeText(this, "Budget goals saved!", Toast.LENGTH_SHORT).show()
        finish()
    }
}
