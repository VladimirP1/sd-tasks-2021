import java.io.File

class HashtagManager(
    val client: VkClient,
    val timestampProvider: ITimestampProvider = TimestampProviderImpl()
) {
    fun getStats(query: String, durationHours: Int): List<Int> {
        val parser = VkParser()
        val stats = Statistics(timestampProvider)
        return stats.Compute(query, parser.parseResponse(client.searchFeed(query)), durationHours)
    }
}