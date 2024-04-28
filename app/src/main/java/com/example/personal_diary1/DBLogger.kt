package com.learnandroid.loginsqlite

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
// MessageModel.kt


class DBHelper(context: Context) : SQLiteOpenHelper(context, DBNAME, null, 1) {

    companion object {
        const val DBNAME = "userinformation.db"
    }

    override fun onCreate(MyDB: SQLiteDatabase) {
        MyDB.execSQL("CREATE TABLE users(username TEXT PRIMARY KEY, password TEXT,indexnumber TEXT)")
    }

    override fun onUpgrade(MyDB: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        MyDB.execSQL("DROP TABLE IF EXISTS users")
    }

//    fun deleteTable(username: String): Boolean {
//        val MyDB = writableDatabase
//
//        return try {
//            MyDB.execSQL("DROP TABLE IF EXISTS $username")
//            true // Table dropped successfully
//        } catch (e: Exception) {
//            e.printStackTrace()
//            false // Error occurred while dropping the table
//        }
//    }


    fun getUserMessages(username: String): List<String> {
        val messages = mutableListOf<String>()
        val db = writableDatabase

        // Define the columns you want to retrieve
        val projection = arrayOf("id","message", "Date", "Time")



        // Sort the results by Date and Time in descending order
        val sortOrder = "Date DESC, Time DESC"

        // Query the database
        val cursor = db.query(
            username,       // The table name
            projection,
            null,// The columns to retrieve
            null,// ,  // The values for the WHERE clause
            null,           // Don't group the rows
            null,           // Don't filter by row groups
            sortOrder       // Sort the results
        )

        // Iterate through the cursor to retrieve the data
        while (cursor.moveToNext()) {
            val id = cursor.getString(cursor.getColumnIndexOrThrow("id"))

            val message = cursor.getString(cursor.getColumnIndexOrThrow("message"))
            val date = cursor.getString(cursor.getColumnIndexOrThrow("Date"))
            val time = cursor.getString(cursor.getColumnIndexOrThrow("Time"))

            // Format the retrieved data as needed
            val formattedMessage = "Date: $date, Time: $time\n$message, Id: $id"
            messages.add(formattedMessage)
        }

        // Close the cursor and database
        cursor.close()
        db.close()

        return messages
    }
    fun getMessagesid(username: String): List<Int> {
        val messageid = mutableListOf<Int>()
        val db = writableDatabase

        // Define the columns you want to retrieve
        val projection = arrayOf("id")



        // Sort the results by Date and Time in descending order
        val sortOrder = "Date DESC, Time DESC"

        // Query the database
        val cursor = db.query(
            username,       // The table name
            projection,
            null,// The columns to retrieve
            null,// ,  // The values for the WHERE clause
            null,           // Don't group the rows
            null,           // Don't filter by row groups
            sortOrder       // Sort the results
        )

        // Iterate through the cursor to retrieve the data
        while (cursor.moveToNext()) {
            val id = cursor.getString(cursor.getColumnIndexOrThrow("id"))
            val idInt = id.toIntOrNull() // Use toIntOrNull() to handle potential null or non-integer values
            idInt?.let {
                messageid.add(idInt) // Add the parsed integer to the list if conversion is successful
            }
        }

        // Close the cursor and database
        cursor.close()
        db.close()

        return messageid
    }
    fun retrieveAndStoreMessageData(context: Context, selectedId: Int,username:String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MessageData", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        val db = writableDatabase

        // Define the columns you want to retrieve
        val projection = arrayOf("message", "Date", "Time")

        // Filter the results by selected ID
        val selection = "id = ?"
        val selectionArgs = arrayOf(selectedId.toString())

        // Query the database
        val cursor = db.query(
            username,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        // Iterate through the cursor to retrieve the data
        if (cursor.moveToFirst()) {
            val message = cursor.getString(cursor.getColumnIndexOrThrow("message"))
            val date = cursor.getString(cursor.getColumnIndexOrThrow("Date"))
            val time = cursor.getString(cursor.getColumnIndexOrThrow("Time"))

            // Store retrieved data in SharedPreferences
            editor.putString("stored_message", message)
            editor.putString("stored_date", date)
            editor.putString("stored_time", time)
            editor.apply()
        }

        // Close the cursor and database
        cursor.close()
        db.close()
    }
    fun updateUserData(username :String,selectedId: Int, message: String, selectedTime: String, selectedDate: String): Boolean {
        val db = writableDatabase
        val contentValues = ContentValues()

        // Prepare the values to be updated
        contentValues.put("message", message)
        contentValues.put("time", selectedTime)
        contentValues.put("date", selectedDate)

        // Specify the WHERE clause to identify the specific record to update based on ID
        val selection = "id = ?"
        val selectionArgs = arrayOf(selectedId.toString())

        // Perform the update operation
        val rowsAffected = db.update(username, contentValues, selection, selectionArgs)

        // Close the database connection
        db.close()

        // Check if the update was successful (rowsAffected > 0 means at least one row was updated)
        return rowsAffected > 0
    }


    fun insertData(username: String, password: String, index: String): Boolean {
        val MyDB = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("username", username)
        contentValues.put("password", password)
        contentValues.put("indexnumber",index)
        val result = MyDB.insert("users", null, contentValues)
        return result != -1L
    }
    fun insertDatatouser(username:String,message:String,selectedtime:String,selecteddate:String):Boolean{
        val MyDB = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("username",username)
        contentValues.put("message",message)
        contentValues.put("Date",selecteddate)
        contentValues.put("Time",selectedtime)
        val result = MyDB.insert(username, null, contentValues)
        return result != -1L
    }
    @SuppressLint("Range")


    fun checkUsername(username: String): Boolean {
        val MyDB = writableDatabase
        val cursor: Cursor = MyDB.rawQuery("SELECT * FROM users WHERE username = ?", arrayOf(username))
        return cursor.count > 0
    }
    fun createUserDataTable(username: String): Boolean {
        val db = writableDatabase
        if (!isTableExists(db,username)) {
            // Table doesn't exist, create it
            db.execSQL("CREATE TABLE $username (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT,message TEXT, Date TEXT, Time TEXT)")
            return true
        }
        return false  // Table already exists
    }

    private fun isTableExists(db: SQLiteDatabase, tableName: String): Boolean {
        val cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = 'table' AND name = ?", arrayOf(tableName))
        var tableExists = false

        cursor.use { cursor ->
            if (cursor.moveToFirst()) {
                val count = cursor.getInt(0)
                tableExists = count > 0
            }
        }

        return tableExists
    }
@SuppressLint("Range")
fun getpassword(username:String): String? {
    val MyDB = writableDatabase
    val cursor: Cursor = MyDB.rawQuery("SELECT password FROM users WHERE username = ? ", arrayOf(username))
    var key: String? = null

    if (cursor.moveToFirst()) {
        // Retrieve the key value from the cursor
        key = cursor.getString(cursor.getColumnIndex("password"))
    }

    cursor.close()
    return key
}

    fun checkUsernamePassword(username: String, password: String): Boolean {
        val MyDB = writableDatabase
        val cursor: Cursor = MyDB.rawQuery("SELECT * FROM users WHERE username = ? AND password = ?", arrayOf(username, password))
        return cursor.count > 0
    }
    @SuppressLint("Range")
    fun getindex(username:String): String? {
        val MyDB = writableDatabase
        val cursor: Cursor = MyDB.rawQuery("SELECT indexnumber FROM users WHERE username = ? ", arrayOf(username))
        var key: String? = null

        if (cursor.moveToFirst()) {
            // Retrieve the key value from the cursor
            key = cursor.getString(cursor.getColumnIndex("indexnumber"))
        }

        cursor.close()
        return key
    }
}
