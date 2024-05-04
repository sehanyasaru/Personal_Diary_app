package com.learnandroid.loginsqlite

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log


// MessageModel.kt


class DBHelper(context: Context) : SQLiteOpenHelper(context, DBNAME, null, 1) {
    val messages = mutableListOf<String>()
    val date = mutableListOf<String>()
    val time= mutableListOf<String>()

    companion object {
        const val DBNAME = "userinformation.db"
    }

    override fun onCreate(MyDB: SQLiteDatabase) {
        MyDB.execSQL("CREATE TABLE users(username TEXT PRIMARY KEY, password TEXT)")
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

        try {
            val projection = arrayOf("id", "message", "Date", "Time")
            val sortOrder = "Date DESC, Time DESC"

            // Query the database
            val cursor = db.query(
                username,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
            )

            while (cursor.moveToNext()) {
                val message = cursor.getString(cursor.getColumnIndexOrThrow("message"))
                val date = cursor.getString(cursor.getColumnIndexOrThrow("Date"))
                val time = cursor.getString(cursor.getColumnIndexOrThrow("Time"))
                val formattedMessage = "$date                                    $time\n\n$message"
                messages.add(formattedMessage)
            }

            cursor.close()
        } catch (e: SQLiteException) {
            e.printStackTrace()
        } finally {
            db.close()
        }

        return messages
    }
    fun getUserImages(username: String): List<String> {
        val images = mutableListOf<String>()
        val db = writableDatabase

        try {
            val projection = arrayOf("id", "image")

            // Query the database
            val cursor = db.query(
                username,
                projection,
                null,
                null,
                null,
                null,
                null
            )

            while (cursor.moveToNext()) {
                // Retrieve the image URL from the cursor
                val image = cursor.getString(cursor.getColumnIndexOrThrow("image"))
                // Add the image URL to the list
                images.add(image)
            }

            // Close the cursor
            cursor.close()
        } catch (e: SQLiteException) {
            // Handle the SQLite exception (e.g., table not found, column not found)
            e.printStackTrace() // Log the exception for debugging
            // You can return an empty list or handle the error as needed
            // images remains empty if an error occurs during database query
        } finally {
            // Close the database
            db.close()
        }

        return images
    }
    fun getmessages(): List<String>{
        return messages


    }
    fun getdate():List<String>{

        return date
    }
    fun gettime():List<String>{

        return time
    }

    fun getMessagesid(username: String): List<Int> {
        val messageIds = mutableListOf<Int>()
        val db = writableDatabase

        try {
            val projection = arrayOf("id")
            val sortOrder = "Date DESC, Time DESC"

            // Query the database
            val cursor = db.query(
                username,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
            )

            while (cursor.moveToNext()) {
                // Retrieve the ID as a string from the cursor
                val idString = cursor.getString(cursor.getColumnIndexOrThrow("id"))

                // Try to convert the ID string to an integer
                val idInt = idString.toIntOrNull()

                // Check if conversion was successful and add to list
                idInt?.let {
                    messageIds.add(it)
                }
            }

            // Close the cursor
            cursor.close()
        } catch (e: SQLiteException) {

            e.printStackTrace()

        } finally {

            db.close()
        }

        return messageIds
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


        if (cursor.moveToFirst()) {
            val message = cursor.getString(cursor.getColumnIndexOrThrow("message"))
            val date = cursor.getString(cursor.getColumnIndexOrThrow("Date"))
            val time = cursor.getString(cursor.getColumnIndexOrThrow("Time"))


            editor.putString("stored_message", message)
            editor.putString("stored_date", date)
            editor.putString("stored_time", time)
            editor.apply()
        }


        cursor.close()
        db.close()
    }
    fun updateUserData(username :String,selectedId: Int, message: String, selectedTime: String, selectedDate: String): Boolean {
        val db = writableDatabase
        val contentValues = ContentValues()


        contentValues.put("message", message)
        contentValues.put("time", selectedTime)
        contentValues.put("date", selectedDate)


        val selection = "id = ?"
        val selectionArgs = arrayOf(selectedId.toString())


        val rowsAffected = db.update(username, contentValues, selection, selectionArgs)


        db.close()


        return rowsAffected > 0
    }


    fun insertData(username: String, password: String): Boolean {
        val MyDB = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("username", username)
        contentValues.put("password", password)
        val result = MyDB.insert("users", null, contentValues)
        return result != -1L
    }
    fun insertDatatouser(username:String,message:String,selectedtime:String,selecteddate:String):Boolean{
        val MyDB = writableDatabase
        val contentValues = ContentValues()
        //contentValues.put("username",username)
        contentValues.put("message",message)
        contentValues.put("Date",selecteddate)
        contentValues.put("Time",selectedtime)
        val result = MyDB.insert(username, null, contentValues)
        return result != -1L
    }
    fun insertimage(username:String,image:String):Boolean{
        val MyDB = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("image",image)
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
        val db = this.writableDatabase
        val tableName = username
        val createTableQuery = "CREATE TABLE IF NOT EXISTS $tableName " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "message TEXT, " +
                "Date TEXT, " +
                "Time TEXT)"
        try {
            db.execSQL(createTableQuery)
            return true
        } catch (e: SQLException) {
            Log.e("Database", "Error creating table: $e")
            return false
        }
    }

    fun userimage(username: String): Boolean {
        val db = writableDatabase
        val tableName = username
        val createTableQuery = "CREATE TABLE IF NOT EXISTS $tableName " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "image TEXT) "

        try {
            db.execSQL(createTableQuery)
            return true
        } catch (e: SQLException) {
            Log.e("Database", "Error creating table: $e")
            return false
        } finally {
            db.close() // Close the database connection after executing the query
        }
    }




    @SuppressLint("Range")
fun getpassword(username:String): String? {
    val MyDB = writableDatabase
    val cursor: Cursor = MyDB.rawQuery("SELECT password FROM users WHERE username = ? ", arrayOf(username))
    var key: String? = null

    if (cursor.moveToFirst()) {

        key = cursor.getString(cursor.getColumnIndex("password"))
    }

    cursor.close()
    return key
}


}
