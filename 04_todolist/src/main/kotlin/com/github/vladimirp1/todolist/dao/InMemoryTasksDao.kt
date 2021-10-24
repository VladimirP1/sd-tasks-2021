package com.github.vladimirp1.todolist.dao

import com.github.vladimirp1.todolist.model.Task
import com.github.vladimirp1.todolist.model.TaskList
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

class InMemoryTasksDao : TasksDao {
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    class IllegalArgumentException(what:String) : RuntimeException(what)

    private val tasksLists = mutableListOf<TaskList>()

    var currentId: Int = 0

    override fun getLists(): List<TaskList> {
        return tasksLists
    }

    override fun addTasksList(name: String) {
        tasksLists.add(TaskList(getId(), name, mutableListOf<Task>()))
    }

    override fun delTasksList(listId: Int) {
        if (tasksLists.none { listId == it.id }) throw IllegalArgumentException("No such id")
        tasksLists.removeIf { it.id == listId }
    }

    override fun addTask(listId: Int, name: String, description: String) {
        if (tasksLists.none { listId == it.id }) throw IllegalArgumentException("No such id")
        tasksLists.filter { it.id == listId }
            .forEach { (it.tasks as MutableList<Task>).add(Task(getId(), name, description, false)) }
    }

    override fun markTaskDone(id: Int) {
        var success = false
        tasksLists.forEach { it ->
            val list = (it.tasks as MutableList<Task>)
            list.forEachIndexed { index, task ->
                if (task.id == id) {
                    list[index] = Task(task.id, task.name, task.description, true)
                    success = true
                }
            }
        }
        if (!success) throw IllegalArgumentException("No such id")
    }

    override fun delTask(id: Int) {
        var success = false
        tasksLists.forEach { it ->
            success = success || (it.tasks as MutableList<Task>).removeIf { it.id == id }
        }
        if (!success) throw IllegalArgumentException("No such id")
    }

    private fun getId() = currentId++
}