package com.jualiv.goblingame.game

class SurvivalTimer {

    private var startTime = 0L
    var seconds: Int = 0
        private set

    fun start() {
        startTime = System.nanoTime()
        seconds = 0
    }

    fun update() {
        if (startTime == 0L) return
        val now = System.nanoTime()
        seconds = ((now - startTime) / 1_000_000_000L).toInt()
    }

    fun reset() {
        start()
    }
}