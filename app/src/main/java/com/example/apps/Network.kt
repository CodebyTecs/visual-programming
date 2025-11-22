package com.example.android_notes.activities

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import com.example.apps.R
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.telephony.*


class Network : AppCompatActivity() {

    val TAG = "TelephonyActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_network)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "Нет разрешений READ_PHONE_STATE или ACCESS_COARSE_LOCATION для получения cell info")

            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_PHONE_STATE,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION),
                1
            )
        }

        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val list = tm.allCellInfo ?: emptyList()
        if (list.isEmpty()) {
            Log.d(TAG, "Cell list is empty")
            return
        }

        for (info in list) {
            when (info) {
                is CellInfoLte -> {
                    val ci = info.cellIdentity
                    val ss = info.cellSignalStrength

                    Log.d(TAG, "CellInfoLte:")
                    Log.d(TAG, "  CellIdentityLte: Band=${ci.bandwidth}, CellIdentity=${ci.ci}, EARFCN=${ci.earfcn}, MCC=${ci.mccString}, MNC=${ci.mncString}, PCI=${ci.pci}, TAC=${ci.tac}")
                    Log.d(TAG, "  CellSignalStrengthLte: ASU=${ss.asuLevel}, CQI=${ss.cqi}, RSRP=${ss.rsrp}, RSRQ=${ss.rsrq}, RSSI=${ss.rssi}, RSSNR=${ss.rssnr}, TimingAdvance=${ss.timingAdvance}")
                }

                is CellInfoGsm -> {
                    val ci = info.cellIdentity
                    val ss = info.cellSignalStrength

                    Log.d(TAG, "CellInfoGsm:")
                    Log.d(TAG, "  CellIdentityGSM: CellIdentity=${ci.cid}, BSIC=${ci.bsic}, ARFCN=${ci.arfcn}, LAC=${ci.lac}, MCC=${ci.mccString}, MNC=${ci.mncString}")
                    Log.d(TAG, "  CellSignalStrengthGsm: Dbm=${ss.dbm}, RSSI=${ss.rssi}, TimingAdvance=${ss.timingAdvance}")
                }

                is CellInfoNr -> {
                    val ci = info.cellIdentity as CellIdentityNr
                    val ss = info.cellSignalStrength as CellSignalStrengthNr

                    Log.d(TAG, "CellInfoNr:")
                    Log.d(TAG, "  CellIdentityNr: Band=${ci.nrarfcn}, NCI=${ci.nci}, PCI=${ci.pci}, Nrargcn=${ci.nrarfcn}, TAC=${ci.tac}, MCC=${ci.mccString}, MNC=${ci.mncString}")
                    Log.d(TAG, "  CellSignalStrengthNr: SS-RSRP=${ss.ssRsrp}, SS-RSRQ=${ss.ssRsrq}, SS-SINR=${ss.ssSinr}, TimingAdvance=-1")
                }
            }
        }
    }
}