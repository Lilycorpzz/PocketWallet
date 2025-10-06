package com.example.pocketwallet

import android.content.Intent
import android.graphics.PorterDuff
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

    // Store added categories temporarily
    private val categoryList = mutableListOf<Category>()

    // Slot references (4 available)
    private lateinit var slotImages: List<ImageView>
    private lateinit var slotTexts: List<TextView>
    private var currentSlotIndex = 0 // track which slot to update next

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.categories)

        // Initialize input fields
        btnReturn = findViewById(R.id.btn_return)
        addButton = findViewById(R.id.imageButton)
        inputName = findViewById(R.id.input_name)
        inputDescription = findViewById(R.id.input_description)
        inputQuote = findViewById(R.id.input_quote)

        // Color picker cards
        colorCards = listOf(
            findViewById(R.id.color_yellow),
            findViewById(R.id.color_gray),
            findViewById(R.id.color_green),
            findViewById(R.id.color_wine),
            findViewById(R.id.color_purple),
            findViewById(R.id.color_cyan)
        )

        setupColorSelection()

        // Link category display slots (these are your 4 TextViews + ImageViews)
        slotImages = listOf(
            findViewById(R.id.img_food),
            findViewById(R.id.img_transport),
            findViewById(R.id.img_housing),
            findViewById(R.id.img_leisure)
        )

        slotTexts = listOf(
            findViewById(R.id.txt_food),
            findViewById(R.id.txt_transport),
            findViewById(R.id.txt_housing),
            findViewById(R.id.txt_leisure)
        )

        // Return button logic
        btnReturn.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        // Add category button
        addButton.setOnClickListener { saveCategory() }
    }

    private fun setupColorSelection() {
        colorCards.forEach { card ->
            card.setOnClickListener {
                // Remove elevation highlight from all
                colorCards.forEach { it.cardElevation = 0f }
                // Highlight selected
                card.cardElevation = 15f
                selectedColor = card.cardBackgroundColor?.defaultColor
            }
        }
    }

    private fun saveCategory() {
        val name = inputName.text.toString().trim()
        val description = inputDescription.text.toString().trim()
        val quoteText = inputQuote.text.toString().trim()

        // Input validation
        if (name.isEmpty()) {
            showToast("Please enter the category name.")
            return
        }
        if (description.isEmpty()) {
            showToast("Please enter the description.")
            return
        }
        if (quoteText.isEmpty()) {
            showToast("Please enter the budget.")
            return
        }
        val budget = quoteText.toDoubleOrNull()
        if (budget == null || budget <= 0) {
            showToast("Please enter a valid budget amount.")
            return
        }
        if (selectedColor == null) {
            showToast("Please select a color.")
            return
        }

        // Create category
        val category = Category(name, description, budget, selectedColor!!)
        categoryList.add(category)

        // Update the display slot
        updateSlotDisplay(category)

        // Feedback and reset
        showToast("Category '$name' added!")
        inputName.text.clear()
        inputDescription.text.clear()
        inputQuote.text.clear()
        colorCards.forEach { it.cardElevation = 0f }
        selectedColor = null
    }

    private fun updateSlotDisplay(category: Category) {
        // Get current slot (0–3)
        val image = slotImages[currentSlotIndex]
        val text = slotTexts[currentSlotIndex]

        // Update UI
        image.setColorFilter(category.color, PorterDuff.Mode.SRC_ATOP)
        text.text = "${category.name}: R ${String.format("%.2f", category.budget)}"

        // Move to next slot (looping)
        currentSlotIndex = (currentSlotIndex + 1) % 4
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}