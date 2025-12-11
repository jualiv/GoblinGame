package com.jualiv.goblingame

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jualiv.goblingame.game.GameView

class GameActivity : AppCompatActivity() {

    private lateinit var gameView: GameView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gameView = GameView(this)
        setContentView(gameView)
    }

    override fun onPause() {
        super.onPause()
        // luego añadiremos pausa del loop
    }

    override fun onResume() {
        super.onResume()
        // luego reanudaremos el loop
    }
}