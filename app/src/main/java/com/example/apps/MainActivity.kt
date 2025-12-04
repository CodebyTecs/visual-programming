package com.example.apps

import android.os.Bundle
import androidx.activity.ComponentActivity
import android.widget.Button
import android.content.Intent
import com.example.android_notes.activities.Locations
import com.example.android_notes.activities.Network

class MainActivity : ComponentActivity() {
    private lateinit var bGoToCalculatorActivity: Button
    private lateinit var bGoToMediaPlayerActivity: Button
    private lateinit var bGoToLocationActivity: Button
    private lateinit var bGoToNetworkActivity: Button
    private lateinit var bGoToSocketsActivity: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bGoToCalculatorActivity = findViewById(R.id.btn0)
        bGoToMediaPlayerActivity = findViewById(R.id.btn1)
        bGoToLocationActivity = findViewById(R.id.btn2)
        bGoToNetworkActivity = findViewById(R.id.btn3)
        bGoToSocketsActivity = findViewById(R.id.btn4)

        bGoToCalculatorActivity.setOnClickListener({
            val randomIntent = Intent(this, Calculator::class.java)
            startActivity(randomIntent)
        });

        bGoToMediaPlayerActivity.setOnClickListener({
            val randomIntent = Intent(this, Player::class.java)
            startActivity(randomIntent)
        });

        bGoToLocationActivity.setOnClickListener({
            val randomIntent = Intent(this, Locations::class.java)
            startActivity(randomIntent)
        });

        bGoToNetworkActivity.setOnClickListener({
            val randomIntent = Intent(this, Network::class.java)
            startActivity(randomIntent)
        });

        bGoToSocketsActivity.setOnClickListener({
            val randomIntent = Intent(this, Sockets::class.java)
            startActivity(randomIntent)
        });
    }
}