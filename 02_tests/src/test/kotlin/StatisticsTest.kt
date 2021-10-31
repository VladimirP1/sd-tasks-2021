import kotlin.test.Test
import kotlin.test.assertEquals

class StatisticsTest {
    private val timeProvider = mockedTimestamp(123456)

    private val statistics = Statistics(timeProvider)

    @Test
    fun simpleTest() {
        val kSecondsInHour: Long = 60 * 60

        val resp = object : ISearchResult {
            override val posts: List<ISearchResult.Post> = listOf(object : ISearchResult.Post {
                override val text = "#hashtag"
                override val postType = "post"
                override val timestamp: Long = timeProvider.getTimestamp()
            },
                object : ISearchResult.Post {
                    override val text = "#hashtag"
                    override val postType = "post"
                    override val timestamp: Long = timeProvider.getTimestamp() - kSecondsInHour
                },
                object : ISearchResult.Post {
                    override val text = "#hashtag"
                    override val postType = "post"
                    override val timestamp: Long = timeProvider.getTimestamp() - kSecondsInHour
                },
                object : ISearchResult.Post {
                    override val text = "#hashtag2"
                    override val postType = "post"
                    override val timestamp: Long = timeProvider.getTimestamp() - kSecondsInHour
                })
        }

        assertEquals(listOf(1, 2, 0), statistics.compute("#hashtag", resp, 3))
    }
}