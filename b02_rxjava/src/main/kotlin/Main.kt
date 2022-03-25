import com.mongodb.reactivestreams.client.MongoClients
import io.ktor.application.*
import io.ktor.html.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.bson.Document
import kotlinx.coroutines.reactive.collect
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.br
import templates.AddProductForm
import templates.RegistrationForm
import templates.RegistrationSuccess

suspend fun t_db() {
    val url = "mongodb://127.0.0.1:27017"
    val db = MongoClients.create(url).getDatabase("testdb")
    db.getCollection("a").insertOne(Document(mapOf("x" to "x"))).collect { }
    val find_a = db.getCollection("a").find()
    find_a.collect { println(it) }
}

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondHtml {
                body {
                    a("/register") { +"Register" }
                    br
                    a("/add-product") { +"Add a product" }
                    br
                    a("/products") { +"List products" }
                }
            }
        }
        get("/register") {
            call.respondHtmlTemplate(RegistrationForm()) {}
        }
        post("/register") {
            call.respondHtmlTemplate(RegistrationSuccess()) {}
        }
        get("/add-product") {
            call.respondHtmlTemplate(AddProductForm()) {}
        }
        post("/add-product") {

        }
    }
}

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureRouting()
    }.start(wait = true)
}
//=
//} io.ktor.server.netty.EngineMain.main(args)


//
//fun main() {
//    runBlocking {
//        t_db()
//    }
//}

