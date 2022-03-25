package templates

import io.ktor.html.*
import kotlinx.html.*

class RegistrationForm : Template<HTML> {
    override fun HTML.apply() {
        body {
            form {
                p { + "Currency:" }
                div {
                    input(InputType.radio) {
                        name = "currency"
                        id = "currencyRub"
                        value = "rub"
                    }
                    label {
                        htmlFor = "currencyRub"
                        +"RUB"
                    }
                    input(InputType.radio) {
                        name = "currency"
                        id = "currencyUsd"
                        value = "usd"
                    }
                    label {
                        htmlFor = "currencyUsd"
                        +"USD"
                    }
                }
                div {
                    label {
                        htmlFor = "username"
                        +"user name:"
                    }
                    input(InputType.text) {
                        id = "username"
                        name = "username"
                    }
                }
                div {
                    button {
                        type = ButtonType.submit
                        formMethod = ButtonFormMethod.post
                        + "Register"
                    }
                }
            }
        }
    }
}