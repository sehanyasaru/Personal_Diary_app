package com.example.personal_diary1

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.learnandroid.loginsqlite.DBHelper
class ViewActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private var selectedtime=""
    private var selecteddate=""
    private var message=""

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.viewactivity)

        val DB = DBHelper(this)
        listView = findViewById(R.id.listview)
        val back=findViewById<ImageView>(R.id.back)
        back.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }

        // Retrieve messages for a specific user (change 'sehan' to your desired username)
        val username = "sehan"
        val userMessages = DB.getUserMessages(username)
        val messageid=DB.getMessagesid(username)

        // Create an ArrayAdapter to populate the ListView with messages
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, userMessages)

        // Set the adapter for the ListView
        listView.adapter = adapter
        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val selectedId = messageid[position] // Get the selected ID from the list

            // Retrieve and store message, date, and time based on the selected ID
            DB.retrieveAndStoreMessageData(this ,selectedId,username)

            // Retrieve stored message, date, and time values separately from SharedPreferences
            val sharedPreferences: SharedPreferences = getSharedPreferences("MessageData", Context.MODE_PRIVATE)
            val storedMessage = sharedPreferences.getString("stored_message", "")
            val storedDate = sharedPreferences.getString("stored_date", "")
            val storedTime = sharedPreferences.getString("stored_time", "")



            // Inflate custom layout for AlertDialog
            val dialogView = layoutInflater.inflate(R.layout.dialog_custom_layout, null)

            // Find EditText widgets in the custom layout
            val editMessage = dialogView.findViewById<EditText>(R.id.editTextMessage)
            val editTime = dialogView.findViewById<TextView>(R.id.editTexttime)
            val editDate=dialogView.findViewById<TextView>(R.id.editTextDate)
            val button=dialogView.findViewById<Button>(R.id.update)

            editDate.setOnClickListener {
                openDatePicker( editDate)
            }
            editTime .setOnClickListener{
                openTimePicker(editTime )
            }
            button.setOnClickListener{
                message=editMessage.text.toString()
                if(selecteddate.isEmpty()||selectedtime.isEmpty()||message.isEmpty()){
                    Toast.makeText(
                        this@ViewActivity,
                        "Fields can't be empty",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else{
                    if(DB.updateUserData(username,selectedId,message,selectedtime,selecteddate)){
                        Toast.makeText(
                            this@ViewActivity,
                            "Values updated successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else{
                        Toast.makeText(
                            this@ViewActivity,
                            "Can't update the fields!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }


            // Set text for EditText widgets
            editMessage.setText( storedMessage)
            editTime.setText( storedTime)
            editDate.setText(storedDate )

            // Build and show an AlertDialog with custom layout
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.apply {
                setTitle("Message Details")
                setView(dialogView) // Set custom layout to AlertDialog
                setPositiveButton("OK") { dialog, which ->
                    updatelist(DB.getUserMessages(username))
                    dialog.dismiss() // Dismiss the dialog
                }
            }

            // Create and show the AlertDialog
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }

    }
    private fun updatelist(messagesid: List<String>){
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, messagesid)

        // Set the adapter for the ListView
        listView.adapter = adapter

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