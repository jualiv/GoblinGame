package com.jualiv.goblingame.game

import android.content.Context
import android.graphics.Canvas
import android.view.SurfaceHolder
import android.view.SurfaceView

class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    init {
        holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        // de momento vacío
    }

    override fun surfaceChanged(
        holder: SurfaceHolder,
        format: Int,
        width: Int,
        height: Int
    ) {
        // de momento vacío
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        // de momento vacío
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // luego dibujaremos fondo, goblin y bombas aquí
    }
}