package com.example.recipeapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import java.util.*


class myRecipeEditDetails : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var firebaseDB : FirebaseFirestore

    private var imageUri: Uri? = null
    private val pickImage = 100

    private lateinit var ivRecipeImage_edit : ImageView
    private lateinit var etingredient_edit : TextView
    private lateinit var etstep_edit : TextView
    private lateinit var spinnerRecipeType_edit: Spinner
    private lateinit var btnsave_edit : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_recipe_edit_details)

        auth = FirebaseAuth.getInstance()
        firebaseDB = FirebaseFirestore.getInstance()

        etingredient_edit = findViewById(R.id.etingredient_edit)
        etstep_edit = findViewById(R.id.etstep_edit)
        btnsave_edit = findViewById(R.id.btnsave_edit)
        ivRecipeImage_edit = findViewById(R.id.ivRecipeImage_edit)
        spinnerRecipeType_edit = findViewById(R.id.spinnerRecipeType_edit)

        ArrayAdapter.createFromResource(
            this,
            R.array.recipetype,
            android.R.layout.simple_spinner_item
        ).also { adapter ->

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spinnerRecipeType_edit.adapter = adapter
        }

        ivRecipeImage_edit.setOnClickListener {
            openGallery()
        }

        loadRecipeData()

        btnsave_edit.setOnClickListener {
            updateRecipe()
        }


    }

    fun openGallery(){
        val gallery =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, pickImage)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            imageUri = data?.data!!

            if(imageUri != null){
                ivRecipeImage_edit.setImageURI(imageUri)
            }
        }
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

                ArrayAdapter.createFromResource(
                    this,
                    R.array.recipetype,
                    android.R.layout.simple_spinner_item
                ).also { adapter ->

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    spinnerRecipeType_edit.adapter = adapter

                }

                etingredient_edit.text = recipe_ingredient
                etstep_edit.text = recipe_step

                Glide.with(this)
                    .load(myrecipeimage)
                    .into(ivRecipeImage_edit)
            }
    }

    private fun updateRecipe(){
        // Get Intent Data
        val myrecipeid = intent.getStringExtra("myRecipeID")
        val myrecipeimage = intent.getStringExtra("recipeImage")

        val ingredient = etingredient_edit.getText().toString().trim()
        val step = etstep_edit.getText().toString().trim()
        val recipetype = spinnerRecipeType_edit.selectedItem.toString().trim()

        if(ingredient.isEmpty() || step.isEmpty()){
            Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_LONG).show()
        }
        else{
            if(imageUri == null){
                val currentUser = auth.currentUser
                val id = currentUser?.uid

                val updRecipeData = hashMapOf(
                    "recipe_ingredient" to ingredient,
                    "recipe_steps" to step,
                    "recipe_type" to recipetype,
                    "recipe_image" to myrecipeimage,
                    "recipe_id" to myrecipeid,
                    "user_id" to id
                )

                firebaseDB.collection("recipe").document(myrecipeid.toString())
                    .set(updRecipeData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Update successful", Toast.LENGTH_LONG).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Log.w(ContentValues.TAG, "Error adding document", e)
                    }

            }
            else{
                val progressDialog = ProgressDialog(this)
                progressDialog.setMessage("Uploading File...")
                progressDialog.setCancelable(false)
                progressDialog.show()

                val filename = UUID.randomUUID().toString()
                val storageRef = FirebaseStorage.getInstance().getReference("eImage/$filename.jpg")

                storageRef.putFile(imageUri!!)
                    .addOnSuccessListener {
                        if (progressDialog.isShowing) progressDialog.dismiss()
                        storageRef.downloadUrl.addOnSuccessListener {
                            val imageUri = it.toString()
                            val currentUser = auth.currentUser
                            val id = currentUser?.uid

                            val updRecipeData = hashMapOf(
                                "recipe_ingredient" to ingredient,
                                "recipe_steps" to step,
                                "recipe_type" to recipetype,
                                "recipe_image" to imageUri,
                                "recipe_id" to myrecipeid,
                                "user_id" to id
                            )

                            firebaseDB.collection("recipe").document(myrecipeid.toString())
                                .set(updRecipeData)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Update successful", Toast.LENGTH_LONG).show()
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Log.w(ContentValues.TAG, "Error adding document", e)
                                }

                        }
                            .addOnFailureListener { e ->
                                Log.w(ContentValues.TAG, "Error adding document", e)
                            }


                    }
                    .addOnFailureListener { e ->
                        Log.w(ContentValues.TAG, "Error adding document", e)
                    }
            }
        }
    }


}