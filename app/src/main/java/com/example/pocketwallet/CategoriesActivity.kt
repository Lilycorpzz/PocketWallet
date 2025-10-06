package com.example.pocketwallet

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView


data class Category(
    val name: String,
    val description: String,
    val budget: Double,
    val color: Int
)

class CategoriesActivity : AppCompatActivity() {

    private lateinit var btnReturn: ImageButton
    private lateinit var addButton: ImageButton
    private lateinit var inputName: EditText
    private lateinit var inputDescription: EditText
    private lateinit var inputQuote: EditText

    private lateinit var colorCards: List<CardView>
    private var selectedColor: Int? = null

    private val categoryList = mutableListOf<Category>() // temporary storage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.categories)

        // Initialize views
        btnReturn = findViewById(R.id.btn_return)
        addButton = findViewById(R.id.imageButton)
        inputName = findViewById(R.id.input_name)
        inputDescription = findViewById(R.id.input_description)
        inputQuote = findViewById(R.id.input_quote)

        // Color card setup
        colorCards = listOf(
            findViewById(R.id.color_yellow),
            findViewById(R.id.color_gray),
            findViewById(R.id.color_green),
            findViewById(R.id.color_wine),
            findViewById(R.id.color_purple),
            findViewById(R.id.color_cyan)
        )

        setupColorSelection()

        // Return to home
        btnReturn.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        // Add new category
        addButton.setOnClickListener { saveCategory() }
    }

    private fun setupColorSelection() {
        colorCards.forEach { card ->
            card.setOnClickListener {
                // Remove border from all
                colorCards.forEach { it.cardElevation = 0f }
                // Highlight selected
                card.cardElevation = 15f
                selectedColor = (card.cardBackgroundColor?.defaultColor ?: Color.GRAY)
            }
        }
    }

    private fun saveCategory() {
        val name = inputName.text.toString().trim()
        val description = inputDescription.text.toString().trim()
        val quoteText = inputQuote.text.toString().trim()

        // Data validation
        if (name.isEmpty()) {
            showToast("Please enter the category name.")
            return
        }
        if (description.isEmpty()) {
            showToast("Please enter the category description.")
            return
        }
        if (quoteText.isEmpty()) {
            showToast("Please enter the budget amount.")
            return
        }
        val quote = quoteText.toDoubleOrNull()
        if (quote == null || quote <= 0) {
            showToast("Please enter a valid number for the budget.")
            return
        }
        if (selectedColor == null) {
            showToast("Please select a color for this category.")
            return
        }

        // Create and store category
        val newCategory = Category(name, description, quote, selectedColor!!)
        categoryList.add(newCategory)

        showToast("Category '$name' added successfully!")

        // Reset input fields
        inputName.text.clear()
        inputDescription.text.clear()
        inputQuote.text.clear()
        colorCards.forEach { it.cardElevation = 0f }
        selectedColor = null
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}