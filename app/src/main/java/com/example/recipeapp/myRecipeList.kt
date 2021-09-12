package com.example.recipeapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import java.util.*

class myRecipeList : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var firebaseDB : FirebaseFirestore
    private lateinit var btnsignout: Button
    private lateinit var rvrecipeuser: RecyclerView
    private lateinit var myRecipeList: ArrayList<RecipeListModel>
    private lateinit var myRecipeAdapter: myRecipeListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        auth = FirebaseAuth.getInstance()
        btnsignout = findViewById(R.id.btnsignout)
        rvrecipeuser = findViewById(R.id.rvrecipeuser)

        btnsignout.setOnClickListener {
            auth.signOut()
//            startActivity(Intent(this@myRecipeList, Login::class.java))
            val intent = Intent(this@myRecipeList, Login::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }

        rvrecipeuser.layoutManager = LinearLayoutManager(this)
        rvrecipeuser.setHasFixedSize(true)
        myRecipeList = arrayListOf()
        myRecipeAdapter = myRecipeListAdapter(myRecipeList, this.baseContext)
        rvrecipeuser.adapter = myRecipeAdapter
        myRecipeList()

    }

    private fun myRecipeList(){
        val currentUser = auth.currentUser
        val uid = currentUser?.uid!!
        firebaseDB = FirebaseFirestore.getInstance()
        firebaseDB.collection("recipe")
            .whereEqualTo("user_id", uid.toString())
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if(error != null){
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }
                    for(dc : DocumentChange in value?.documentChanges!!){
                        if(dc.type == DocumentChange.Type.ADDED){
                            val recipeItem: RecipeListModel = dc.document.toObject(RecipeListModel::class.java)
                            myRecipeList.add(recipeItem)
                        }
                    }
                    myRecipeAdapter.notifyDataSetChanged()
                }

        })

    }
}