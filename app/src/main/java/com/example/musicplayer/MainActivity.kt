package com.example.musicplayer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayer.data.Song
import com.example.musicplayer.data.SongSource


class MainActivity : AppCompatActivity() {

    private lateinit var mService: MusicService
    private var mBound: Boolean = false

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as MusicService.LocalBinder
            mService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bPrev = findViewById<ImageButton>(R.id.prev_button)
        val bPlay = findViewById<ImageButton>(R.id.play_button)
        val bNext = findViewById<ImageButton>(R.id.next_button)

        bPrev.isEnabled = false

        bPrev.setOnClickListener {
            if (mBound) {
                mService.prevSong()
                refreshDisplayForSong(mService.getCurrentSong())
                bPlay.setImageResource(R.drawable.pause_72)
            }
            else Toast.makeText(this, "No service found.", Toast.LENGTH_SHORT).show()
        }
        bPlay.setOnClickListener {
            if (mBound) {
                mService.playSong()
                refreshDisplayForSong(mService.getCurrentSong())
                if(mService.isPlaying()) bPlay.setImageResource(R.drawable.pause_72)
                else bPlay.setImageResource(R.drawable.play_72)
            }
            else Toast.makeText(this, "No service found.", Toast.LENGTH_SHORT).show()
        }
        bNext.setOnClickListener {
            if (mBound) {
                mService.nextSong()
                refreshDisplayForSong(mService.getCurrentSong())
                bPlay.setImageResource(R.drawable.pause_72)
            }
            else Toast.makeText(this, "No service found.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, MusicService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        if(isFinishing) {
            unbindService(connection)
            mBound = false
        }
    }

    fun refreshDisplayForSong(currentSong: Song) {
        val image = findViewById<ImageView>(R.id.image)
        val songName = findViewById<TextView>(R.id.songName)
        val authorName = findViewById<TextView>(R.id.authorName)
        val songNumber = findViewById<TextView>(R.id.song_number)

        image.setImageResource(currentSong.image)
        songName.text = currentSong.name
        authorName.text = currentSong.author
        songNumber.text = "${SongSource.collection.indexOf(currentSong) + 1}/5"

        val bPrev = findViewById<ImageButton>(R.id.prev_button)
        val bNext = findViewById<ImageButton>(R.id.next_button)

        if(SongSource.collection.indexOf(currentSong) == 0) {
            bPrev.isEnabled = false
            bPrev.alpha = 0.5F
        } else {
            bPrev.isEnabled = true
            bPrev.alpha = 1.0F
        }

        if(SongSource.collection.indexOf(currentSong) == SongSource.collection.lastIndex) {
            bNext.isEnabled = false
            bNext.alpha = 0.5F
        } else {
            bNext.isEnabled = true
            bNext.alpha = 1.0F
        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)

        savedInstanceState.putInt("indexOfSong", SongSource.collection.indexOf(mService.getCurrentSong()))
        savedInstanceState.putBoolean("isPlaying", mService.isPlaying())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        refreshDisplayForSong(SongSource.collection[savedInstanceState.getInt("indexOfSong")])

        val bPlay = findViewById<ImageButton>(R.id.play_button)
        if(savedInstanceState.getBoolean("isPlaying")) bPlay.setImageResource(R.drawable.pause_72)
        else bPlay.setImageResource(R.drawable.play_72)
    }
}