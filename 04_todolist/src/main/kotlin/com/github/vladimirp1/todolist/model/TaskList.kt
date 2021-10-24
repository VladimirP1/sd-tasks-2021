package com.github.vladimirp1.todolist.model

data class TaskList (
    val id : Int,
    val name : String,
    val tasks : List<Task>
)