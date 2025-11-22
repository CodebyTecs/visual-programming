package com.example.android_notes.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
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
import com.example.apps.MainActivity
import com.example.apps.R
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class DataLocations(
    val latitude: String,
    val longitude: String,
    val altitude: String,
    val currentTime: String
)

class Locations : AppCompatActivity(), LocationListener {

    val value: Int = 0
    val LOG_TAG: String = "LOCATION_ACTIVITY"
    private lateinit var bBackToMain: Button

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
    }

    private lateinit var locationManager: LocationManager
    private lateinit var tvLat: TextView
    private lateinit var tvLon: TextView
    private lateinit var tvAlt: TextView
    private lateinit var tvCurrentTime: TextView
    private lateinit var tvAppContext: TextView
    private lateinit var tvActivityContext: TextView

    private var lastLatitude: Double? = null
    private var lastLongitude: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_location)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bBackToMain = findViewById(R.id.back_to_main)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        tvLat = findViewById(R.id.tv_lat)
        tvLon = findViewById(R.id.tv_lon)
        tvAlt = findViewById(R.id.tv_alt)
        tvCurrentTime = findViewById(R.id.tv_curtime)
        tvAppContext = findViewById(R.id.tv_appContext)
        tvActivityContext = findViewById(R.id.tv_activityContext)

        tvAppContext.text = applicationContext.toString()
        tvActivityContext.text = this.toString()
    }

    override fun onResume() {
        super.onResume()

        bBackToMain.setOnClickListener {
            val backToMain = Intent(this, MainActivity::class.java)
            startActivity(backToMain)
        }

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

                // GPS_PROVIDER
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
            tvLat.text = "Permission is not granted"
            tvLon.text = "Permission is not granted"
            tvAlt.text = "Permission is not granted"
            tvCurrentTime.text = "Permission is not granted"
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
        tvLat.text = "Latitude: ${location.latitude}"
        tvLon.text = "Longitude: ${location.longitude}"
        tvAlt.text = "Altitude: ${location.altitude}"
        tvCurrentTime.text = "Current Time: ${location.time}"

        if (lastLatitude != location.latitude || lastLongitude != location.longitude) {
            lastLatitude = location.latitude
            lastLongitude = location.longitude

            val data = DataLocations(
                latitude = location.latitude.toString(),
                longitude = location.longitude.toString(),
                altitude = location.altitude.toString(),
                currentTime = location.time.toString()
            )

            appendLocationToJson(data)
        }
    }
    private fun appendLocationToJson(data: DataLocations) {
        val downloadDir = android.os.Environment.getExternalStoragePublicDirectory(
            android.os.Environment.DIRECTORY_DOWNLOADS
        )
        if (!downloadDir.exists()) {
            downloadDir.mkdirs()
        }

        val file = File(downloadDir, "location.json")

        val jsonString = Json.encodeToString(data)
        file.appendText(jsonString + "\n", Charsets.UTF_8)
    }
}