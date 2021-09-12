package com.example.recipeapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import java.util.ArrayList

class Main : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var firebaseDB : FirebaseFirestore

    private lateinit var btnadd: FloatingActionButton
    private lateinit var iv_user: ImageView
    private lateinit var spinnerRecipeType: Spinner
    private lateinit var rvlist: RecyclerView
    private lateinit var recipeList: ArrayList<RecipeListModel>
    private lateinit var recipeAdapter: myRecipeListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        iv_user = findViewById(R.id.iv_user)
        btnadd = findViewById(R.id.btnadd)
        spinnerRecipeType = findViewById(R.id.spinnerRecipeType)
        rvlist = findViewById(R.id.rvlist)

        auth = FirebaseAuth.getInstance()
        firebaseDB = FirebaseFirestore.getInstance()



        btnadd.setOnClickListener {
            startActivity(Intent(this@Main, Add_Recipe::class.java))

        }

        iv_user.setOnClickListener {
            startActivity(Intent(this@Main, myRecipeList::class.java))

        }

        ArrayAdapter.createFromResource(
            this,
            R.array.recipetype,
            android.R.layout.simple_spinner_item
        ).also { adapter ->

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spinnerRecipeType.adapter = adapter
        }

        rvlist.layoutManager = LinearLayoutManager(this)
        rvlist.setHasFixedSize(true)
        recipeList = arrayListOf()
        recipeAdapter = myRecipeListAdapter(recipeList, this.baseContext)
        rvlist.adapter = recipeAdapter


        RecipeList()


//        spinnerRecipeType.setOnItemSelectedListener(this)



    }

    private fun RecipeList(){
        firebaseDB.collection("recipe")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if(error != null){
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }
                    for(dc : DocumentChange in value?.documentChanges!!){
                        if(dc.type == DocumentChange.Type.ADDED){
                            val recipeItem: RecipeListModel = dc.document.toObject(RecipeListModel::class.java)
                            recipeList.add(recipeItem)
                        }
                    }
                    recipeAdapter.notifyDataSetChanged()
                }

            })

    }

//    private fun filterType(){
//
//
//    }

//    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//        val spinnerData: String = spinnerRecipeType.selectedItem.toString().trim()
//
//        firebaseDB.collection("recipe").whereEqualTo("recipe_type", spinnerData)
//            .addSnapshotListener(object : EventListener<QuerySnapshot> {
//                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
//                    if(error != null){
//                        Log.e("Firestore Error", error.message.toString())
//                        return
//                    }
//                    for(dc : DocumentChange in value?.documentChanges!!){
//                        if(dc.type == DocumentChange.Type.ADDED){
//                            val recipeItem: RecipeListModel = dc.document.toObject(RecipeListModel::class.java)
//                            recipeList.add(recipeItem)
//                        }
//                    }
//
//                    recipeAdapter.notifyDataSetChanged()
//
//                }
//            })
//
//
//    }

//    override fun onNothingSelected(parent: AdapterView<*>?) {
//        TODO("Not yet implemented")
//    }
}