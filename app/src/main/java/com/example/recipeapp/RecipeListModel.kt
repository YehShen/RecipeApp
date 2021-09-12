package com.example.recipeapp

data class RecipeListModel(
    var recipe_type: String ?= null,
    var recipe_image: String ?= null,
    var recipe_id : String ?= null,
    var user_id : String ?= null
)
