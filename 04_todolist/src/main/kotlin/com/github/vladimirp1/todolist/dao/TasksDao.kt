package com.github.vladimirp1.todolist.dao

import com.github.vladimirp1.todolist.model.TaskList

interface TasksDao {
    fun getLists() : List<TaskList>

    fun addTasksList(name : String)
    fun delTasksList(listId : Int)

    fun addTask(listId : Int, name : String, description : String)
    fun markTaskDone(id : Int)
    fun delTask(id : Int)
}