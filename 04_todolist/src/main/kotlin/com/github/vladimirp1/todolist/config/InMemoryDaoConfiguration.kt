package com.github.vladimirp1.todolist.config

import com.github.vladimirp1.todolist.dao.InMemoryTasksDao
import com.github.vladimirp1.todolist.dao.TasksDao
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("testing")
class InMemoryDaoConfiguration {
    @Bean
    fun tasksDao() : TasksDao {
        return InMemoryTasksDao()
    }
}