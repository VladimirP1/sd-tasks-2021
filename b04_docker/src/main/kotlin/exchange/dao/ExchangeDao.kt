package exchange.dao

import exchange.model.Shares

interface ExchangeDao {
    fun sellShares(company : String, count : Long) : Long
    fun buyShares(company : String, count : Long) : Long
    fun getOffer(company : String) : Shares?
    fun newCompany(company : String, shares : Shares)
    fun assignPrice(company : String, price : Long)
}