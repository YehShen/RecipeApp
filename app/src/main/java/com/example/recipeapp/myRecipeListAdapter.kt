package com.example.recipeapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class myRecipeListAdapter(private val recipeList: ArrayList<RecipeListModel>, private val context: Context) : RecyclerView.Adapter<myRecipeListAdapter.myView>() {
    var firebaseDB = Firebase.firestore
    lateinit var auth: FirebaseAuth

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myRecipeListAdapter.myView {
        return myView(
            LayoutInflater.from(context).inflate(R.layout.receipe_list_layout, parent, false)
        )

    }

    override fun onBindViewHolder(holder: myRecipeListAdapter.myView, position: Int) {
        auth = FirebaseAuth.getInstance()
        firebaseDB = FirebaseFirestore.getInstance()
        val currentUser = auth.currentUser

        val recipe : RecipeListModel = recipeList[position]
        holder.tvtype.text = recipe.recipe_type

        Glide.with(context)
            .load(recipe.recipe_image)
            .into(holder.ivreceipepic)

        holder.tvview.setOnClickListener{
            val intent = Intent(context, myRecipeDetails::class.java)
            intent.putExtra("myRecipeID",recipe.recipe_id.toString())
            intent.putExtra("recipeImage",recipe.recipe_image.toString())
            intent.putExtra("userid", recipe.user_id.toString())
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent)
        }

        holder.receipe_list.setOnClickListener{
            val intent = Intent(context, myRecipeDetails::class.java)
            intent.putExtra("myRecipeID",recipe.recipe_id.toString())
            intent.putExtra("recipeImage",recipe.recipe_image.toString())
            intent.putExtra("userid", recipe.user_id.toString())
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }

    public class myView(itemView: View) : RecyclerView.ViewHolder(itemView){
        val ivreceipepic: ImageView = itemView.findViewById(R.id.ivreceipepic)
        val tvtype: TextView = itemView.findViewById(R.id.tvtype)
        val tvview: TextView = itemView.findViewById(R.id.tvview)
        val receipe_list: ConstraintLayout = itemView.findViewById(R.id.receipe_list)

    }

}

