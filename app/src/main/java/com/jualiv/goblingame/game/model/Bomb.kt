package com.jualiv.goblingame.game.model

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF

class Bomb(
    private var bitmap: Bitmap,
    private val screenW: Int,
    private val screenH: Int,
    private val speed: Float
) {
    var x = 0f
    var y = 0f
    var width = 0f
    var height = 0f
    var active = true

    init {
        width = screenW / 5f
        val aspect = bitmap.height.toFloat() / bitmap.width.toFloat()
        height = width * aspect
        bitmap = Bitmap.createScaledBitmap(bitmap, width.toInt(), height.toInt(), true)

        reset()
    }

    fun reset() {
        x = (0..(screenW - width.toInt())).random().toFloat()
        y = -height
        active = true
    }

    fun update(deltaSec: Float) {
        if (!active) return
        y += speed * deltaSec
        if (y > screenH) active = false
    }

    fun draw(canvas: Canvas) {
        if (active) canvas.drawBitmap(bitmap, x, y, null)
    }

    //TAMAÑO SPRITE PARA COLISIÓN
    fun getRect(): RectF {
        val marginX = width * 0.2f
        val marginY = height * 0.2f
        return RectF(
            x + marginX,
            y + marginY,
            x + width - marginX,
            y + height - marginY
        )
    }
}