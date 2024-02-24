package com.example.musicplayer

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayer.MusicPlayerApp.Companion.isReceiverRegistered
import com.example.musicplayer.MusicServiceNotification.Companion.Actions
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

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.extras!!.getString("actionName")) {
                Actions.PLAY.toString() -> findViewById<ImageButton>(R.id.play_button).callOnClick()
                Actions.NEXT.toString() -> findViewById<ImageButton>(R.id.next_button).callOnClick()
                Actions.PREV.toString() -> findViewById<ImageButton>(R.id.prev_button).callOnClick()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(!isReceiverRegistered) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                registerReceiver(broadcastReceiver, IntentFilter("Actions"), RECEIVER_EXPORTED)
            else registerReceiver(broadcastReceiver, IntentFilter("Actions"))
            isReceiverRegistered = true
        }

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
        }
        bPlay.setOnClickListener {
            if (mBound) {
                mService.playSong()
                refreshDisplayForSong(mService.getCurrentSong())
                if(mService.isPlaying()) bPlay.setImageResource(R.drawable.pause_72)
                else bPlay.setImageResource(R.drawable.play_72)
            }
        }
        bNext.setOnClickListener {
            if (mBound) {
                mService.nextSong()
                refreshDisplayForSong(mService.getCurrentSong())
                bPlay.setImageResource(R.drawable.pause_72)
            }
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
        if(isFinishing || isChangingConfigurations) {
            unregisterReceiver(broadcastReceiver)
            isReceiverRegistered = false
        }
    }

    private fun refreshDisplayForSong(currentSong: Song) {
        findViewById<ImageView>(R.id.image).setImageResource(currentSong.image)
        findViewById<TextView>(R.id.songName).text = currentSong.name
        findViewById<TextView>(R.id.authorName).text = currentSong.author
        findViewById<TextView>(R.id.song_number).text = "${SongSource.collection.indexOf(currentSong) + 1}/5"

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

    override fun onDestroy() {
        super.onDestroy()
        if(isFinishing) {
            (this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancelAll()
            unbindService(connection)
        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)

        savedInstanceState.putInt("indexOfSong", SongSource.collection.indexOf(mService.getCurrentSong()))
        savedInstanceState.putBoolean("isPlaying", mService.isPlaying())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        if(mBound) refreshDisplayForSong(mService.getCurrentSong())
        else refreshDisplayForSong(SongSource.collection[savedInstanceState.getInt("indexOfSong")])

        if(savedInstanceState.getBoolean("isPlaying")) findViewById<ImageButton>(R.id.play_button).setImageResource(R.drawable.pause_72)
        else findViewById<ImageButton>(R.id.play_button).setImageResource(R.drawable.play_72)
    }
}