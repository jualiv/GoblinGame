package com.jualiv.goblingame.game

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.jualiv.goblingame.R
import com.jualiv.goblingame.game.model.Bomb
import com.jualiv.goblingame.game.model.Explosion
import com.jualiv.goblingame.game.model.Goblin
import com.jualiv.goblingame.game.model.GameOver

class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    private var gameThread: GameThread? = null
    private lateinit var gameOver: GameOver

    // Fondo
    private lateinit var bgBitmap: Bitmap
    private var screenW = 0
    private var screenH = 0

    // Objetos del juego
    private lateinit var goblin: Goblin
    private val bombs = mutableListOf<Bomb>()
    private lateinit var explosion: Explosion

    // Estado
    private var isGameOver = false

    // Spawn de bombas
    private var bombSpawnTimer = 0f
    private val bombSpawnInterval = 3f   // segundos entre bombas

    // Tiempo
    private var lastTime = System.nanoTime()

    init {
        holder.addCallback(this)
        isFocusable = true
        loadBackground()
    }

    private fun loadBackground() {
        bgBitmap = BitmapFactory.decodeResource(resources, R.drawable.bosque)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        screenW = width
        screenH = height

        //referenciamos a la imagen en drawable/
        val goBmp = BitmapFactory.decodeResource(resources, R.drawable.game_over01)
        gameOver = GameOver(goBmp, screenW, screenH)

        // Escalar fondo a pantalla
        bgBitmap = Bitmap.createScaledBitmap(bgBitmap, screenW, screenH, true)

        // Crear goblin
        val goblinBmp = BitmapFactory.decodeResource(resources, R.drawable.goblin)
        goblin = Goblin(goblinBmp, screenW, screenH)

        // Crear explosión (mismo tamaño que el goblin)
        val explosionBmp = BitmapFactory.decodeResource(resources, R.drawable.explosion)
        explosion = Explosion(explosionBmp).also {
            it.scaleTo(goblin.width, goblin.height)
        }

        // Limpiar estado
        bombs.clear()
        isGameOver = false
        bombSpawnTimer = 0f
        lastTime = System.nanoTime()

        // Lanzar hilo del juego
        gameThread = GameThread(holder, this).also {
            it.running = true
            it.start()
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        var retry = true
        gameThread?.running = false
        while (retry) {
            try {
                gameThread?.join()
                retry = false
            } catch (_: InterruptedException) { }
        }
    }

    override fun surfaceChanged(
        holder: SurfaceHolder,
        format: Int,
        width: Int,
        height: Int
    ) {
        // No usado de momento
    }

    // Llamado desde GameThread en cada frame
    fun update() {
        val now = System.nanoTime()
        val deltaSec = (now - lastTime) / 1_000_000_000f
        lastTime = now

        if (isGameOver) {
            explosion.update(deltaSec)
            gameOver.update(deltaSec)

            if (!gameOver.visible) {
                gameOver.show()
            } else if (gameOver.isFinished()) {
                val intent = android.content.Intent(context, com.jualiv.goblingame.MainActivity::class.java)
                intent.addFlags(
                    android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                )
                context.startActivity(intent)
            }
            return
        }

        // Spawn periódico de bombas
        bombSpawnTimer += deltaSec
        if (bombSpawnTimer >= bombSpawnInterval) {
            bombSpawnTimer = 0f

            val bombBmp = BitmapFactory.decodeResource(resources, R.drawable.bomba)
            val bomb = Bomb(
                bombBmp,
                screenW,
                screenH,
                speed = 600f
            )
            bombs.add(bomb)
        }

        // Actualizar bombas
        bombs.forEach { it.update(deltaSec) }

        // Actualizar explosión (por si está activa)
        explosion.update(deltaSec)

        // Comprobar colisiones goblin–bombas
        val goblinRect = goblin.getRect()
        for (bomb in bombs) {
            if (bomb.active && android.graphics.RectF.intersects(bomb.getRect(), goblinRect)) {
                isGameOver = true
                bomb.active = false
                explosion.trigger(goblin.x, goblin.y)
                break
            }
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        // Fondo
        canvas.drawBitmap(bgBitmap, 0f, 0f, null)

        // Bombas
        bombs.forEach { it.draw(canvas) }

        // Goblin
        goblin.draw(canvas)

        // Explosión
        explosion.draw(canvas)

        // GameOver
        gameOver.draw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isGameOver) return true

        if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
            val step = screenW / 15f
            if (event.x < screenW / 2f) {
                goblin.moveLeft(step)
            } else {
                goblin.moveRight(step)
            }
        }
        return true
    }

    fun pause() {
        gameThread?.running = false
    }

    fun resume() {
        if (holder.surface.isValid && (gameThread == null || !gameThread!!.isAlive)) {
            gameThread = GameThread(holder, this).also {
                lastTime = System.nanoTime()
                it.running = true
                it.start()
            }
        }
    }
}
