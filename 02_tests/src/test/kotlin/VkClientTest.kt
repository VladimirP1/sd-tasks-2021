import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlin.test.Test
import kotlin.test.assertContains

class VkClientTest {
    @Test
    fun simpleTest() {
        withMockServer("search_response.json") {
            assertContains(VkClient("FAKE_SERVICE_TOKEN", it).searchFeed("DJI FPV"), "DJI V2 FPV")
        }
    }
}