import bl.ProductsProcessor
import currency.CurrencyConvertor
import currency.FakeCurrencyConvertor
import dao.Dao
import io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE
import io.netty.handler.codec.http.HttpHeaderValues.TEXT_HTML
import io.netty.handler.codec.http.HttpHeaderValues.TEXT_PLAIN
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.asFlux
import currency.Currency
import model.Product
import model.User
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import reactor.netty.http.server.HttpServer

fun main() {
    val di = DI {
        val client = KMongo.createClient().coroutine
        val database = client.getDatabase("test")
        bind<CoroutineDatabase> { singleton { database } }
        bind<CurrencyConvertor> { singleton { FakeCurrencyConvertor() } }
        bind<Dao> { singleton { Dao(di) } }
        bind<ProductsProcessor> { singleton { ProductsProcessor(di) } }
    }

    val dao: Dao by di.instance()
    val productsProcessor: ProductsProcessor by di.instance()

    val server = HttpServer.create().port(8080).route { r ->
        r.get(
            "/products/{user}"
        ) { req, res ->
            res.header(CONTENT_TYPE, TEXT_HTML)
                .sendString(
                    productsProcessor.makeProductsResponse(
                        productsProcessor.convertProductsCurrency(
                            dao.getProducts(),
                            dao.getUserCurrency(req.params()["user"]!!)
                        )
                    ).asFlux()
                )
        }.get(
            "/add_product/{name}/{price}/{currency}"
        ) { req, res ->
            res.header(CONTENT_TYPE, TEXT_PLAIN).sendString(
                dao.addProduct(
                    Product(
                        req.params()["name"]!!,
                        req.params()["price"]!!.toDouble(),
                        Currency.valueOf(req.params()["currency"]!!)
                    )
                ).map { it.toString() }.asFlux()
            )
        }.get(
            "/add_user/{name}/{currency}"
        ) { req, res ->
            res.header(CONTENT_TYPE, TEXT_PLAIN).sendString(
                dao.addUser(
                    User(
                        req.params()["name"]!!,
                        Currency.valueOf(req.params()["currency"]!!)
                    )
                ).map { it.toString() }.asFlux()
            )
        }
    }

    server.bindNow()
        .onDispose()
        .block()
}