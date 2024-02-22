package com.example.musicplayer.data

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import com.example.musicplayer.R

data class Song(
    val name: String,
    val author: String,
    @DrawableRes val image: Int,
    @RawRes val song: Int
)

object SongSource {
    val collection = listOf<Song>(
        Song("Smells Like Teen Spirit", "Nirvana", R.drawable.nirvana, R.raw.nirvana),
        Song("(I Can’t Get No) Satisfaction", "The Rolling Stones", R.drawable.the_rolling_stones, R.raw.the_rolling_stones),
        Song("Strawberry Fields Forever", "The Beatles", R.drawable.the_beatles, R.raw.the_beatles),
        Song("Bohemian Rhapsody", "Queen", R.drawable.queen, R.raw.queen),
        Song("Dreams", "Fleetwood Mac", R.drawable.fleetwood_mac, R.raw.fleetwood_mac)
    )
}