import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class HashtagManagerTest {
    @Test
    fun simpleTest() {
        withMockServer("search_response_autumn.json") {
            val manager = HashtagManager(VkClient("FAKE_SERVICE_TOKEN", it), mockedTimestamp(1634923975))

            assertEquals(
                listOf(7, 9, 3, 9, 11, 13, 8, 5, 6, 4, 12, 10, 7, 4, 2, 2, 2, 1, 3, 2, 3, 8, 7, 5),
                manager.getStats("#autumn", 24)
            )
        }
    }
}