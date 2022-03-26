package dao

import kotlinx.coroutines.flow.*
import currency.Currency
import model.Product
import model.User
import org.kodein.di.DI
import org.kodein.di.instance
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class Dao(val di: DI) {
    private val db: CoroutineDatabase by di.instance()
    private val productColl = db.getCollection<Product>()
    private val userColl = db.getCollection<User>()

    fun getProducts(): Flow<Product> {
        return productColl.find().toFlow()
    }

    fun addProduct(product: Product): Flow<Boolean> {
        return flowOf(
            productColl.find(Product::name eq product.name).toFlow().map { false },
            flow { emit(productColl.insertOne(product).wasAcknowledged()) }).flattenConcat().take(1)
    }

    fun getUserCurrency(name: String): Flow<Currency> {
        return userColl.find(User::name eq name).toFlow().map { it.currency }
    }

    fun addUser(user: User): Flow<Boolean> {
        return flowOf(
            userColl.find(User::name eq user.name).toFlow().map { false },
            flow { emit(userColl.insertOne(user).wasAcknowledged()) }).flattenConcat().take(1)
    }
}