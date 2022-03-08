package com.github.vladimirp1.sd.clock.test

import com.github.vladimirp1.sd.clock.clock.SettableClock
import com.github.vladimirp1.sd.clock.evt.EventStatisticImpl
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertEquals

class EventStatisticImplTest {
    @Test
    fun `Smoke test for getAllEventStatistic`() {
        val clock = SettableClock()

        clock.now = Instant.EPOCH
        val stat = EventStatisticImpl(clock)

        clock.now = clock.now!!.plusSeconds(10)
        stat.incEvent("A")

        assertEquals(stat.getEventStatisticByName("A"), stat.getAllEventStatistic()["A"])
    }

    @Test
    fun `When EventStatisticImpl object is younger than 1 hour, it should return correct results`() {
        val clock = SettableClock()

        clock.now = Instant.EPOCH
        val stat = EventStatisticImpl(clock)

        clock.now = clock.now!!.plusSeconds(10)
        stat.incEvent("A")
        clock.now = clock.now!!.plusSeconds(5)

        assertEquals(1.0 / (15.0 / 60), stat.getEventStatisticByName("A"), .00001)
    }

    @Test
    fun `After one hour events get dropped from stats`() {
        val clock = SettableClock()

        clock.now = Instant.EPOCH
        val stat = EventStatisticImpl(clock)

        clock.now = clock.now!!.plusSeconds(10)
        stat.incEvent("A")
        clock.now = clock.now!!.plusSeconds(5)
        stat.incEvent("B")
        stat.incEvent("A")
        clock.now = clock.now!!.plusSeconds(3600 - 4)

        assertEquals(1.0 / 60, stat.getEventStatisticByName("A"), .00001)
        assertEquals(1.0 / 60, stat.getEventStatisticByName("B"), .00001)
    }
}