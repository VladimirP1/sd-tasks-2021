package com.github.vladimirp1.todolist.dao

import com.github.vladimirp1.todolist.model.Task
import com.github.vladimirp1.todolist.model.TaskList
import org.springframework.jdbc.core.DataClassRowMapper
import org.springframework.jdbc.core.support.JdbcDaoSupport
import javax.annotation.PostConstruct
import javax.sql.DataSource
import kotlin.streams.toList


class JdbcTasksDao(val ds: DataSource) : JdbcDaoSupport(), TasksDao {
    @PostConstruct
    private fun initialize() {
        setDataSource(ds)
    }

    override fun getLists(): List<TaskList> {
        return jdbcTemplate?.queryForStream("SELECT list_id, name from TasksLists") { rs, _ ->
            val list_id = rs.getInt("list_id")
            val list_name = rs.getString("name")
            TaskList(list_id, list_name, jdbcTemplate?.query({ it ->
                it.prepareStatement("SELECT task_id as id, name, description, done from Tasks WHERE list_id = ?")
                    .apply { setInt(1, list_id) }
            }, DataClassRowMapper<Task>(Task::class.java))!!)
        }!!.toList()
    }

    override fun addTasksList(name: String) {
        jdbcTemplate?.update("INSERT INTO TasksLists (name) VALUES (?)", name)
    }

    override fun delTasksList(listId: Int) {
        jdbcTemplate?.update(
            "START TRANSACTION;; DELETE FROM Tasks WHERE list_id = ?; DELETE FROM TasksLists WHERE list_id = ?; COMMIT;",
            listId,
            listId
        )
    }

    override fun addTask(listId: Int, name: String, description: String) {
        jdbcTemplate?.update(
            "INSERT INTO Tasks (list_id, name, description, done) VALUES (?,?,?,?)",
            listId,
            name,
            description,
            false
        )
    }

    override fun markTaskDone(id: Int) {
        jdbcTemplate?.update("UPDATE Tasks SET done = true WHERE task_id = ?", id)
    }

    override fun delTask(id: Int) {
        jdbcTemplate?.update("DELETE FROM Tasks WHERE task_id = ?", id)
    }
}