package templates

import io.ktor.html.*
import kotlinx.html.HTML
import kotlinx.html.body
import kotlinx.html.p

class RegistrationSuccess: Template<HTML> {
    override fun HTML.apply() {
        body {
            p{
                + "Registration successful"
            }
        }
    }
}