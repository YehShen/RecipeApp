package com.example.recipeapp

import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.view.View


class myRecipeDetails : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var firebaseDB : FirebaseFirestore

    private lateinit var ivreceipepic_my_data: ImageView
    private lateinit var tvingredient_my_data: TextView
    private lateinit var tvstep_my_data: TextView
    private lateinit var tvtype_my_data: TextView
    private lateinit var btnEdit: Button
    private lateinit var btnDelete: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_recipe_details)

        auth = FirebaseAuth.getInstance()
        firebaseDB = FirebaseFirestore.getInstance()

        val currentUser = auth.currentUser
        val id = currentUser?.uid

        // Get Intent Data
        val myrecipeid = intent.getStringExtra("myRecipeID")
        val myrecipeimage = intent.getStringExtra("recipeImage")
        val userid = intent.getStringExtra("userid")

        ivreceipepic_my_data = findViewById(R.id.ivreceipepic_my_data)
        tvingredient_my_data = findViewById(R.id.tvingredient_my_data)
        tvstep_my_data = findViewById(R.id.tvstep_my_data)
        tvtype_my_data = findViewById(R.id.tvtype_my_data)
        btnEdit = findViewById(R.id.btnEdit)
        btnDelete = findViewById(R.id.btnDelete)

        tvingredient_my_data.setMovementMethod(ScrollingMovementMethod())
        tvstep_my_data.setMovementMethod(ScrollingMovementMethod())

        if(userid != id){
            btnEdit.setVisibility(View.INVISIBLE)
            btnDelete.setVisibility(View.INVISIBLE)
        }
        else{
            btnEdit.setVisibility(View.VISIBLE)
            btnDelete.setVisibility(View.VISIBLE)
        }


        btnDelete.setOnClickListener {
            deleteRecipeData()
        }

        btnEdit.setOnClickListener {
            val intent = Intent(this@myRecipeDetails, myRecipeEditDetails::class.java)
            intent.putExtra("myRecipeID",myrecipeid)
            intent.putExtra("recipeImage",myrecipeimage)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent)
        }

        loadRecipeData()

    }

    fun loadRecipeData(){
        // Get Intent Data
        val myrecipeid = intent.getStringExtra("myRecipeID")
        val myrecipeimage = intent.getStringExtra("recipeImage")

        firebaseDB.collection("recipe").document(myrecipeid.toString())
            .get()
            .addOnSuccessListener {
                val recipe_ingredient : String = it["recipe_ingredient"].toString()
                val recipe_step : String = it["recipe_steps"].toString()
                val recipe_type : String = it["recipe_type"].toString()

                tvingredient_my_data.text = recipe_ingredient
                tvstep_my_data.text = recipe_step
                tvtype_my_data.text = recipe_type

                Glide.with(this)
                    .load(myrecipeimage)
                    .into(ivreceipepic_my_data)
            }
    }

    fun deleteRecipeData(){
        // Get Intent Data
        val myrecipeid = intent.getStringExtra("myRecipeID")

        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Warning")
            .setMessage("Are you sure to delete")
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                firebaseDB.collection("recipe").document(myrecipeid.toString())
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Successfully Deleted", Toast.LENGTH_LONG).show()
                        finish()
                        startActivity(Intent(this@myRecipeDetails, myRecipeList::class.java))

                    }
                    .addOnFailureListener{
                        Log.w(ContentValues.TAG, "Error adding document", it)
                    }
            })
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->  })
            .show()
    }
}