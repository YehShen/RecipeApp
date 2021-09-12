package com.example.recipeapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class Add_Recipe : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    var firebaseDB = Firebase.firestore

    private lateinit var etingredient : EditText
    private lateinit var etstep : EditText
    private lateinit var btnsave : Button
    private lateinit var ivRecipeImage: ImageView
    private lateinit var spinnerRecipeType: Spinner

    private var imageUri: Uri? = null
    private val pickImage = 100

    var databaseReference: DatabaseReference? = null
    var database: FirebaseDatabase? = null



    // SQLite
//    companion object{
//        lateinit var dbHelper: DBHelper
//    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recipe)

//        SQLite
//        dbHelper = DBHelper(this)

        etingredient = findViewById(R.id.etingredient)
        etstep = findViewById(R.id.etstep)
        btnsave = findViewById(R.id.btnsave)
        ivRecipeImage = findViewById(R.id.ivRecipeImage)
        spinnerRecipeType = findViewById(R.id.spinnerRecipeType)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("recipe")

        println("Another place " + auth)

        ArrayAdapter.createFromResource(
            this,
            R.array.recipetype,
            android.R.layout.simple_spinner_item
        ).also { adapter ->

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spinnerRecipeType.adapter = adapter
        }

        ivRecipeImage.setOnClickListener {
            openGallery()
        }

        btnsave.setOnClickListener {
            createRecipe()
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
                ivRecipeImage.setImageURI(imageUri)
            }
        }
    }

//    Firebase
    private fun createRecipe(){
        val ingredient = etingredient.getText().toString().trim()
        val step = etstep.getText().toString().trim()
        val recipetype = spinnerRecipeType.selectedItem.toString().trim()

        if(ingredient.isEmpty() || step.isEmpty()){
            Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_LONG).show()
        }
        else if(imageUri == null){
            Toast.makeText(this, "Please insert image", Toast.LENGTH_LONG).show()
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

                        var newID: Int = 0
                        firebaseDB.collection("recipe")
                            .get()
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    for (document in it.result!!) {
                                        val id = document.id.replace("r", "")
                                        val idInt = id.toInt()

                                        if (idInt >= newID) {
                                            newID = idInt
                                        }
                                    }

                                    newID += 1

                                    val output: String = String.format("r%03d", newID)
                                    val currentUser = auth.currentUser
                                    val id = currentUser?.uid

                                    val recipe = hashMapOf(
                                        "recipe_ingredient" to ingredient,
                                        "recipe_steps" to step,
                                        "recipe_type" to recipetype,
                                        "recipe_image" to imageUri,
                                        "recipe_id" to output,
                                        "user_id" to id
                                    )

                                    firebaseDB.collection("recipe").document(output)
                                        .set(recipe)
                                        .addOnSuccessListener {
                                            Toast.makeText(this, "Insert successful", Toast.LENGTH_LONG)
                                                .show()
                                            startActivity(Intent(this@Add_Recipe, Main::class.java))
                                            finish()
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w(ContentValues.TAG, "Error adding document", e)
                                        }
                                }

                            }
                            .addOnFailureListener { e ->
                                Log.w(ContentValues.TAG, "Error adding document", e)
                            }
                    }

                }

        }
    }

    // Create Recipe SQLite
//    private fun createRecipe(){
//        val ingredient = etingredient.getText().toString().trim()
//        val step = etstep.getText().toString().trim()
//        val recipetype = spinnerRecipeType.selectedItem.toString().trim()
//
//        if(ingredient.isEmpty() || step.isEmpty()){
//            Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_LONG).show()
//        }
//        else if(imageUri == null){
//            Toast.makeText(this, "Please insert image", Toast.LENGTH_LONG).show()
//        }
//        else{
//            val checkdata: Boolean = dbHelper.addRecipe(ingredient, step, recipetype)
//
//            if (checkdata){
//                Toast.makeText(this@Add_Recipe, "Insert Successfully", Toast.LENGTH_LONG).show()
//                etingredient.setText("")
//                etstep.setText("")
//                startActivity(Intent(this@Add_Recipe, Main::class.java))
//            }
//            else{
//                Toast.makeText(this@Add_Recipe, "Fail to insert", Toast.LENGTH_LONG).show()
//            }
//
//        }
//    }
}