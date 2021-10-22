class TimestampProviderImpl : ITimestampProvider {
    override fun getTimestamp(): Long {
        return System.currentTimeMillis() / 1000L
    }
}