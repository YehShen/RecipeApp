package com.example.recipeapp

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    private lateinit var etemail : EditText
    private lateinit var etpassword : EditText
    private lateinit var btnlogin : Button
    private lateinit var signup: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etemail = findViewById(R.id.etemail)
        etpassword = findViewById(R.id.etpassword)
        btnlogin = findViewById(R.id.btnlogin)
        signup = findViewById(R.id.signup)

        signup.setOnClickListener {
            val intent = Intent(this@Login, Register::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }


        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser

        if(currentUser != null){
            startActivity(Intent(this@Login, Main::class.java))
            finish()
        }

        login()
    }

    private fun login(){
        btnlogin.setOnClickListener{
            if(TextUtils.isEmpty(etemail.text.toString()) || TextUtils.isEmpty(etpassword.text.toString())){
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_LONG).show()
            }else{
                auth.signInWithEmailAndPassword(etemail.text.toString(), etpassword.text.toString())
                    .addOnCompleteListener {
                        if(it.isSuccessful){
                            startActivity(Intent(this@Login, Main::class.java))
                            finish()
                        }else{
                            Toast.makeText(this, "Please try again", Toast.LENGTH_LONG).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.w(ContentValues.TAG,"Error adding document",e)
                    }
            }
        }
    }


}