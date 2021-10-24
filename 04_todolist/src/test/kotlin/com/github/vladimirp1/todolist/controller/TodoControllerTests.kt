package com.github.vladimirp1.todolist.controller

import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@WebMvcTest
@ComponentScan("com.github.vladimirp1.todolist")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TodoControllerTests(@Autowired val mockMvc: MockMvc) {

    @Test
    fun `add list`() {
        val name = "My Tasks"

        mockMvc.post("/add_tasks_list") {
            contentType = MediaType.APPLICATION_FORM_URLENCODED
            content = "name=$name"
        }.andExpect {
            status { is3xxRedirection() }
        }
    }

    @Test
    fun `add many lists`() {

        for (i in 0..1) {
            val name = "My Tasks $i"

            mockMvc.post("/add_tasks_list") {
                contentType = MediaType.APPLICATION_FORM_URLENCODED
                content = "name=$name"
            }.andExpect {
                status { is3xxRedirection() }
            }

            mockMvc.get("/") {
            }.andExpect {
                status { isOk() }
                content { string(containsString(name)) }
            }
        }
    }

    @Test
    fun `add many lists and fill them`() {
        for (i in 0..1) {
            val name = "My Tasks $i"

            mockMvc.post("/add_tasks_list") {
                contentType = MediaType.APPLICATION_FORM_URLENCODED
                content = "name=$name"
            }.andExpect {
                status { is3xxRedirection() }
            }

            mockMvc.get("/") {
            }.andExpect {
                status { isOk() }
                content { string(containsString(name)) }
            }
        }

        for (i in 0..1) {
            val name = "Task $i"
            val descr = "This is a very important task"

            mockMvc.post("/add_task") {
                contentType = MediaType.APPLICATION_FORM_URLENCODED
                content = "id=$i&name=$name&description=$descr"
            }.andExpect {
                status { is3xxRedirection() }
            }

            mockMvc.get("/") {
            }.andExpect {
                status { isOk() }
                content { string(containsString(name)) }
            }
        }
    }

    @Test
    fun `delete task list`() {
        val name = "My Tasks"

        mockMvc.post("/add_tasks_list") {
            contentType = MediaType.APPLICATION_FORM_URLENCODED
            content = "name=$name"
        }.andExpect {
            status { is3xxRedirection() }
        }

        mockMvc.post("/delete_tasks_list") {
            contentType = MediaType.APPLICATION_FORM_URLENCODED
            content = "id=0"
        }.andExpect {
            status { is3xxRedirection() }
        }

        mockMvc.get("/") {
        }.andExpect {
            status { isOk() }
            content { string(not(containsString(name))) }
        }
    }

    @Test
    fun `delete task`() {
        val name = "My Tasks"
        val task_name = "My Task 0"
        val descr = "Very important task"

        mockMvc.post("/add_tasks_list") {
            contentType = MediaType.APPLICATION_FORM_URLENCODED
            content = "name=$name"
        }.andExpect {
            status { is3xxRedirection() }
        }

        mockMvc.post("/add_task") {
            contentType = MediaType.APPLICATION_FORM_URLENCODED
            content = "id=0&name=$task_name&description=$descr"
        }.andExpect {
            status { is3xxRedirection() }
        }

        mockMvc.post("/delete_task") {
            contentType = MediaType.APPLICATION_FORM_URLENCODED
            content = "id=1"
        }.andExpect {
            status { is3xxRedirection() }
        }

        mockMvc.get("/") {
        }.andExpect {
            status { isOk() }
            content { string(not(containsString(task_name))) }
            content {string(containsString(name))}
        }
    }


    @Test
    fun `delete nonexisting list`() {
        mockMvc.post("/delete_tasks_list") {
            contentType = MediaType.APPLICATION_FORM_URLENCODED
            content = "id=1"
        }.andExpect {
            status { isInternalServerError() }
        }
    }

    @Test
    fun `delete nonexisting task`() {
        mockMvc.post("/delete_task") {
            contentType = MediaType.APPLICATION_FORM_URLENCODED
            content = "id=1"
        }.andExpect {
            status { isInternalServerError() }
        }
    }
}
