package com.test.musicplayer.presentation.music.adapter

import com.test.musicplayer.domain.Music

interface MusicAdapterCallback {

    fun onMusicClick(music: Music)

}