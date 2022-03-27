import exchange.model.Shares
import lk.client.ExchangeClient
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers


@Testcontainers
class TestExchange {
    @Container
    var container: KGenericContainer = KGenericContainer("sd/b04_docker:latest").withExposedPorts(8080)

    @Test
    fun `Test valid scenario`() {
        val exchange = ExchangeClient("127.0.0.1", container.firstMappedPort)

        exchange.newCompany("google", Shares(1000, 10))
        Assertions.assertEquals(Shares(1000, 10), exchange.getOffer("google"))

        exchange.assignPrice("google", 12)
        Assertions.assertEquals(Shares(1000, 12), exchange.getOffer("google"))

        exchange.buyShares("google", 10)
        Assertions.assertEquals(Shares(990, 12), exchange.getOffer("google"))

        exchange.sellShares("google", 10)
        Assertions.assertEquals(Shares(1000, 12), exchange.getOffer("google"))
    }

    @Test
    fun `Test case when company does not exist`() {
        val exchange = ExchangeClient("127.0.0.1", container.firstMappedPort)

        Assertions.assertThrows(Exception::class.java) {
            exchange.sellShares("apple", 10)
        }

        Assertions.assertThrows(Exception::class.java) {
            exchange.buyShares("apple", 10)
        }

        Assertions.assertThrows(Exception::class.java) {
            exchange.assignPrice("apple", 12)
        }
    }

    @Test
    fun `Test case when trying to create the same company more than once`() {
        val exchange = ExchangeClient("127.0.0.1", container.firstMappedPort)

        exchange.newCompany("apple", Shares(1000, 10))

        Assertions.assertThrows(Exception::class.java) {
            exchange.newCompany("apple", Shares(1000, 10))
        }
    }
}