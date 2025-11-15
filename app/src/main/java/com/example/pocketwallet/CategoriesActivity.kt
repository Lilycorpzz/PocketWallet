package com.example.pocketwallet

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.example.pocketwallet.data.AppDatabase
import com.example.pocketwallet.data.CategoryEntity
import kotlinx.coroutines.launch
import java.util.*

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
    private lateinit var inputPeriod: Spinner
    private lateinit var inputCustomStart: EditText
    private lateinit var inputCustomEnd: EditText
    private lateinit var inputCategory: Spinner // add this at the top
    private lateinit var inputDate: EditText
    private lateinit var inputStartTime: EditText
    private lateinit var inputEndTime: EditText



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.categories)

        btnReturn = findViewById(R.id.btn_return)
        addButton = findViewById(R.id.imageButton)
        inputName = findViewById(R.id.input_name)
        inputDescription = findViewById(R.id.input_description)
        inputQuote = findViewById(R.id.input_quote)
        inputPeriod = findViewById(R.id.input_period)
        inputCustomStart = findViewById(R.id.input_custom_start)
        inputCustomEnd = findViewById(R.id.input_custom_end)
        inputCategory = findViewById(R.id.input_category) // initialize
        inputDate = findViewById(R.id.input_date)
        inputStartTime = findViewById(R.id.input_start_time)
        inputEndTime = findViewById(R.id.input_end_time)


        colorCards = listOf(
            findViewById(R.id.color_yellow),
            findViewById(R.id.color_gray),
            findViewById(R.id.color_green),
            findViewById(R.id.color_wine),
            findViewById(R.id.color_purple),
            findViewById(R.id.color_cyan)
        )



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
        setupCategorySpinner()
        setupPeriodSpinner()
        setupColorSelection()
        setupDateAndTimePickers()

        btnReturn.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        addButton.setOnClickListener { saveCategory() }


    }



    private fun setupCategorySpinner() {
        val categories = listOf(
            "Select Category", "Food", "Transport", "Housing", "Leisure",
            "Entertainment", "Health", "Education", "Shopping", "Other"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        inputCategory.adapter = adapter

        inputCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Optional: do something on selection
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }




    private fun setupPeriodSpinner() {
        val periods = listOf("Select Period", "Today", "Last 7 Days", "Last 30 Days", "This Month", "Custom Range")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, periods)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        inputPeriod.adapter = adapter

        inputPeriod.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selected = periods[position]

                if (selected == "Custom Range") {
                    inputCustomStart.visibility = View.VISIBLE
                    inputCustomEnd.visibility = View.VISIBLE
                } else {
                    inputCustomStart.visibility = View.GONE
                    inputCustomEnd.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
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

    // --- Date & Time Pickers ---
    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun setupDateAndTimePickers() {
        inputDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, y, m, d ->
                inputDate.setText("$d/${m + 1}/$y")
            }, year, month, day).show()
        }

        val timePickerListener = { editText: EditText ->
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(this, { _, h, m ->
                editText.setText(String.format("%02d:%02d", h, m))
            }, hour, minute, true).show()
        }

        inputStartTime.setOnClickListener { timePickerListener(inputStartTime) }
        inputEndTime.setOnClickListener { timePickerListener(inputEndTime) }

        // Optional: also for custom range dates
        inputCustomStart.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, y, m, d ->
                inputCustomStart.setText("$d/${m + 1}/$y")
            }, year, month, day).show()
        }

        inputCustomEnd.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, y, m, d ->
                inputCustomEnd.setText("$d/${m + 1}/$y")
            }, year, month, day).show()
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
        inputDate.text.clear()
        inputStartTime.text.clear()
        inputEndTime.text.clear()
        inputCustomStart.text.clear()
        inputCustomEnd.text.clear()
        colorCards.forEach { it.cardElevation = 0f }
        selectedColor = null
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
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