import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun withMockServer(resourcePath : String, code: (endpoint: String) -> Unit) {
    val port = 8888
    val address = "127.0.0.1"
    val path = "/newsfeed.search"
    embeddedServer(Netty, port = port, host = address) {
        routing {
            get(path) {
                call.respondText(object {}.javaClass.getResource(resourcePath)!!.readText())
            }
        }
    }.start().also { code("http://$address:$port$path") }.stop(100, 200)
}

fun mockedTimestamp(time : Long) = object : ITimestampProvider {
    override fun getTimestamp(): Long = time
}