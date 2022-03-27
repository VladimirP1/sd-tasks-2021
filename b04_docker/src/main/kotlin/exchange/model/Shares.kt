package exchange.model
import kotlinx.serialization.*

@Serializable
data class Shares(val count: Long, val price: Long) {
    init {
        require(count >= 0)
        require(price > 0)
    }
}