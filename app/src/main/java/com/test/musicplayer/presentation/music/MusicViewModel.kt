package com.test.musicplayer.presentation.music

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.musicplayer.domain.AudioRepository
import com.test.musicplayer.domain.Music
import com.test.musicplayer.presentation.utils.AppDispatchers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MusicViewModel @Inject constructor(
    private val audioRepository: AudioRepository,
    private val appDispatchers: AppDispatchers
): ViewModel() {

    private val _state: MutableStateFlow<State> = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    fun getMusic() {
        audioRepository.getMusic()
            .flowOn(appDispatchers.io)
            .onEach { audio ->
                val last = state.value.music.toMutableList()
                last.add(audio)
                val current = last.toList()
                _state.update { it.copy(music = current) }
            }
            .flowOn(appDispatchers.main)
            .launchIn(viewModelScope)
    }


    data class State(
        val music: List<Music> = emptyList()
    )

}