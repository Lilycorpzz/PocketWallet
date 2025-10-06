package com.example.pocketwallet

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.example.pocketwallet.data.AppDatabase
import com.example.pocketwallet.data.CategoryEntity
import kotlinx.coroutines.launch

class CategoriesActivity : AppCompatActivity() {

    private lateinit var btnReturn: ImageButton
    private lateinit var addButton: ImageButton
    private lateinit var inputName: EditText
    private lateinit var inputDescription: EditText
    private lateinit var inputQuote: EditText

    private lateinit var colorCards: List<CardView>
    private var selectedColor: Int? = null

    // Database
    private val db by lazy { AppDatabase.getDatabase(this) }

    // Slot references (4 available)
    private lateinit var slotImages: List<ImageView>
    private lateinit var slotTexts: List<TextView>
    private var currentSlotIndex = 0 // track which slot to update next

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.categories)

        btnReturn = findViewById(R.id.btn_return)
        addButton = findViewById(R.id.imageButton)
        inputName = findViewById(R.id.input_name)
        inputDescription = findViewById(R.id.input_description)
        inputQuote = findViewById(R.id.input_quote)

        colorCards = listOf(
            findViewById(R.id.color_yellow),
            findViewById(R.id.color_gray),
            findViewById(R.id.color_green),
            findViewById(R.id.color_wine),
            findViewById(R.id.color_purple),
            findViewById(R.id.color_cyan)
        )

        setupColorSelection()

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

        btnReturn.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        addButton.setOnClickListener { saveCategory() }
    }

    private fun setupColorSelection() {
        colorCards.forEach { card ->
            card.setOnClickListener {
                colorCards.forEach { it.cardElevation = 0f }
                card.cardElevation = 15f
                selectedColor = card.cardBackgroundColor.defaultColor
            }
        }
    }

    private fun saveCategory() {
        val name = inputName.text.toString().trim()
        val description = inputDescription.text.toString().trim()
        val quoteText = inputQuote.text.toString().trim()

        if (name.isEmpty() || description.isEmpty() || quoteText.isEmpty()) {
            showToast("Please fill all fields.")
            return
        }

        val budget = quoteText.toDoubleOrNull()
        if (budget == null || budget <= 0) {
            showToast("Invalid budget value.")
            return
        }

        if (selectedColor == null) {
            showToast("Please select a color.")
            return
        }

        val category = CategoryEntity(
            name = name,
            description = description,
            budget = budget,
            color = selectedColor!!
        )

        // Save to DB
        lifecycleScope.launch {
            db.categoryDao().insert(category)
        }

        // Update UI slot
        updateSlotDisplay(category)
        showToast("Category '$name' saved!")

        inputName.text.clear()
        inputDescription.text.clear()
        inputQuote.text.clear()
        colorCards.forEach { it.cardElevation = 0f }
        selectedColor = null
    }

    private fun updateSlotDisplay(category: CategoryEntity) {
        val image = slotImages[currentSlotIndex]
        val text = slotTexts[currentSlotIndex]
        image.setColorFilter(category.color, PorterDuff.Mode.SRC_ATOP)
        text.text = "${category.name}: R ${String.format("%.2f", category.budget)}"
        currentSlotIndex = (currentSlotIndex + 1) % 4
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}