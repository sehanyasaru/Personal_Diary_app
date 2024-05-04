package com.example.personal_diary1
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
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
    private lateinit var sharedPreferences: SharedPreferences
private lateinit var DB:DBHelper
private lateinit var encodedImages:List<String>
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.viewactivity)

        DB = DBHelper(this)
        val back=findViewById<ImageView>(R.id.back)
        back.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "") ?: ""
        Log.d("username","$username")
        val userMessages = DB.getUserMessages(username)
        Log.d("Messages", userMessages.size.toString())
        val messageid=DB.getMessagesid(username)
         encodedImages = DB.getUserImages("image$username")
       listView = findViewById(R.id.listview)
        val adapter = CustomAdapter(this,R.layout.list_itm,userMessages,encodedImages)
        listView.adapter = adapter
        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val selectedId = messageid[position]
            DB.retrieveAndStoreMessageData(this ,selectedId,username)
            val sharedPreferences: SharedPreferences = getSharedPreferences("MessageData", Context.MODE_PRIVATE)
            val storedMessage = sharedPreferences.getString("stored_message", "")
            val storedDate = sharedPreferences.getString("stored_date", "")
            val storedTime = sharedPreferences.getString("stored_time", "")





            val dialogView = layoutInflater.inflate(R.layout.dialog_custom_layout, null)
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
            editMessage.setText( storedMessage)
            editTime.setText( storedTime)
            editDate.setText(storedDate )

            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.apply {
                setTitle("Message Details")
                setView(dialogView)
                setPositiveButton("OK") { dialog, which ->
                    updatelist(DB.getUserMessages(username))
                    dialog.dismiss()
                }
            }


            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }

    }



    private fun updatelist(messagesid: List<String>){
        val adapter = CustomAdapter(this, R.layout.list_itm, messagesid, encodedImages)
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
