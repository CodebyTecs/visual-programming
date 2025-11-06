package com.example.apps

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

class Player : AppCompatActivity() {

    private lateinit var playBtn: FloatingActionButton
    private lateinit var backBtn: FloatingActionButton
    private lateinit var restartBtn: FloatingActionButton
    private lateinit var forwardBtn: FloatingActionButton
    private lateinit var listBtn: FloatingActionButton
    private lateinit var seekBar: SeekBar
    private lateinit var currentTimeTv: TextView
    private lateinit var totalTimeTv: TextView
    private lateinit var titleTv: TextView

    private val trackUris = ArrayList<Uri>()
    private val trackTitles = ArrayList<String>()
    private val targetFolder = "Music/"

    private var mediaPlayer: MediaPlayer? = null
    private var currentIndex = 0

    private val handler = Handler(Looper.getMainLooper())
    private val updater = object : Runnable {
        override fun run() {
            val mp = mediaPlayer ?: return
            if (mp.isPlaying) {
                val pos = mp.currentPosition
                seekBar.progress = pos
                currentTimeTv.text = formatTime(pos)
                handler.postDelayed(this, 500)
            }
        }
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                loadSongsFromFolder()
                if (trackUris.isEmpty()) {
                    Toast.makeText(this, "Песни не найдены в $targetFolder", Toast.LENGTH_LONG).show()
                } else {
                    startFirstTrack()
                }
            } else {
                Toast.makeText(this, "Нет разрешения на музыку", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        playBtn = findViewById(R.id.playbtn)
        backBtn = findViewById(R.id.backbtn)
        restartBtn = findViewById(R.id.restartbtn)
        forwardBtn = findViewById(R.id.forwardbtn)
        listBtn = findViewById(R.id.listbtn)
        seekBar = findViewById(R.id.seekbar)
        currentTimeTv = findViewById(R.id.current_time)
        totalTimeTv = findViewById(R.id.total_time)
        titleTv = findViewById(R.id.track_title)

        playBtn.setOnClickListener {
            val mp = mediaPlayer ?: return@setOnClickListener
            if (mp.isPlaying) {
                mp.pause()
                playBtn.setImageResource(R.drawable.vector_play_button)
            } else {
                mp.start()
                handler.post(updater)
                playBtn.setImageResource(R.drawable.vector_pause_button)
            }
        }

        backBtn.setOnClickListener {
            if (trackUris.isEmpty()) return@setOnClickListener
            currentIndex = if (currentIndex > 0) currentIndex - 1 else trackUris.size - 1
            switchTo(currentIndex)
        }

        restartBtn.setOnClickListener {
            val mp = mediaPlayer ?: return@setOnClickListener
            mp.seekTo(0)
            if (!mp.isPlaying) {
                mp.start()
                handler.post(updater)
            }
            seekBar.progress = 0
            currentTimeTv.text = formatTime(0)
            playBtn.setImageResource(R.drawable.vector_pause_button)
        }

        forwardBtn.setOnClickListener {
            if (trackUris.isEmpty()) return@setOnClickListener
            currentIndex = (currentIndex + 1) % trackUris.size
            switchTo(currentIndex)
        }

        listBtn.setOnClickListener { showTrackListDialog() }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) mediaPlayer?.seekTo(progress)
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        askPermissionThenLoad()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updater)
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun askPermissionThenLoad() {
        val permission = Manifest.permission.READ_MEDIA_AUDIO
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            loadSongsFromFolder()
            if (trackUris.isEmpty()) {
                Toast.makeText(this, "Песни не найдены в $targetFolder", Toast.LENGTH_LONG).show()
            } else {
                startFirstTrack()
            }
        } else {
            permissionLauncher.launch(permission)
        }
    }

    private fun loadSongsFromFolder() {
        trackUris.clear()
        trackTitles.clear()
        val audioUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = "${MediaStore.Audio.Media.IS_MUSIC}=1 AND ${MediaStore.Audio.Media.RELATIVE_PATH} LIKE ?"
        val selectionArgs = arrayOf("$targetFolder%")
        val projection = arrayOf(MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE)
        val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} DESC"
        contentResolver.query(audioUri, projection, selection, selectionArgs, sortOrder)?.use { c ->
            val idCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            while (c.moveToNext()) {
                val id = c.getLong(idCol)
                val uri = Uri.withAppendedPath(audioUri, id.toString())
                val title = c.getString(titleCol) ?: "Без названия"
                trackUris.add(uri)
                trackTitles.add(title)
            }
        }
    }

    private fun startFirstTrack() {
        currentIndex = 0
        switchTo(currentIndex)
    }

    private fun switchTo(index: Int) {
        handler.removeCallbacks(updater)
        mediaPlayer?.release()
        mediaPlayer = null
        val uri = trackUris[index]
        val title = trackTitles.getOrNull(index) ?: "Трек"
        titleTv.text = title
        val mp = MediaPlayer()
        mp.setDataSource(this, uri)
        mp.setOnPreparedListener {
            seekBar.max = mp.duration
            totalTimeTv.text = formatTime(mp.duration)
            currentTimeTv.text = formatTime(0)
            seekBar.progress = 0
            mp.start()
            handler.post(updater)
            playBtn.setImageResource(R.drawable.vector_pause_button)
        }
        mp.setOnCompletionListener {
            currentIndex = (currentIndex + 1) % trackUris.size
            switchTo(currentIndex)
        }
        mp.prepareAsync()
        mediaPlayer = mp
    }

    private fun showTrackListDialog() {
        if (trackUris.isEmpty()) {
            Toast.makeText(this, "Список пуст — добавь музыку в $targetFolder", Toast.LENGTH_SHORT).show()
            return
        }
        val listView = ListView(this)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, trackTitles)
        listView.adapter = adapter
        val dialog = AlertDialog.Builder(this)
            .setTitle("Выбери трек")
            .setView(listView)
            .setNegativeButton("Отмена", null)
            .create()
        listView.setOnItemClickListener { _, _, position, _ ->
            currentIndex = position
            switchTo(currentIndex)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun formatTime(ms: Int): String {
        val totalSec = ms / 1000
        val m = totalSec / 60
        val s = totalSec % 60
        return String.format("%d:%02d", m, s)
    }
}