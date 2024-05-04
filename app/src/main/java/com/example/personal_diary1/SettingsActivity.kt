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
        val back= findViewById<ImageView>(R.id.back)
        sharedPreferences1 = getSharedPreferences("MyPrefs2", Context.MODE_PRIVATE)
        var DB=DBHelper(this)
        username1 = sharedPreferences1.getString("username", "") ?: ""
        textViewWelcomeMessage.text = "This App was developed by Sehan Yasaru"


        button.setOnClickListener {
            // Update the username when the button is clicked
            username1 = username.text.toString()
            username.setText("")
            sharedPreferences1.edit().putString("username", username1).apply()
        }

        back.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
    }

}



