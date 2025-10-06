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
    val type: String, // "Income" or "Expense"
    val photoUri: Uri? = null
)

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

    private val entriesList = mutableListOf<Expense>()

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
// Spinner setup
        val typeSpinner = findViewById<Spinner>(R.id.type_spinner)

        val typeOptions = resources.getStringArray(R.array.income_expense_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, typeOptions)
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
            Toast.makeText(this, "Enter a valid number for value.", Toast.LENGTH_SHORT).show()
            return
        }

        val entry = Expense(name, description, category, value, type, selectedImageUri)
        entriesList.add(entry)

        // Update total based on type
        totalBalance += if (type == "Income") value else -value
        totalValueField.setText("R %.2f".format(totalBalance))

        val sign = if (type == "Income") "+" else "-"
        Toast.makeText(
            this,
            "$type recorded:\n$name — $sign R$value\nCategory: $category",
            Toast.LENGTH_LONG
        ).show()

        // Reset fields
        nameInput.text.clear()
        descInput.text.clear()
        categoryInput.text.clear()
        valueInput.text.clear()
        addPhotoBtn.text = "Add Photo (Optional)"
        selectedImageUri = null
    }
}
