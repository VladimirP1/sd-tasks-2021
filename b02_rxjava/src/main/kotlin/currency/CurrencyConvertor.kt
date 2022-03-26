package currency

interface CurrencyConvertor {
    fun convert(amount: Double, from: Currency, to: Currency) : Double
}