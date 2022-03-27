package lk.client

import exchange.dao.ExchangeDao
import exchange.model.Shares
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import reactor.netty.http.client.HttpClient
import java.net.InetSocketAddress
import java.time.Duration

class ExchangeClient(private val host: String, private val port: Int) {
    fun sellShares(company: String, count: Long): Long {
        return baseClient().uri("/sell/$company/$count").responseContent().aggregate().asString().map { it.toLong() }
            .block(kTimeout)!!
    }

    fun buyShares(company: String, count: Long): Long {
        return baseClient().uri("/buy/$company/$count").responseContent().aggregate().asString().map { it.toLong() }
            .block(kTimeout)!!
    }

    fun getOffer(company: String): Shares? {
        val res = baseClient()
            .uri("/get_offer/$company")
            .responseContent().aggregate().asString().map { Json.decodeFromString<Shares?>(it) }.blockOptional(kTimeout)
        check(res.isPresent)
        return res.get()
    }

    fun newCompany(company: String, shares: Shares) {
        baseClient()
            .uri("/new_company/$company/${shares.count}/${shares.price}")
            .responseContent().aggregate().asString().block(kTimeout)!!
    }

    fun assignPrice(company: String, price: Long) {
        baseClient()
            .uri("/assign_price/$company/$price")
            .responseContent().aggregate().asString().block(kTimeout)!!
    }

    private fun baseClient(): HttpClient.ResponseReceiver<*> {
        return HttpClient.create()
            .remoteAddress { InetSocketAddress(host, port) }
            .get()
    }

    private val kTimeout = Duration.ofSeconds(10)
}