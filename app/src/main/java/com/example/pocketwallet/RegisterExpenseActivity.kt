package com.example.pocketwallet

import android.app.NotificationChannel
import android.app.NotificationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import com.example.pocketwallet.data.AppDatabase
import com.example.pocketwallet.data.ExpenseEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterExpenseActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var descInput: EditText
    private lateinit var categoryInput: EditText
    private lateinit var valueInput: EditText
    private lateinit var addPhotoBtn: Button
    private lateinit var addExpenseBtn: ImageButton
    private lateinit var totalValueField: EditText
    private lateinit var typeSpinner: Spinner

    private var selectedImageUri: Uri? = null
    private var totalBalance: Double = 0.0

    // Database
    private val db by lazy { AppDatabase.getDatabase(this) }

    companion object {
        private const val TAG = "RegisterExpense"
        private const val CHANNEL_ID = "budget_channel"
    }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        if (uri != null) {
            Toast.makeText(this, "Photo added successfully!", Toast.LENGTH_SHORT).show()
            addPhotoBtn.text = "Change Photo"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        createNotificationChannel()

        typeSpinner = findViewById(R.id.type_spinner)
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.income_expense_array,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinner.adapter = adapter

        totalValueField = findViewById(R.id.total_value_field)
        nameInput = findViewById(R.id.input_name)
        descInput = findViewById(R.id.input_description)
        categoryInput = findViewById(R.id.input_category)
        valueInput = findViewById(R.id.input_value)
        addPhotoBtn = findViewById(R.id.button_add_photo)
        addExpenseBtn = findViewById(R.id.button_add_expense)

        addPhotoBtn.setOnClickListener { pickImageLauncher.launch("image/*") }
        addExpenseBtn.setOnClickListener { saveEntry() }
        findViewById<ImageButton?>(R.id.return_button)?.setOnClickListener { finish() }
    }

    private fun saveEntry() {
        val type = typeSpinner.selectedItem.toString()
        val name = nameInput.text.toString().trim()
        val description = descInput.text.toString().trim()
        val category = categoryInput.text.toString().trim()
        val valueText = valueInput.text.toString().trim()

        if (name.isEmpty() || category.isEmpty() || valueText.isEmpty()) {
            Toast.makeText(this, "Please complete all fields.", Toast.LENGTH_SHORT).show()
            return
        }

        val value = try {
            valueText.toDouble()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Enter a valid number.", Toast.LENGTH_SHORT).show()
            return
        }

        val expense = ExpenseEntity(
            name = name,
            description = description,
            category = category,
            value = value,
            type = type,
            photoUri = selectedImageUri?.toString()
        )

        // Save to database and then re-check budget
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                db.expenseDao().insert(expense)

                // After insertion compute totals and check thresholds
                val expenses = db.expenseDao().getAll()
                val totalSpent = expenses.filter { it.type == "Expense" }.sumOf { it.value }

                withContext(Dispatchers.Main) {
                    // Update UI in this screen (optional running total)
                    totalBalance += if (type == "Income") value else -value
                    totalValueField.setText("R %.2f".format(totalBalance))

                    Toast.makeText(
                        this@RegisterExpenseActivity,
                        "$type recorded: $name — R$value in $category",
                        Toast.LENGTH_LONG
                    ).show()

                    // Clear inputs
                    nameInput.text.clear()
                    descInput.text.clear()
                    categoryInput.text.clear()
                    valueInput.text.clear()
                    addPhotoBtn.text = "Add Photo (Optional)"
                    selectedImageUri = null

                    // Check budget and possibly send notification
                    checkSpendingLimitAndNotify(totalSpent)
                }
            } catch (e: Exception) {
                Log.e(TAG, "saveEntry error", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegisterExpenseActivity, "Error saving entry: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun checkSpendingLimitAndNotify(totalSpent: Double) {
        val prefs = getSharedPreferences("BudgetPrefs", MODE_PRIVATE)
        val max = prefs.getFloat("maxGoal", -1f).toDouble()
        if (max <= 0) return

        val pct = totalSpent / max
        // Debug
        Log.d(TAG, "Total spent: $totalSpent, Max: $max, pct: $pct")

        if (pct >= 1.0) {
            sendSpendingNotification("You have reached your monthly spending limit.")
            Toast.makeText(this, "Warning: You reached your monthly spending limit.", Toast.LENGTH_LONG).show()
        } else if (pct >= 0.8) {
            sendSpendingNotification("You have used ${ (pct*100).toInt() }% of your monthly budget.")
            Toast.makeText(this, "You're nearing your monthly spending limit.", Toast.LENGTH_LONG).show()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Budget Alerts"
            val desc = "Alerts for reaching monthly budget thresholds"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply { description = desc }
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)
        }
    }

    private fun sendSpendingNotification(message: String) {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Spending Alert")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        NotificationManagerCompat.from(this).notify(System.currentTimeMillis().toInt(), builder.build())
    }
}

