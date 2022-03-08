package com.github.vladimirp1.sd.clock.evt

import com.github.vladimirp1.sd.clock.clock.Clock
import java.time.Duration
import java.time.Instant
import kotlin.math.min

class EventStatisticImpl(private val clock: Clock) : EventsStatistic {
    private val kHistoryLengthSeconds: Long = 3600

    private val kMinutesInHour: Double = 60.0
    private val kSecondsInMinute: Double = 60.0
    private val kMillisInSecond: Double = 1000.0

    private val events = mutableMapOf<String, ArrayDeque<Instant>>()
    private val created = clock.now()

    override fun incEvent(name: String) {
        val now = clock.now()
        val deque = events.getOrPut(name) { ArrayDeque() }
        deque.addLast(now)
        cleanupOld(now, deque)
    }

    override fun getEventStatisticByName(name: String): Double {
        return getEventStatisticByNameImpl(name)
    }

    override fun getAllEventStatistic(): Map<String, Double> {
        return getAllEventStatisticImpl()
    }

    override fun printStatistic() {
        val stats = getAllEventStatisticImpl()
        println("## Statistics: ##")
        print(stats.map { "  ${it.key} -- ${it.value} rps" }.joinToString("\n"))
    }

    private fun getAllEventStatisticImpl(): Map<String, Double> {
        return events.keys.associateWith { getEventStatisticByName(it) }
    }

    private fun getEventStatisticByNameImpl(name: String): Double {
        val now = clock.now()
        val deque = events[name] ?: return 0.0
        cleanupOld(now, deque)
        return deque.size / historyLengthMinutes()
    }

    private fun cleanupOld(now: Instant, q: ArrayDeque<Instant>) {
        while (!q.isEmpty() && q.first() < now.minusSeconds(kHistoryLengthSeconds)) {
            q.removeFirst()
        }
    }

    private fun historyLengthMinutes(): Double {
        return min(
            Duration.between(created, clock.now()).toMillis() / kMillisInSecond,
            kHistoryLengthSeconds.toDouble()
        ) / kSecondsInMinute
    }
}