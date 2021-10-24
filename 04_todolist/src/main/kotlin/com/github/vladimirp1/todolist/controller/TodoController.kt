package com.github.vladimirp1.todolist.controller

import com.github.vladimirp1.todolist.dao.TasksDao
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class TodoController( val dao: TasksDao) {
    @GetMapping("")
    fun index(m: Model): String {
        m.addAttribute("tasks_lists", dao.getLists())
        return "index"
    }

    @PostMapping("add_task")
    fun addTask(@RequestParam id: Int, @RequestParam name: String, @RequestParam description: String): String {
        dao.addTask(id, name, description)
        return "redirect:/"
    }

    @PostMapping("add_tasks_list")
    fun addList(@RequestParam name: String): String {
        dao.addTasksList(name)
        return "redirect:/"
    }

    @PostMapping("mark_done")
    fun markDone(@RequestParam id: Int): String {
        dao.markTaskDone(id)
        return "redirect:/"
    }

    @PostMapping("delete_tasks_list")
    fun deleteTasksList(id: Int): String {
        dao.delTasksList(id)
        return "redirect:/"
    }

    @PostMapping("delete_task")
    fun deleteTask(id: Int): String {
        dao.delTask(id)
        return "redirect:/"
    }
}