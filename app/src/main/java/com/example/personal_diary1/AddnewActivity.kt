package com.example.personal_diary1


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.learnandroid.loginsqlite.DBHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.IOException

class AddnewActivity : AppCompatActivity(){
    private lateinit var sharedPreferences: SharedPreferences
    private var username=""
    private var message=""

    private var job: Job? = null
    private lateinit var dateTextView: TextView
    private lateinit var image:ImageView
    private lateinit var timeTextView: TextView
    private  lateinit var base64String:String
    @SuppressLint("MissingInflatedId", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.addnewactivity )
        val DB= DBHelper(this)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        username = sharedPreferences.getString("username", "") ?: ""
        dateTextView=findViewById(R.id.textView14)
        timeTextView=findViewById(R.id.textView16)
        val text=findViewById<EditText>(R.id.textView11)
        val add=findViewById<Button>(R.id.button6)
      val back=findViewById<ImageView>(R.id.imageView)
         image=findViewById(R.id.image1)
        startDateTimeUpdates()
        image.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }
  back.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
        add.setOnClickListener {
            message=text.text.toString()
            text.setText("")
            if(message.isEmpty()){
                Toast.makeText(
                    this@AddnewActivity,
                    "Fields can't be empty",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else{
              //  Log.d("Encryption", "Your message is: $message your date is : $selecteddate your time is :$selectedtime")
                if(DB.createUserDataTable(username)==true){
                    Toast.makeText(
                        this@AddnewActivity,
                        "Created the table $username successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else{
                    Toast.makeText(
                        this@AddnewActivity,
                        "The table is already exist",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Log.d("image encodings", "Encoded image: $base64String")
                val insert= DB.insertDatatouser(username,message,getCurrentTime("HH:mm"),getCurrentDate("yyyy.MM.dd"))
                if(insert){
                    Toast.makeText(
                        this@AddnewActivity,
                        "Values are inserted successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else{
                    Toast.makeText(
                        this@AddnewActivity,
                        "Values are not inserted",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }




}

override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    Log.d("onActivityResult", "requestCode: $requestCode, resultCode: $resultCode")

    if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
        data?.data?.let { uri ->
            image.setImageURI(uri)
            val bitmap: Bitmap? = uriToBitmap(uri)
            bitmap?.let { bitmap ->
             base64String = bitmapToBase64(bitmap)


            }
        }
    }
}

    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            contentResolver.openInputStream(uri)?.use {
                Bitmap.createBitmap(BitmapFactory.decodeStream(it))
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
        return  Base64.encodeToString(byteArray, Base64.DEFAULT)

    }

    companion object {
        private val PICK_IMAGE_REQUEST = 1
    }
    private fun startDateTimeUpdates() {
        job?.cancel() // Cancel any existing job to avoid overlapping

        job = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                // Get current date and time
                val currentDate = getCurrentDate("yyyy.MM.dd")
                val currentTime = getCurrentTime("HH:mm")


                dateTextView.text = currentDate
                timeTextView.text = currentTime

                delay(60000)
            }
        }
    }

    private fun getCurrentDate(format: String): String {
        val currentTimeMillis = System.currentTimeMillis()
        return android.text.format.DateFormat.format(format, currentTimeMillis).toString()
    }

    private fun getCurrentTime(format: String): String {
        val currentTimeMillis = System.currentTimeMillis()
        return android.text.format.DateFormat.format(format, currentTimeMillis).toString()
    }

}