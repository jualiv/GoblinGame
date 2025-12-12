package com.jualiv.goblingame.game

import android.content.Context
import android.media.MediaPlayer
import android.media.SoundPool
import com.jualiv.goblingame.R

class SoundManager(context: Context) {

    private val appContext = context.applicationContext

    private var bgMusic: MediaPlayer? = null
    private var soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(4)
        .build()

    private var explosionSound: Int =
        soundPool.load(appContext, R.raw.explosion, 1)

    fun startMusic() {
        if (bgMusic == null) {
            bgMusic = MediaPlayer.create(appContext, R.raw.music).apply {
                isLooping = true
                setVolume(1f, 1f)
            }
        }
        bgMusic?.start()
    }

    fun pauseMusic() {
        bgMusic?.pause()
    }

    fun stopAndRelease() {
        bgMusic?.stop()
        bgMusic?.release()
        bgMusic = null
        soundPool.release()
    }

    fun playExplosion() {
        soundPool.play(explosionSound, 1f, 1f, 1, 0, 1f)
    }
}