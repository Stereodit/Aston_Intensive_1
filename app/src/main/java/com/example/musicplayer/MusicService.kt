package com.example.musicplayer

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.example.musicplayer.data.Song
import com.example.musicplayer.data.SongSource


class MusicService: Service() {
    private var mMediaPlayer: MediaPlayer? = null
    private var currentSong = SongSource.collection.first()
    private lateinit var musicServiceNotification: MusicServiceNotification

    private val binder = LocalBinder()
    inner class LocalBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        musicServiceNotification = MusicServiceNotification(this)
        musicServiceNotification.showNotification(currentSong, isPlaying())
    }

    fun getCurrentSong(): Song {
        return currentSong
    }

    fun isPlaying(): Boolean {
        return mMediaPlayer?.isPlaying ?: false
    }

    fun nextSong() {
        if(SongSource.collection.indexOf(currentSong) != SongSource.collection.lastIndex) {
            stopSong()
            currentSong = SongSource.collection[SongSource.collection.indexOf(currentSong) + 1]
            playSong()
        }
    }

    fun prevSong() {
        if(SongSource.collection.indexOf(currentSong) != 0) {
            stopSong()
            currentSong = SongSource.collection[SongSource.collection.indexOf(currentSong) - 1]
            playSong()
        }
    }

    fun playSong() {
        if (mMediaPlayer != null && mMediaPlayer!!.isPlaying) {
            mMediaPlayer!!.pause()
            musicServiceNotification.showNotification(currentSong, isPlaying())
            return
        }
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(this, currentSong.song)
            mMediaPlayer!!.isLooping = true
            mMediaPlayer!!.start()
        } else mMediaPlayer!!.start()
        musicServiceNotification.showNotification(currentSong, isPlaying())
    }

    fun stopSong() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.stop()
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }
}