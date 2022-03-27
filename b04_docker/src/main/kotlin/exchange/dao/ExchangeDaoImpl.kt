package exchange.dao

import exchange.model.Shares
import java.util.concurrent.ConcurrentHashMap

class ExchangeDaoImpl : ExchangeDao {
    private val shares = ConcurrentHashMap<String, Shares>()
    override fun sellShares(company: String, count: Long): Long {
        require(count > 0)
        var cost: Long = 0
        requireNotNull(shares.computeIfPresent(company) { _: String, s: Shares ->
            cost = s.price * count
            s.copy(count = s.count + count)
        })
        return cost
    }

    override fun buyShares(company: String, count: Long): Long {
        require(count > 0)
        var cost: Long = 0
        requireNotNull(shares.computeIfPresent(company) { _: String, s: Shares ->
            cost = s.price * count
            require(s.count - count > 0)
            s.copy(count = s.count - count)
        })
        return cost
    }

    override fun getOffer(company: String): Shares? {
        return shares[company]
    }

    override fun newCompany(company: String, shares: Shares) {
        require(this.shares.putIfAbsent(company, shares) == null)
    }


    override fun assignPrice(company: String, price: Long) {
        require(price > 0)
        requireNotNull(shares.computeIfPresent(company) { _: String, s: Shares ->
            s.copy(price = price)
        })
    }
}