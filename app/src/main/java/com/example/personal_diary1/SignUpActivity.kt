package com.example.personal_diary1

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.learnandroid.loginsqlite.DBHelper
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class SignUpActivity : AppCompatActivity(){


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signupactivity)

        val usernameTextView = findViewById<EditText>(R.id.textView)
        val passwordTextView = findViewById<EditText>(R.id.textView3)
        val reppasswordTextView = findViewById<EditText>(R.id.textView5)
        val button=findViewById<Button>(R.id.button)
        val button2=findViewById<Button>(R.id.button2)

        var DB = DBHelper(this)
        val aesKey = SecretKeySpec("mySecretKey12345".toByteArray(), "AES/ECB/PKCS5Padding")
        button.setOnClickListener {
            val username1 = usernameTextView.text.toString()
            val password = passwordTextView.text.toString()
            val reppassword = reppasswordTextView.text.toString()
 if (username1.isEmpty() || password.isEmpty()|| reppassword.isEmpty()) {
                Toast.makeText(this@SignUpActivity, "Please enter all the fields", Toast.LENGTH_SHORT).show()
            } else {
                if (password ==  reppassword) {
                    val encryptedPassword = encryptPassword(username1,password,aesKey)
                    val checkUser = DB.checkUsername(username1)
                    if (!checkUser) {
                        val insert = DB.insertData( username1, encryptedPassword)

                        if (insert) {
                            Toast.makeText(this@SignUpActivity, "Registered successfully", Toast.LENGTH_SHORT).show()
                            val intent = Intent(applicationContext,LoginActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@SignUpActivity, "Registration failed", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@SignUpActivity, "User already exists! Please sign in", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@SignUpActivity, "Passwords not matching", Toast.LENGTH_SHORT).show()
                }
            }
        }
        button2.setOnClickListener {
            val intent = Intent(applicationContext,LoginActivity::class.java)
            startActivity(intent)
        }
  }


    private fun encryptPassword(username:String,password: String, aesKey: SecretKey): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, aesKey)
        val encryptedBytes = cipher.doFinal(password.toByteArray(Charsets.UTF_8))
        val encryptedPasswordBase64 = android.util.Base64.encodeToString(encryptedBytes, android.util.Base64.DEFAULT)
        Log.d("Encryption", "Encrypted password is: $encryptedPasswordBase64")

        return encryptedPasswordBase64
    }


}