package com.example.recipeapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object{
        val DATABASE_NAME = "TestCode"
        val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE recipe (recipe_id INTEGER PRIMARY KEY AUTOINCREMENT, recipe_ingredient TEXT, recipe_steps TEXT, recipe_type TEXT, recipe_image BLOB)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS recipe")
        onCreate(db)
    }

    val myDB = this.writableDatabase
    val cv = ContentValues()

    fun addRecipe(ingredient: String, steps: String, type: String): Boolean{
        cv.put("recipe_ingredient", ingredient)
        cv.put("recipe_steps", steps)
        cv.put("recipe_type", type)
//        cv.put("recipe_image", image)
        val row = myDB.insert("recipe", null, cv)

        return if(row == -1L) false else true
    }

}