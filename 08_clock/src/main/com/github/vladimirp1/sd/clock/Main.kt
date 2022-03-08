package com.github.vladimirp1.sd.clock

import com.github.vladimirp1.sd.clock.clock.NormalClock
import com.github.vladimirp1.sd.clock.clock.SettableClock
import com.github.vladimirp1.sd.clock.evt.EventStatisticImpl
import java.time.Instant

fun main() {
    val clock = NormalClock()
    val stat = EventStatisticImpl(clock)

    stat.incEvent("A")
    Thread.sleep(1500)
    stat.incEvent("B")
    stat.incEvent("A")

    stat.printStatistic()
}
