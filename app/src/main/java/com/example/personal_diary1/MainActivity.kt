package com.example.personal_diary1

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private val imageUrls = arrayOf(
        R.drawable.image1,
        R.drawable.image2,
        R.drawable.image3,
        R.drawable.image4,
        R.drawable.image5,
        R.drawable.image6,
        R.drawable.image7,
        R.drawable.image8,
        R.drawable.image9,


    )

    private val updateIntervalMillis = 86_400_000L // 24 hours in milliseconds
    private lateinit var imageView: RelativeLayout
    private var job: Job? = null // Store reference to the coroutine job
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button1=findViewById<Button>(R.id.button1)
        val button2=findViewById<Button>(R.id.button3)
        val button3=findViewById<Button>(R.id.button2)
        sharedPreferences = getSharedPreferences("MyPrefs2", Context.MODE_PRIVATE)
        button1.setOnClickListener{
            val intent = Intent(applicationContext,AddnewActivity::class.java)
            startActivity(intent)
        }
        button2.setOnClickListener {
            val intent = Intent(applicationContext,SettingsActivity::class.java)
            startActivity(intent)
        }
        button3.setOnClickListener {
            val intent = Intent(applicationContext,ViewActivity::class.java)
            startActivity(intent)
        }

        imageView = findViewById(R.id.background)



        // Retrieve the username from SharedPreferences
        val username = sharedPreferences.getString("username", null)
        val textViewWelcomeMessage = findViewById<TextView>(R.id.boxView2)
        if(username!=null){
            textViewWelcomeMessage.setText( "Welcome, $username !")
        }
        else{
            textViewWelcomeMessage.setText( "Welcome, yasaru !")
        }

        updateBackgroundPeriodically()
    }

    private fun updateBackgroundPeriodically() {
        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            while (true) {
                val randomImageResourceId = imageUrls.random()
                loadImageFromResource(randomImageResourceId)
                delay(updateIntervalMillis)
            }
        }
    }

    private suspend fun loadImageFromResource(resourceId: Int) {
        withContext(Dispatchers.Main) {
            val drawable = ContextCompat.getDrawable(this@MainActivity, resourceId)
            imageView.background=drawable
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        job?.cancel() // Cancel the coroutine job when the activity is destroyed
    }
}
