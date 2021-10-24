package com.github.vladimirp1.todolist.config

import com.github.vladimirp1.todolist.dao.JdbcTasksDao
import com.github.vladimirp1.todolist.dao.TasksDao
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource

@Configuration
@Profile("produciton")
class JdbcDaoConfiguration {
    @Bean("dataSource")
    fun dataSource() : DataSource {
        val dataSource = DriverManagerDataSource()
        dataSource.setDriverClassName("org.postgresql.Driver")
        dataSource.url = "jdbc:postgresql://localhost/todolist"
        dataSource.username = "postgres"
        dataSource.password = "12345"
        return dataSource
    }
    @Bean
    fun tasksDao() : TasksDao {
        return JdbcTasksDao(dataSource())
    }
}