import exchange.model.Shares
import lk.client.ExchangeClient
import lk.dao.LkDao
import lk.dao.LkDaoImpl
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
class TestLk {
    @Container
    var container: KGenericContainer = KGenericContainer("sd/b04_docker:latest").withExposedPorts(8080)

    @Test
    fun `Test valid scenario`() {
        val exchange = ExchangeClient("127.0.0.1", container.firstMappedPort)
        exchange.newCompany("google", Shares(1000, 100))
        exchange.newCompany("apple", Shares(1200, 110))

        val lk : LkDao = LkDaoImpl(exchange)

        val uid1 = lk.addUser("user")
        val uid2 = lk.addUser("user")

        lk.deposit(uid1, 1000)
        lk.deposit(uid2, 120)

        Assertions.assertEquals(1000, lk.balance(uid1))
        Assertions.assertEquals(120, lk.balance(uid2))

        lk.buyShares(uid2, "apple", 1)
        exchange.assignPrice("apple", 1100)

        Assertions.assertEquals(1110, lk.fullBalance(uid2))
        Assertions.assertEquals(10, lk.balance(uid2))

        lk.sellShares(uid2, "apple", 1)

        Assertions.assertEquals(1110, lk.balance(uid2))
    }
}