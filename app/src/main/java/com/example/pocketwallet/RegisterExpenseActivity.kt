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

    // Room database
    private val db by lazy { AppDatabase.getDatabase(this) }

    companion object {
        private const val TAG = "RegisterExpense"
        private const val CHANNEL_ID = "budget_channel"
    }

    /** PICK PHOTO **/
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri
        if (uri != null) {
            Toast.makeText(this, "Photo added!", Toast.LENGTH_SHORT).show()
            addPhotoBtn.text = "Change Photo"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        createNotificationChannel()
        initViews()
        setupListeners()
    }

    private fun initViews() {
        typeSpinner = findViewById(R.id.type_spinner)
        totalValueField = findViewById(R.id.total_value_field)
        nameInput = findViewById(R.id.input_name)
        descInput = findViewById(R.id.input_description)
        categoryInput = findViewById(R.id.input_category)
        valueInput = findViewById(R.id.input_value)
        addPhotoBtn = findViewById(R.id.button_add_photo)
        addExpenseBtn = findViewById(R.id.button_add_expense)

        // Spinner setup
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.income_expense_array,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinner.adapter = adapter
    }

    private fun setupListeners() {
        addPhotoBtn.setOnClickListener { pickImageLauncher.launch("image/*") }
        addExpenseBtn.setOnClickListener { saveEntry() }
        findViewById<ImageButton>(R.id.return_button)?.setOnClickListener { finish() }
    }

    /** SAVE EXPENSE TO FIREBASE **/
    private fun saveExpenseToFirebase(expense: ExpenseEntity) {
        val key = FirebaseManager.db.child("expenses").push().key ?: return

        val upload = mapOf(
            "id" to key,
            "name" to expense.name,
            "description" to expense.description,
            "category" to expense.category,
            "value" to expense.value,
            "type" to expense.type,
            "photoUri" to expense.photoUri
        )

        FirebaseManager.db.child("expenses").child(key).setValue(upload)
            .addOnSuccessListener {
                Toast.makeText(this, "Expense uploaded online!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to upload expense", Toast.LENGTH_SHORT).show()
            }
    }

    /** MAIN SAVE FUNCTION **/
    private fun saveEntry() {

        val name = nameInput.text.toString().trim()
        val description = descInput.text.toString().trim()
        val category = categoryInput.text.toString().trim()
        val type = typeSpinner.selectedItem.toString()
        val valueStr = valueInput.text.toString().trim()

        // Validation
        if (name.isEmpty() || category.isEmpty() || valueStr.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields.", Toast.LENGTH_SHORT).show()
            return
        }

        val value = valueStr.toDoubleOrNull()
        if (value == null) {
            Toast.makeText(this, "Invalid number.", Toast.LENGTH_SHORT).show()
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

        // Save locally + Firebase
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                db.expenseDao().insert(expense)
                saveExpenseToFirebase(expense)

                val expenses = db.expenseDao().getAll()
                val totalSpent = expenses.filter { it.type == "Expense" }.sumOf { it.value }

                withContext(Dispatchers.Main) {
                    updateBalance(type, value)
                    clearInputs()
                    checkSpendingLimitAndNotify(totalSpent)
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error saving entry", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegisterExpenseActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun updateBalance(type: String, value: Double) {
        totalBalance += if (type == "Income") value else -value
        totalValueField.setText("R %.2f".format(totalBalance))

        Toast.makeText(
            this,
            "$type added — R$value",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun clearInputs() {
        nameInput.text.clear()
        descInput.text.clear()
        categoryInput.text.clear()
        valueInput.text.clear()
        addPhotoBtn.text = "Add Photo (Optional)"
        selectedImageUri = null
    }

    /** BUDGET CHECK **/
    private fun checkSpendingLimitAndNotify(totalSpent: Double) {
        val prefs = getSharedPreferences("BudgetPrefs", MODE_PRIVATE)
        val maxGoal = prefs.getFloat("maxGoal", -1f).toDouble()
        if (maxGoal <= 0) return

        val pct = totalSpent / maxGoal

        when {
            pct >= 1.0 -> sendSpendingNotification("You reached your monthly spending limit.")
            pct >= 0.8 -> sendSpendingNotification("You used ${(pct * 100).toInt()}% of your monthly budget.")
        }
    }

    /** NOTIFICATION SYSTEM **/
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Budget Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Budget warnings and notifications"
            }
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    private fun sendSpendingNotification(message: String) {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Spending Alert")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        /*NotificationManagerCompat.from(this)
            .notify(System.currentTimeMillis().toInt(), builder.build())*/
    }
}
