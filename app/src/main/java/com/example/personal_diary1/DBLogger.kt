package com.learnandroid.loginsqlite

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

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
        contentValues.put("Date",selecteddate)
        contentValues.put("Time",selectedtime)
        val result = MyDB.insert(username, null, contentValues)
        return result != -1L
    }

    fun checkUsername(username: String): Boolean {
        val MyDB = writableDatabase
        val cursor: Cursor = MyDB.rawQuery("SELECT * FROM users WHERE username = ?", arrayOf(username))
        return cursor.count > 0
    }
    fun createUserDataTable(username: String): Boolean {
        val db = writableDatabase
        if (!isTableExists(db,username)) {
            // Table doesn't exist, create it
            db.execSQL("CREATE TABLE $username (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, Date TEXT, Time TEXT)")
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
        val cursor: Cursor = MyDB.rawQuery("SELECT indexnumber FROM users WHERE username = ? AND password = ?", arrayOf(username))
        var key: String? = null

        if (cursor.moveToFirst()) {
            // Retrieve the key value from the cursor
            key = cursor.getString(cursor.getColumnIndex("indexnumber"))
        }

        cursor.close()
        return key
    }
}
