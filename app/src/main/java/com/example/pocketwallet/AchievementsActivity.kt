package com.example.pocketwallet

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AchievementsActivity : AppCompatActivity() {

    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.achievements)

        // Initialize views
        backButton = findViewById(R.id.backButton2)

        // Optional: load references to text views or images if you want to update them
        val firstExpense = findViewById<TextView>(R.id.receita_x_despesa2)
        val stayedUnderBudget = findViewById<TextView>(R.id.receita_x_despesa3)
        val sevenDays = findViewById<TextView>(R.id.receita_x_despesa4)
        val firstIncome = findViewById<TextView>(R.id.receita_x_despesa5)
        val icons = listOf(
            findViewById<ImageView>(R.id.imageView4),
            findViewById<ImageView>(R.id.imageView5),
            findViewById<ImageView>(R.id.imageView11),
            findViewById<ImageView>(R.id.imageView12)
        )

        // TODO (optional): later you can check achievements progress here
        // Example idea: read SharedPreferences flags and update colors/icons
        // val prefs = getSharedPreferences("AchievementsPrefs", MODE_PRIVATE)
        // if (prefs.getBoolean("firstExpense", false)) firstExpense.setTextColor(getColor(R.color.green))

        // Back button logic
        backButton.setOnClickListener {
            finish() // closes AchievementsActivity and returns to previous screen
        }
    }
}
