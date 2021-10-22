import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class VkParser {
    @Serializable
    data class ItemImpl(
        override val text: String = "",
        @SerialName("post_type")
        override val postType: String = "",
        @SerialName("date")
        override val timestamp: Long = 0
    ) : ISearchResult.Post

    @Serializable
    data class SearchResponseImpl(
        @SerialName("items") override val posts: List<ItemImpl> = emptyList()
    ) : ISearchResult

    @Serializable
    data class ResponseContent(val response: SearchResponseImpl)

    fun parseResponse(resp: String): ISearchResult {
        return Json { ignoreUnknownKeys = true }.decodeFromString<ResponseContent>(resp).response
    }
}