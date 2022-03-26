package bl

import currency.CurrencyConvertor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import currency.Currency
import model.Product
import org.kodein.di.DI
import org.kodein.di.instance

class ProductsProcessor(di: DI) {
    private val currencyConvertor: CurrencyConvertor by di.instance()

    fun makeProductsResponse(products: Flow<Product>): Flow<String> {
        return flow {
            emit("<!doctype html>")
            emit("<html lang=en>")
            emit("<head>")
            emit("<meta charset=utf-8>")
            emit("</head>")
            emit("<body>")
            emit("<table>")
            products.collect {
                emit("<tr>")
                emit("<td>")
                emit(it.name)
                emit("</td>")
                emit("<td>")
                emit(it.price.toString() + " " + it.currency.toString())
                emit("</td>")
                emit("</tr>")
            }
            emit("</table>")
            emit("</body>")
        }
    }

    fun convertProductsCurrency(products: Flow<Product>, targetFlow: Flow<Currency>): Flow<Product> {
        return flow {
            val target = targetFlow.toList().first()
            products.collect {
                emit(
                    Product(
                        it.name,
                        currencyConvertor.convert(it.price, it.currency, target),
                        target
                    )
                )
            }
        }
    }
}