package com.example.apps

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.android_notes.activities.DataLocations
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.zeromq.SocketType
import org.zeromq.ZContext
import org.zeromq.ZMQ

class Sockets : AppCompatActivity(), LocationListener {

    val LOG_TAG: String = "SOCKETS_ACTIVITY"

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
    }

    private lateinit var locationManager: LocationManager
    private lateinit var bBackToMain: Button
    private lateinit var bSendClientData: Button
    private lateinit var tvClient: TextView
    private lateinit var tvServer: TextView
    private lateinit var handler: Handler
    private var lastLatitude: Double? = null
    private var lastLongitude: Double? = null
    private lateinit var zmqContext: ZContext
    private lateinit var zmqSocket: ZMQ.Socket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sockets)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bBackToMain = findViewById(R.id.back_to_main)
        bSendClientData = findViewById(R.id.send_client_data)
        tvClient = findViewById(R.id.tv_client)
        tvServer = findViewById(R.id.tv_server)
        handler = Handler(Looper.getMainLooper())
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    override fun onResume() {
        super.onResume()

        bBackToMain.setOnClickListener {
            val backToMain = Intent(this, MainActivity::class.java)
            startActivity(backToMain)
        }

        bSendClientData.setOnClickListener {
            startClient()
        }
    }

    fun startServer() {
        val context = ZContext()
        val socket = context.createSocket(SocketType.REP)
        socket.bind("tcp://127.0.0.1:2222")
        var counter = 0

        while (true) {
            counter++
            val requestBytes = socket.recv(0)
            val request = String(requestBytes, ZMQ.CHARSET)
            println("[SERVER] Received request: [$request]")

            handler.post {
                tvServer.text = "Received MSG from Client = $counter"
            }

            Thread.sleep(1000)

            val response = "Hello from Android ZMQ Server!"
            socket.send(response.toByteArray(ZMQ.CHARSET), 0)
            println("[SERVER] Sent reply: [$response]")
        }
    }

    fun startClient() {
        if (!::zmqContext.isInitialized) {
            zmqContext = ZContext()
            zmqSocket = zmqContext.createSocket(SocketType.REQ)
            zmqSocket.connect("tcp://10.84.72.216:2222")
            Log.d(LOG_TAG, "ZMQ client connected to tcp://10.84.72.216:2222")
        }
        /*
        val request = "Hello from Android client!"
        for(i in 0..10){
            zmqSocket.send(request.toByteArray(ZMQ.CHARSET), 0)
            Log.d(LOG_TAG, "[CLIENT] SendT: $request")

            val reply = zmqSocket.recv(0)
            Log.d(LOG_TAG, "[CLIENT] Received: " + String(reply, ZMQ.CHARSET))
        }
        */
        updateCurrentLocation()
    }

    private fun updateCurrentLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions()
                    return
                }

                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000L,
                    1f,
                    this
                )
            } else {
                Toast.makeText(applicationContext, "Enable location in settings", Toast.LENGTH_SHORT)
                    .show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            Log.w(LOG_TAG, "location permission is not allowed")
            tvClient.text = "Permission is not granted"
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        Log.w(LOG_TAG, "requestPermissions()")
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_ACCESS_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "Permission granted", Toast.LENGTH_SHORT).show()
                updateCurrentLocation()
            } else {
                Toast.makeText(applicationContext, "Denied by user", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    override fun onLocationChanged(location: Location) {
        if (lastLatitude != location.latitude || lastLongitude != location.longitude) {
            lastLatitude = location.latitude
            lastLongitude = location.longitude

            val data = DataLocations(
                latitude = location.latitude.toString(),
                longitude = location.longitude.toString(),
                altitude = location.altitude.toString(),
                currentTime = location.time.toString()
            )
            sendLocationInJson(data)
        }
    }

    private fun sendLocationInJson(data: DataLocations) {
        val jsonString = Json.encodeToString(data)
        Log.d(LOG_TAG, "Sending JSON: $jsonString")

        val thread = Thread {
            try {
                zmqSocket.send(jsonString.toByteArray(ZMQ.CHARSET), 0)
                val replyBytes = zmqSocket.recv(0)
                val reply = String(replyBytes, ZMQ.CHARSET)
                Log.d(LOG_TAG, "Server reply: $reply")
                handler.post {
                    tvClient.text = "Ответ от сервера: $reply"
                }
            } catch (e: Exception) {
                Log.e(LOG_TAG, "Error sending JSON via ZMQ", e)
            }
        }
        thread.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (::zmqSocket.isInitialized) {
                zmqSocket.close()
                Log.d(LOG_TAG, "ZMQ socket closed")
            }
            if (::zmqContext.isInitialized) {
                zmqContext.close()
                Log.d(LOG_TAG, "ZMQ context closed")
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Error closing ZMQ", e)
        }
    }
}