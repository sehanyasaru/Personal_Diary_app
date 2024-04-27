package com.example.personal_diary1


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.learnandroid.loginsqlite.DBHelper

class AddnewActivity : AppCompatActivity(){
    private lateinit var sharedPreferences: SharedPreferences
    private var username=""
    private var selectedtime=""
    private var selecteddate=""
    private var message=""


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.addnewactivity )
        val DB= DBHelper(this)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        username = sharedPreferences.getString("username", "") ?: ""
        val date=findViewById<TextView>(R.id.textView14)
        val time=findViewById<TextView>(R.id.textView16)
        val text=findViewById<EditText>(R.id.textView11)
        val add=findViewById<Button>(R.id.button6)
      val back=findViewById<ImageView>(R.id.imageView)
//        val check=findViewById<Button>(R.id.button7)
////        check.setOnClickListener {
////            if (DB.deleteTable(username)) {
////                Log.d("Encryption", "table removed successfully")
////            } else {
////                Log.d("Encryption", "table not exisit")
////            }
////        }
        date.setOnClickListener {
            openDatePicker(date)
        }
        time.setOnClickListener{
           openTimePicker(time)
        }
        back.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
        add.setOnClickListener {
            message=text.text.toString()
            text.setText("")
            date.setText("")
            time.setText("")
            if(selecteddate.isEmpty()||selectedtime.isEmpty()||message.isEmpty()){
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
                val insert= DB.insertDatatouser(username,message,selectedtime,selecteddate)
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
    @SuppressLint("SuspiciousIndentation")
    private fun openDatePicker(date:TextView) {

        val datePickerDialog = DatePickerDialog(this, android.R.style.Theme_Dialog,
            { datePicker, year, month, day ->
              selecteddate="$year.${month+1}.$day"
                date.setText("$year.${month+1}.$day")

            }, 2023, 1, 20
        )
        datePickerDialog.show()


    }
    private fun openTimePicker(time:TextView) {

        val timePickerDialog = TimePickerDialog(this, android.R.style.Theme_Dialog,
            { timePicker, hour, minute ->
               selectedtime="$hour:$minute"
                time.setText("$hour:$minute")

            }, 15, 30, false
        )
        timePickerDialog.show()

    }
}