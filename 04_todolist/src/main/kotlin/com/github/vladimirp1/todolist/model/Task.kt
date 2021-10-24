package com.github.vladimirp1.todolist.model

data class Task(
    val id : Int,
    val name : String,
    val description : String,
    val done : Boolean
)