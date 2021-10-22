class Statistics(private val timeProvider: ITimestampProvider) {
    private val regex = Regex("""#\w+""")

    fun Compute(query: String, response: ISearchResult, durationHours: Int): List<Int> {
        val timestamp = timeProvider.getTimestamp()
        val kSecondsInHour = 60 * 60
        val bins: Map<Int, Int> =
            response.posts.map { regex.findAll(it.text) }.map { matchResults ->
                matchResults.map { match -> match.value }.contains(query)
            }.zip(response.posts.map { it.timestamp })
                .groupingBy { ((timestamp - it.second) / kSecondsInHour).toInt() }
                .aggregate { _, count: Int?, element, first ->
                    (if (element.first) 1 else 0) +
                            (if (first) 0 else count!!)
                }.toMap()
        return (0 until durationHours).map { bins.getOrDefault(it, 0) }
    }
}