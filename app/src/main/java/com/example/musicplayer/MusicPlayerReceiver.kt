package com.example.musicplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MusicPlayerReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        context.sendBroadcast(Intent("Actions").putExtra("actionName", intent?.action))
    }
}