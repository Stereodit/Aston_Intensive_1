package com.example.musicplayer

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayer.data.Song
import com.example.musicplayer.data.SongSource

class MainActivity : AppCompatActivity() {

    var mMediaPlayer: MediaPlayer? = null
    var currentSong = SongSource.collection.first()
    var isPlaying = false
    var songPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bPrev = findViewById<ImageButton>(R.id.prev_button)
        val bPlay = findViewById<ImageButton>(R.id.play_button)
        val bNext = findViewById<ImageButton>(R.id.next_button)

        bPrev.isEnabled = false

        bPrev.setOnClickListener {
            stopSong()

            bNext.isEnabled = true
            bNext.alpha = 1.0F

            currentSong = SongSource.collection[SongSource.collection.indexOf(currentSong) - 1]
            refreshSong(currentSong)
            bPlay.callOnClick()

            if(SongSource.collection.indexOf(currentSong) == 0) {
                bPrev.isEnabled = false
                bPrev.alpha = 0.5F
            }
        }

        bPlay.setOnClickListener {
            isPlaying = if (mMediaPlayer?.isPlaying == true) {
                mMediaPlayer?.pause()
                bPlay.setImageResource(R.drawable.play_72)
                false
            } else {
                playSong()
                bPlay.setImageResource(R.drawable.pause_72)
                true
            }
        }

        bNext.setOnClickListener {
            stopSong()

            bPrev.isEnabled = true
            bPrev.alpha = 1.0F

            currentSong = SongSource.collection[SongSource.collection.indexOf(currentSong) + 1]
            refreshSong(currentSong)
            bPlay.callOnClick()

            if(SongSource.collection.indexOf(currentSong) == SongSource.collection.lastIndex) {
                bNext.isEnabled = false
                bNext.alpha = 0.5F
            }
        }
    }

    fun refreshSong(currentSong: Song) {
        val image = findViewById<ImageView>(R.id.image)
        val songName = findViewById<TextView>(R.id.songName)
        val authorName = findViewById<TextView>(R.id.authorName)
        val songNumber = findViewById<TextView>(R.id.song_number)

        image.setImageResource(currentSong.image)
        songName.text = currentSong.name
        authorName.text = currentSong.author
        songNumber.text = "${SongSource.collection.indexOf(currentSong) + 1}/5"
    }

    fun playSong() {
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(this, currentSong.song)
            mMediaPlayer!!.isLooping = true
            mMediaPlayer!!.start()
        } else mMediaPlayer!!.start()
    }

    fun stopSong() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.stop()
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }

    override fun onStop() {
        super.onStop()
        if (mMediaPlayer != null) {
            songPosition = mMediaPlayer!!.currentPosition
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)

        savedInstanceState.putInt("indexOfCurrentSong", SongSource.collection.indexOf(currentSong))
        savedInstanceState.putBoolean("isPlaying", isPlaying)
        savedInstanceState.putInt("songPosition", songPosition)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        currentSong = SongSource.collection[savedInstanceState.getInt("indexOfCurrentSong")]
        isPlaying = savedInstanceState.getBoolean("isPlaying")
        songPosition = savedInstanceState.getInt("songPosition")

        refreshSong(currentSong)

        val bPrev = findViewById<ImageButton>(R.id.prev_button)
        val bPlay = findViewById<ImageButton>(R.id.play_button)
        val bNext = findViewById<ImageButton>(R.id.next_button)

        if(SongSource.collection.indexOf(currentSong) == 0) {
            bPrev.isEnabled = false
            bPrev.alpha = 0.5F
        } else {
            bPrev.isEnabled = true
            bPrev.alpha = 1F
        }

        if(SongSource.collection.indexOf(currentSong) == SongSource.collection.lastIndex) {
            bNext.isEnabled = false
            bNext.alpha = 0.5F
        } else {
            bNext.isEnabled = true
            bNext.alpha = 1F
        }

        if(isPlaying) {
            mMediaPlayer = MediaPlayer.create(this, currentSong.song)
            mMediaPlayer!!.isLooping = true
            mMediaPlayer!!.seekTo(songPosition)
            mMediaPlayer!!.start()
            bPlay.setImageResource(R.drawable.pause_72)
        } else {
                mMediaPlayer = MediaPlayer.create(this, currentSong.song)
                mMediaPlayer!!.isLooping = true
                mMediaPlayer!!.seekTo(songPosition)
        }
    }
}