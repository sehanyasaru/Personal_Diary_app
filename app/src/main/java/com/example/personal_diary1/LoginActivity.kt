package com.example.personal_diary1


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.learnandroid.loginsqlite.DBHelper
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class LoginActivity: AppCompatActivity(){
    private lateinit var sharedPreferences: SharedPreferences


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.loginactivity)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val button = findViewById<Button>(R.id.button4)
        val button2 = findViewById<Button>(R.id.button2)
        val username = findViewById<EditText>(R.id.textView4)
        val password = findViewById<EditText>(R.id.textView7)
        val DB = DBHelper(this)
        button2.setOnClickListener {
            val intent = Intent(applicationContext, SignUpActivity::class.java)
            startActivity(intent)
        }
        button.setOnClickListener {
            val username = username.text.toString()
            val password = password.text.toString()
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    this@LoginActivity,
                    "Please enter all the fields",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val passwordnew = DB.getpassword(username)

                if (passwordnew != null) {
                    val checkuserpass = decryptPassword(
                        passwordnew,
                        SecretKeySpec("mySecretKey12345".toByteArray(), "AES/ECB/PKCS5Padding")
                    )
                    if (password== checkuserpass) {
                        sharedPreferences.edit().putString("username", username).apply()
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Invalid password or username",
                            Toast.LENGTH_SHORT
                        ).show()
                    }


                }
            }
        }
    }
    private fun encryptPassword(password: String, aesKey: SecretKey): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, aesKey)
        val encryptedBytes = cipher.doFinal(password.toByteArray(Charsets.UTF_8))
        val encryptedPasswordBase64 = android.util.Base64.encodeToString(encryptedBytes, android.util.Base64.DEFAULT)
        Log.d("Encryption", "Encrypted password is: $encryptedPasswordBase64")

        return encryptedPasswordBase64
    }
    private fun decryptPassword(encryptedPasswordBase64: String, aesKey: SecretKey): String {
        try {
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, aesKey)
            val encryptedBytes = Base64.decode(encryptedPasswordBase64, Base64.DEFAULT)
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            val decryptedPassword = String(decryptedBytes, Charsets.UTF_8)
            Log.d("Decryption", "Decrypted password is: $decryptedPassword")
            return decryptedPassword
        } catch (e: Exception) {
            Log.e("Decryption", "Error decrypting password: ${e.message}")
            return "" // Return empty string or handle the error accordingly
        }
    }

}