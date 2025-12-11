package com.jualiv.goblingame.game.model

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF

class Goblin(
    private var bitmap: Bitmap,
    private val screenW: Int,
    private val screenH: Int
) {
    var x = 0f
    var y = 0f
    var width = 0f
    var height = 0f

    init {
        // Escalado
        width = screenW / 3f
        val aspect = bitmap.height.toFloat() / bitmap.width.toFloat()
        height = width * aspect
        bitmap = Bitmap.createScaledBitmap(bitmap, width.toInt(), height.toInt(), true)

        x = (screenW - width) / 2f
        y = screenH - height - 50f
    }

    fun moveLeft(step: Float) {
        x = (x - step).coerceAtLeast(0f)
    }

    fun moveRight(step: Float) {
        x = (x + step).coerceAtMost(screenW - width)
    }

    fun draw(canvas: Canvas) {
        canvas.drawBitmap(bitmap, x, y, null)
    }
    //TAMAÑO SPRITE PARA COLISIÓN
    fun getRect(): RectF {
        val marginX = width * 0.3f   // 30% menos por cada lado
        val marginY = height * 0.2f  // 20% menos arriba y abajo
        return RectF(
            x + marginX,
            y + marginY,
            x + width - marginX,
            y + height - marginY
        )
    }
}