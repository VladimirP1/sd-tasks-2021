package exchange

import exchange.dao.ExchangeDao
import exchange.dao.ExchangeDaoImpl
import io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE
import io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON
import io.netty.handler.codec.http.HttpHeaderValues.TEXT_PLAIN
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.reactor.asFlux
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import exchange.model.Shares
import reactor.netty.http.server.HttpServer
import util.par

fun main() {
    val dao: ExchangeDao = ExchangeDaoImpl()

    HttpServer.create().port(8080).route { r ->
        r.get(
            "/sell/{company}/{count}"
        ) { req, res ->
            res.header(CONTENT_TYPE, TEXT_PLAIN).sendString(
                flowOf(dao.sellShares(req.par("company"), req.par("count").toLong()).toString()).asFlux()
            )
        }.get(
            "/buy/{company}/{count}"
        ) { req, res ->
            res.header(CONTENT_TYPE, TEXT_PLAIN).sendString(
                flowOf(dao.buyShares(req.par("company"), req.par("count").toLong()).toString()).asFlux()
            )
        }.get(
            "/get_offer/{company}"
        ) { req, res ->
            res.header(CONTENT_TYPE, APPLICATION_JSON).sendString(
                flowOf(Json.encodeToString(dao.getOffer(req.par("company")))).asFlux()
            )
        }.get(
            "/new_company/{company}/{count}/{price}"
        ) { req, res ->
            dao.newCompany(
                req.par("company"),
                Shares(req.par("count").toLong(), req.par("price").toLong())
            )
            res.header(CONTENT_TYPE, TEXT_PLAIN).sendString(
                flowOf("OK").asFlux()
            )
        }.get(
            "/assign_price/{company}/{price}"
        ) { req, res ->
            dao.assignPrice(
                req.par("company"),
                req.par("price").toLong()
            )
            res.header(CONTENT_TYPE, TEXT_PLAIN).sendString(
                flowOf("OK").asFlux()
            )
        }
    }.bindNow().onDispose().block()
}