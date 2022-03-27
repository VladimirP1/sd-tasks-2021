package lk.dao

import lk.model.CompanyShares

interface LkDao {
    fun addUser(name : String) : Long
    fun deposit(id : Long, amount : Long)
    fun usersShares(id : Long) : List<CompanyShares>
    fun fullBalance(id : Long) : Long
    fun balance(id : Long) : Long
    fun buyShares(id : Long, company : String, count : Long)
    fun sellShares(id : Long, company : String, count : Long)
}