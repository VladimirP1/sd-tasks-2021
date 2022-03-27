package lk

import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpHeaderValues
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.reactor.asFlux
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import lk.client.ExchangeClient
import lk.dao.LkDao
import lk.dao.LkDaoImpl
import reactor.netty.http.server.HttpServer
import util.par


fun main() {
    val exchange: ExchangeClient = ExchangeClient("127.0.0.1", 8080)

    val dao: LkDao = LkDaoImpl(exchange)

    HttpServer.create().port(8080).route { r ->
        r.get(
            "/add_user/{name}"
        ) { req, res ->
            res.header(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN).sendString(
                flowOf(dao.addUser(req.par("name")).toString()).asFlux()
            )
        }.get(
            "/deposit/{uid}/{amount}"
        ) { req, res ->
            dao.deposit(req.par("uid").toLong(), req.par("amount").toLong())
            res.header(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN).sendString(
                flowOf("OK").asFlux()
            )
        }.get(
            "/buy/{uid}/{company}/{count}"
        ) { req, res ->
            dao.buyShares(req.par("uid").toLong(), req.par("company"), req.par("count").toLong())
            res.header(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN).sendString(
                flowOf("OK").asFlux()
            )
        }.get(
            "/sell/{uid}/{company}/{count}"
        ) { req, res ->
            dao.sellShares(req.par("uid").toLong(), req.par("company"), req.par("count").toLong())
            res.header(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN).sendString(
                flowOf("OK").asFlux()
            )
        }.get(
            "/user_shares/{uid}"
        ) { req, res ->
            res.header(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN).sendString(
                flowOf(Json.encodeToString(dao.usersShares(req.par("uid").toLong()))).asFlux()
            )
        }.get(
            "/balance/{uid}"
        ) { req, res ->
            res.header(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN).sendString(
                flowOf(dao.balance(req.par("uid").toLong()).toString()).asFlux()
            )
        }.get(
            "/full_balance/{uid}"
        ) { req, res ->
            res.header(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN).sendString(
                flowOf(dao.balance(req.par("uid").toLong()).toString()).asFlux()
            )
        }
    }.bindNow().onDispose().block()
}