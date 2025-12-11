package com.jualiv.goblingame.game.model

import android.graphics.Bitmap
import android.graphics.Canvas

class Explosion(
    private var bitmap: Bitmap
) {
    var x = 0f
    var y = 0f
    var visible = false
    private var timer = 0f
    private val duration = 3f  // segundos

    fun scaleTo(width: Float, height: Float) {
        bitmap = Bitmap.createScaledBitmap(bitmap, width.toInt(), height.toInt(), true)
    }

    fun trigger(x: Float, y: Float) {
        this.x = x
        this.y = y
        timer = 0f
        visible = true
    }

    fun update(deltaSec: Float) {
        if (!visible) return
        timer += deltaSec
        if (timer >= duration) visible = false
    }

    fun draw(canvas: Canvas) {
        if (visible) canvas.drawBitmap(bitmap, x, y, null)
    }
}