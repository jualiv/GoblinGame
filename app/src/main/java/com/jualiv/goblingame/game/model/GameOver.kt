package com.jualiv.goblingame.game.model

import android.graphics.Bitmap
import android.graphics.Canvas

class GameOver(
    private var bitmap: Bitmap,
    private val screenW: Int,
    private val screenH: Int
) {
    var visible = false
    private var timer = 0f
    private val duration = 2f  // segundos antes de volver al menú

    private var x = 0f
    private var y = 0f

    init {
        // Escalar al 70% del ancho de pantalla y centrar
        val targetWidth = (screenW * 0.7f).toInt()
        val aspect = bitmap.height.toFloat() / bitmap.width.toFloat()
        val targetHeight = (targetWidth * aspect).toInt()
        bitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)

        x = (screenW - bitmap.width) / 2f
        y = (screenH - bitmap.height) / 2f
    }

    fun show() {
        visible = true
        timer = 0f
    }

    fun update(deltaSec: Float) {
        if (!visible) return
        timer += deltaSec
    }

    fun isFinished(): Boolean = visible && timer >= duration

    fun draw(canvas: Canvas) {
        if (visible) {
            canvas.drawBitmap(bitmap, x, y, null)
        }
    }
}