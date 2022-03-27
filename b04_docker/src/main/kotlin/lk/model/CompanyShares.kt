package lk.model

data class CompanyShares (val company : String, val count : Long, val price : Long) {
    init {
        require(count >= 0)
        require(price > 0)
    }
}