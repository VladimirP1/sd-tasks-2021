package com.github.vladimirp1.sd.clock.evt

public interface EventsStatistic {
    fun incEvent(name : String)
    fun getEventStatisticByName(name:String) : Double
    fun getAllEventStatistic() : Map<String, Double>
    fun printStatistic()
}