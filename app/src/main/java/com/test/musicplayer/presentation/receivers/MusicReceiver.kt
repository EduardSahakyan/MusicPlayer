package com.test.musicplayer.presentation.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.test.musicplayer.presentation.services.MusicService

class MusicReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            context?.sendBroadcast(Intent(MusicService.RECEIVER_INTENT_FILTER).apply {
                putExtra(MusicService.AUDIO_KEY, intent.action)
            })
        }
    }

}