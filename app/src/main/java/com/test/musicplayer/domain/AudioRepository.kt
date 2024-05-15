package com.test.musicplayer.domain

import kotlinx.coroutines.flow.Flow

interface AudioRepository {

    fun getMusic(): Flow<Music>

}