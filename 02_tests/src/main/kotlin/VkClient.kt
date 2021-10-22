import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class VkClient(
    private val kServiceToken: String,
    private val endpoint: String = "https://api.vk.com/method/newsfeed.search",
) {
    private val kDefaultCount = 200
    private val kApiVersion = "5.81"

    fun searchFeed(query: String): String {
        val encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString())
        val urlString =
            "$endpoint?q=$encodedQuery&count=$kDefaultCount&start_time=0&access_token=$kServiceToken&v=$kApiVersion"
        return readUrl(URL(urlString))
    }

    private fun readUrl(url: URL): String {
        return url.openStream().readBytes().decodeToString()
    }
}
