package com.example.pocketwallet

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class CategoriesActivity : AppCompatActivity() {

    private lateinit var btnReturn: ImageButton
    private lateinit var addButton: ImageButton
    private lateinit var inputName: EditText
    private lateinit var inputDescription: EditText
    private lateinit var inputQuote: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.categories)

        // Initialize views
        btnReturn = findViewById(R.id.btn_return)
        addButton = findViewById(R.id.imageButton)
        inputName = findViewById(R.id.input_name)
        inputDescription = findViewById(R.id.input_description)
        inputQuote = findViewById(R.id.input_quote)

        // Go back to HomeActivity when clicking the return button
        btnReturn.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Add new category (temporary Toast feedback)
        addButton.setOnClickListener {
            val name = inputName.text.toString().trim()
            val description = inputDescription.text.toString().trim()
            val quote = inputQuote.text.toString().trim()

            if (name.isEmpty() || description.isEmpty() || quote.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Category '$name' added!", Toast.LENGTH_SHORT).show()

                // Later: Save category data (to DB or SharedPreferences)
                inputName.text.clear()
                inputDescription.text.clear()
                inputQuote.text.clear()
            }
        }
    }
}
