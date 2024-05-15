package com.test.musicplayer.presentation.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.test.musicplayer.R
import com.test.musicplayer.domain.Music

class MusicService: Service() {

    private val binder = LocalBinder()
    private var mediaPlayer = MediaPlayer()

    override fun onCreate() {
        super.onCreate()
        createNotificationChanel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    inner class LocalBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    private fun createNotificationChanel(){
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ONE, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH,
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    fun play(music: Music) {
        startForeground(NOTIFICATION_ID, createNotification(music.title))
        mediaPlayer.release()
        mediaPlayer = MediaPlayer.create(this, music.path.toUri())
        mediaPlayer.start()
    }

    private fun createNotification(name: String) = NotificationCompat.Builder(this, CHANNEL_ONE)
        .setSilent(true)
        .setContentTitle(name)
        .setContentText("")
        .setSmallIcon(R.drawable.ic_music)
/*        .addAction(android.R.drawable.ic_media_previous, "Previous", pendingIntentPrevious)
        .addAction(playOrPauseDrawable, "Play", pendingIntentPlay)
        .addAction(android.R.drawable.ic_media_next, "Next", pendingIntentNext)
        .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Close", pendingIntentClose)
        .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
            .setShowActionsInCompactView(0,1,2,3)
            .setMediaSession(mediaSessionCompat.sessionToken)
        )*/
        .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
        .build()

    companion object {
        private const val NOTIFICATION_ID = 123
        private const val CHANNEL_ONE = "CHANNEL_ONE"
        private const val CHANNEL_NAME = "Music"
    }

}