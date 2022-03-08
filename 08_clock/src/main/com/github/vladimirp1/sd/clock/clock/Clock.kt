package com.github.vladimirp1.sd.clock.clock

import java.time.Instant

interface Clock {
    fun now() : Instant
}