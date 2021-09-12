package com.example.recipeapp

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.lang.ref.PhantomReference

class Register : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    private lateinit var btnregister: Button
    private lateinit var etfirstname: EditText
    private lateinit var etlastname: EditText
    private lateinit var etemail: EditText
    private lateinit var etpassword: EditText

    var databaseReference: DatabaseReference? = null
    var database: FirebaseDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etfirstname = findViewById(R.id.etfirstname)
        etlastname = findViewById(R.id.etlastname)
        etemail = findViewById(R.id.etemail)
        etpassword = findViewById(R.id.etpassword)
        btnregister = findViewById(R.id.btnregister)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("user")

        register()
    }

    private fun register(){
        btnregister.setOnClickListener {
            if(TextUtils.isEmpty(etfirstname.text.toString()) ||
                TextUtils.isEmpty(etlastname.text.toString()) ||
                TextUtils.isEmpty(etemail.text.toString())){
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_LONG).show()
            }
            else if(TextUtils.isEmpty(etpassword.text.toString()) || etpassword.text.toString().length < 6){
                Toast.makeText(this, "Password must 6 character", Toast.LENGTH_LONG).show()
            }
            else{
                auth.createUserWithEmailAndPassword(etemail.text.toString(), etpassword.text.toString())
                    .addOnCompleteListener {
                            if(it.isSuccessful){
                                val currentUser = auth.currentUser
                                val currentUserDB = databaseReference?.child(currentUser?.uid!!)
                                currentUserDB?.child("first_name")?.setValue(etfirstname.text.toString())
                                currentUserDB?.child("last_name")?.setValue(etlastname.text.toString())
                                Toast.makeText(this, "Register successful", Toast.LENGTH_LONG).show()
                                startActivity(Intent(this@Register, Login::class.java))
                                finish()

                            }else{
                                Toast.makeText(this, "Fail to register", Toast.LENGTH_LONG).show()
                            }

                    }
                    .addOnFailureListener { e ->
                        Log.w(ContentValues.TAG,"Error adding document",e)
                        Toast.makeText(this, "REGISTERED FAILED.",Toast.LENGTH_LONG).show()
                    }
            }
        }
    }
}