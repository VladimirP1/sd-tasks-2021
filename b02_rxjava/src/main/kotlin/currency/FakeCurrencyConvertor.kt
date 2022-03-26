package currency

class FakeCurrencyConvertor : CurrencyConvertor {
    private val rates = mapOf(
        Currency.RUB to 100,
        Currency.USD to 1,
        Currency.EUR to 1
    )

    override fun convert(amount: Double, from: Currency, to: Currency) =
        amount / rates[from]!! * rates[to]!!
}