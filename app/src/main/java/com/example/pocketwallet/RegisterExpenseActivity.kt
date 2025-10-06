package com.example.pocketwallet

import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class RegisterExpenseActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var descInput: EditText
    private lateinit var categoryInput: EditText
    private lateinit var valueInput: EditText
    private lateinit var addPhotoBtn: Button
    private lateinit var addExpenseBtn: ImageButton
    private var selectedImageUri: Uri? = null

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
        val desc = descInput.text.toString().trim()
        val category = categoryInput.text.toString().trim()
        val value = valueInput.text.toString().trim()

        if (name.isEmpty() || value.isEmpty()) {
            Toast.makeText(this, "Please fill in both name and value.", Toast.LENGTH_SHORT).show()
            return
        }

        val photoStatus = if (selectedImageUri != null) "with photo" else "no photo"
        Toast.makeText(this, "Expense Added: $name (R$value) — $photoStatus", Toast.LENGTH_LONG).show()

        nameInput.text.clear()
        descInput.text.clear()
        categoryInput.text.clear()
        valueInput.text.clear()
        addPhotoBtn.text = "Add Photo (Optional)"
        selectedImageUri = null
    }
}
