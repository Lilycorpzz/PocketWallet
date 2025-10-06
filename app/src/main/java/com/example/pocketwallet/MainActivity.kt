package com.example.pocketwallet

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlin.jvm.java

class MainActivity : AppCompatActivity() {

    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        usernameInput = findViewById(R.id.inputUsername)
        passwordInput = findViewById(R.id.inputPassword)
        loginButton = findViewById(R.id.buttonLogin)

        loginButton.setOnClickListener {
            handleLogin()
        }
    }

    private fun handleLogin() {
        val username = usernameInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both username and password.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Welcome, $username!", Toast.LENGTH_SHORT).show()

            // Navigate to Home Page
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
