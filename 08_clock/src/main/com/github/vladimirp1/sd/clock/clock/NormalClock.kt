package com.github.vladimirp1.sd.clock.clock

import java.time.Instant

class NormalClock : Clock {
    override fun now(): Instant {
        return Instant.now()
    }

}