package com.test.musicplayer.presentation.services

import com.test.musicplayer.domain.Music

interface MusicServiceCallback {

    fun onStop()

    fun onPause(isPaused: Boolean)

    fun onTrackChange(music: Music)

}