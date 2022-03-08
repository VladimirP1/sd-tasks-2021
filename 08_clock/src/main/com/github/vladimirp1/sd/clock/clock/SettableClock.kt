package com.github.vladimirp1.sd.clock.clock

import java.time.Instant

class SettableClock : Clock {
    var now : Instant? = null

    override fun now(): Instant {
        return now!!
    }
}