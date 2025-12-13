package com.jualiv.goblingame

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)   // usa el XML directamente

        val buttonStart = findViewById<MaterialButton>(R.id.buttonStart)
        buttonStart.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }

        val buttonExit = findViewById<MaterialButton>(R.id.buttonExit)
        buttonExit.setOnClickListener {
            finish()
        }
    }
}


