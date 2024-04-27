package com.example.personal_diary1

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.learnandroid.loginsqlite.DBHelper

class SettingsActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferences1: SharedPreferences
    private var username1="";
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.settingsactivity)

        val button = findViewById<Button>(R.id.button5)
        val username=findViewById<EditText>(R.id.textView8)
        val textViewWelcomeMessage = findViewById<TextView>(R.id.textView9)
        val back= findViewById<ImageView>(R.id.imageView2)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        sharedPreferences1 = getSharedPreferences("MyPrefs2", Context.MODE_PRIVATE)
        var DB=DBHelper(this)
        username1 = sharedPreferences.getString("username", "") ?: ""

        // Retrieve stored id from SharedPreferences
        var id = sharedPreferences1.getString("username", "") ?: ""
        var index=DB.getindex(id)
        // Update the welcome message based on the retrieved username and id
        if (username1.isNotEmpty()) {
            textViewWelcomeMessage.text = "This App was developed by $username1 ($index)"
        } else {
            textViewWelcomeMessage.text = "This App was developed by Your_Name (Student_ID)"
        }

        button.setOnClickListener {
            // Update the username when the button is clicked
            username1 = username.text.toString()
            username.setText("")
            sharedPreferences.edit().putString("username", username1).apply()

            // Update the welcome message after updating the username
            if (username1.isNotEmpty()) {
                textViewWelcomeMessage.text = "This App was developed by $username1 ($index)"
            } else {
                textViewWelcomeMessage.text = "This App was developed by Your_Name (Student_ID)"
            }
        }

        back.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
    }

}



