package com.test.musicplayer.presentation.services

import android.app.Notification.MediaStyle
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.test.musicplayer.R
import com.test.musicplayer.domain.Music
import com.test.musicplayer.presentation.receivers.MusicReceiver

class MusicService: Service() {

    private val binder = LocalBinder()
    private var mediaPlayer = MediaPlayer()
    private var musicServiceCallback: MusicServiceCallback? = null
    private var music: List<Music> = emptyList()
    private var currentId: Int = -1
    private val onCompletionListener = MediaPlayer.OnCompletionListener { next() }
    private val mediaSessionCompat: MediaSessionCompat by lazy {
        MediaSessionCompat(applicationContext, "tag")
    }
    private val playPauseDrawable: Int
        get() {
            return if (mediaPlayer.isPlaying)
                android.R.drawable.ic_media_pause
            else
                android.R.drawable.ic_media_play
        }

    private val musicReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.extras?.getString(AUDIO_KEY)
            when (action) {
                ACTION_NEXT -> {
                    next()
                }
                ACTION_PREV -> {
                    prev()
                }
                ACTION_PLAY -> {
                    pause()
                }
                ACTION_STOP -> {
                    stop()
                }
            }
        }

    }

    private val pendingIntentPrevious by lazy {
        PendingIntent.getBroadcast(
            applicationContext,
            0,
            Intent(this, MusicReceiver::class.java).apply {
                setAction(ACTION_PREV)
            },
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    private val pendingIntentNext by lazy {
        PendingIntent.getBroadcast(
            applicationContext,
            0,
            Intent(this, MusicReceiver::class.java).apply {
                setAction(ACTION_NEXT)
            },
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    private val pendingIntentPlay by lazy {
        PendingIntent.getBroadcast(
            applicationContext,
            0,
            Intent(this, MusicReceiver::class.java).apply {
                setAction(ACTION_PLAY)
            },
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    private val pendingIntentClose by lazy {
        PendingIntent.getBroadcast(
            applicationContext,
            0,
            Intent(this, MusicReceiver::class.java).apply {
                setAction(ACTION_STOP)
            },
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChanel()
        registerReceiver(musicReceiver, IntentFilter(RECEIVER_INTENT_FILTER))
    }

    override fun onDestroy() {
        unregisterReceiver(musicReceiver)
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
        val id = this.music.indexOf(music)
        onPlayStart(id)
    }

    fun stop() {
        mediaPlayer.stop()
        mediaPlayer.release()
        musicServiceCallback?.onStop()
        currentId = -1
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    fun setupServiceCallback(callback: MusicServiceCallback?) {
        musicServiceCallback = callback
    }

    fun setupMusic(music: List<Music>) {
        this.music = music
    }

    fun next() {
        val nextIndex = if (currentId == music.lastIndex) 0 else currentId + 1
        onPlayStart(nextIndex)
    }

    fun prev() {
        val prevIndex = if (currentId == 0) music.lastIndex else currentId - 1
       onPlayStart(prevIndex)
    }

    fun pause() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        } else {
            mediaPlayer.start()
        }
        musicServiceCallback?.onPause(mediaPlayer.isPlaying.not())
        startForeground(NOTIFICATION_ID, createNotification(music[currentId].title))
    }

    private fun onPlayStart(newId: Int) {
        mediaPlayer.release()
        mediaPlayer = MediaPlayer.create(this, music[newId].path.toUri())
        mediaPlayer.start()
        currentId = newId
        mediaPlayer.setOnCompletionListener(onCompletionListener)
        musicServiceCallback?.onTrackChange(music[newId])
        startForeground(NOTIFICATION_ID, createNotification(music[newId].title))
    }

    private fun createNotification(name: String) = NotificationCompat.Builder(this, CHANNEL_ONE)
        .setSilent(true)
        .setContentTitle(name)
        .setContentText("")
        .setSmallIcon(R.drawable.ic_music)
        .addAction(android.R.drawable.ic_media_previous, "Previous", pendingIntentPrevious)
        .addAction(playPauseDrawable, "Play", pendingIntentPlay)
        .addAction(android.R.drawable.ic_media_next, "Next", pendingIntentNext)
        .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Close", pendingIntentClose)
        .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
            .setShowActionsInCompactView(0,1,2,3)
            .setMediaSession(mediaSessionCompat.sessionToken)
        )
        .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
        .build()

    companion object {
        private const val NOTIFICATION_ID = 123
        private const val CHANNEL_ONE = "CHANNEL_ONE"
        private const val CHANNEL_NAME = "Music"
        private const val ACTION_PREV = "action_prev"
        private const val ACTION_NEXT = "action_next"
        private const val ACTION_STOP = "action_stop"
        private const val ACTION_PLAY = "action_play"
        const val RECEIVER_INTENT_FILTER = "RECEIVER_INTENT_FILTER"
        const val AUDIO_KEY = "audio_key"
    }

}