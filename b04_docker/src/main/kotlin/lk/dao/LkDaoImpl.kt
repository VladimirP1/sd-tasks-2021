package lk.dao

import lk.client.ExchangeClient
import lk.model.CompanyShares
import lk.model.User
import java.util.concurrent.ConcurrentHashMap


class LkDaoImpl(val exchange: ExchangeClient) : LkDao {
    override fun addUser(name: String): Long {
        require(!users.contains(name))
        require(users.putIfAbsent(lastFreeId, User(name, 0, mapOf())) == null)
        return lastFreeId++
    }

    override fun deposit(id: Long, amount: Long) {
        require(amount >= 0)
        requireNotNull(users.computeIfPresent(id) { _, v -> v.copy(balance = v.balance + amount) })
    }

    override fun usersShares(id: Long): List<CompanyShares> {
        val shares = mutableListOf<CompanyShares>()
        val user = users[id]!!
        for (item in user.shares) {
            shares.add(CompanyShares(item.key, item.value, exchange.getOffer(item.key)!!.price))
        }
        return shares
    }

    override fun fullBalance(id: Long): Long {
        val user = users[id]!!
        var fullBalance: Long = 0
        fullBalance = user.balance
        for (item in user.shares) {
            fullBalance += item.value * exchange.getOffer(item.key)!!.price
        }
        return fullBalance
    }

    override fun balance(id: Long): Long {
        return users[id]!!.balance
    }

    override fun buyShares(id: Long, company: String, count: Long) {
        val user = users[id]!!
        val change = exchange.buyShares(company, count)
        users.computeIfPresent(id) { _, v ->
            val mShares = v.shares.toMutableMap()
            mShares.compute(company) { _, v -> (v ?: 0) + count }
            v.copy(balance = v.balance - change, shares = mShares)
        }
    }

    override fun sellShares(id: Long, company: String, count: Long) {
        val user = users[id]!!
        users.computeIfPresent(id) { _, v ->
            require(((v.shares[company] ?: 0L) - count) >= 0)
            val mShares = v.shares.toMutableMap()
            mShares.compute(company) { _, v -> (v ?: 0) - count }
            v.copy(shares = mShares)
        }
        var change: Long = 0
        try {
            change = exchange.sellShares(company, count)
            users.computeIfPresent(id) { _, v ->
                v.copy(balance = v.balance + change)
            }
        } catch (e: java.lang.Exception) {
            users.computeIfPresent(id) { _, v ->
                val mShares = v.shares.toMutableMap()
                mShares.compute(company) { _, v -> (v ?: 0) + count }
                v.copy(shares = mShares)
            }
        }
    }


    private val users = ConcurrentHashMap<Long, User>()
    private var lastFreeId: Long = 0
}