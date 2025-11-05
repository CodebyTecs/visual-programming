package com.example.apps

import android.os.Bundle
import androidx.activity.ComponentActivity
import android.widget.Button
import android.content.Intent

class MainActivity : ComponentActivity() {
    private lateinit var bGoToCalculatorActivity: Button
    private lateinit var bGoToMediaPlayerActivity: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bGoToCalculatorActivity = findViewById(R.id.btn0)
        bGoToMediaPlayerActivity = findViewById(R.id.btn1)

        bGoToCalculatorActivity.setOnClickListener({
            val randomIntent = Intent(this, Calculator::class.java)
            startActivity(randomIntent)
        });

        bGoToMediaPlayerActivity.setOnClickListener({
            val randomIntent = Intent(this, Player::class.java)
            startActivity(randomIntent)
        });
    }
}