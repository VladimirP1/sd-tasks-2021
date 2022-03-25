package templates

import io.ktor.html.*
import kotlinx.html.FlowContent
import kotlinx.html.HTML
import kotlinx.html.body
import kotlinx.html.p

class AddProductSuccess: Template<HTML> {
    val header = Placeholder<FlowContent>()
    override fun HTML.apply() {
        body {
            p {
                +"Registration successful"
            }
        }
    }
}