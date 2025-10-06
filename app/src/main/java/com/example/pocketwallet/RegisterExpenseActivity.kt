package com.example.pocketwallet

import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity


data class Expense(
    val name: String,
    val description: String,
    val category: String,
    val value: Double,
    val photoUri: Uri? = null
)

class RegisterExpenseActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var descInput: EditText
    private lateinit var categoryInput: EditText
    private lateinit var valueInput: EditText
    private lateinit var addPhotoBtn: Button
    private lateinit var addExpenseBtn: ImageButton
    private var selectedImageUri: Uri? = null

    // Temporary storage of expenses
    private val expensesList = mutableListOf<Expense>()

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

        nameInput = findViewById(R.id.input_name)
        descInput = findViewById(R.id.input_description)
        categoryInput = findViewById(R.id.input_category)
        valueInput = findViewById(R.id.input_value)
        addPhotoBtn = findViewById(R.id.button_add_photo)
        addExpenseBtn = findViewById(R.id.button_add_expense)

        addPhotoBtn.setOnClickListener { pickImageLauncher.launch("image/*") }
        addExpenseBtn.setOnClickListener { saveExpense() }

        findViewById<ImageButton?>(R.id.return_button)?.setOnClickListener { finish() }
    }

    private fun saveExpense() {
        val name = nameInput.text.toString().trim()
        val description = descInput.text.toString().trim()
        val category = categoryInput.text.toString().trim()
        val value = valueInput.text.toString().trim()

        // Data validation
        when {
            name.isEmpty() -> {
                Toast.makeText(this, "Please enter the expense name.", Toast.LENGTH_SHORT).show()
                return
            }

            category.isEmpty() -> {
                Toast.makeText(this, "Please enter the category.", Toast.LENGTH_SHORT).show()
                return
            }

            value.isEmpty() -> {
                Toast.makeText(this, "Please enter the value.", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // Parse value
        val valuetemp = try {
            value.toDouble()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Please enter a valid number for value.", Toast.LENGTH_SHORT)
                .show()
            return
        }

        // Create expense object
        val expense = Expense(name, description, category, valuetemp, selectedImageUri)

        // Add to in-memory list
        expensesList.add(expense)

        // Notify user
        val photoStatus = if (selectedImageUri != null) "with photo" else "without photo"
        Toast.makeText(
            this,
            "Expense noted:\nName: $name\nCategory: $category\nValue: R$valuetemp\nPhoto: $photoStatus",
            Toast.LENGTH_LONG
        ).show()

        // Clear input fields for next entry
        nameInput.text.clear()
        descInput.text.clear()
        categoryInput.text.clear()
        valueInput.text.clear()
        addPhotoBtn.text = "Add Photo (Optional)"
        selectedImageUri = null
    }
}
