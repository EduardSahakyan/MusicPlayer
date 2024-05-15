package com.test.musicplayer.data

import android.content.Context
import android.provider.MediaStore
import com.test.musicplayer.domain.AudioRepository
import com.test.musicplayer.domain.Music
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AudioRepositoryImpl(
    private val context: Context
): AudioRepository {

    override fun getMusic(): Flow<Music> {
        return flow {
            val cursor = context.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null)
            cursor?.use {
                while (cursor.moveToNext()) {
                    val audioTitle = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                    val audioDuration =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                    val audioId = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                    val audioData = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                    val title = cursor.getString(audioTitle)
                    val duration = cursor.getInt(audioDuration)
                    val id = cursor.getInt(audioId)
                    val data = cursor.getString(audioData)
                    val music = Music(id, title, duration, data)
                    emit(music)
                }
            }
        }
    }

}