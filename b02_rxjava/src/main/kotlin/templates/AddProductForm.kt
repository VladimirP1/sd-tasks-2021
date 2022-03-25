package templates

import io.ktor.html.*
import kotlinx.html.*

class AddProductForm: Template<HTML> {
    override fun HTML.apply() {
        body {
            form {
                div {
                    label {
                        htmlFor = "productName"
                        +"name:"
                    }
                    input(InputType.text) {
                        id = "productName"
                        name = "name"
                    }
                    br
                    label {
                        htmlFor = "productPrice"
                        +"price:"
                    }
                    input(InputType.text) {
                        id = "productPrice"
                        name = "price"
                    }
                }
                div {
                    button {
                        type = ButtonType.submit
                        formMethod = ButtonFormMethod.post
                        +"Add"
                    }
                }
            }
        }
    }
}