import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class VkParserTest {
    private val parser = VkParser()
    private val response = VkParserTest::class.java.getResource("search_response.json")!!.readText()

    @Test
    fun testEmpty() {
        assertTrue { parser.parseResponse("{\"response\":{}}").posts.isEmpty() }
    }

    @Test
    fun testRealData() {
        val parsed = parser.parseResponse(response)
        assertEquals(1, parsed.posts.size)
        assertContains(parsed.posts[0].text, "DJI V2 FPV")
    }
}