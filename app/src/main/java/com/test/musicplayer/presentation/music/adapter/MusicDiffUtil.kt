package com.test.musicplayer.presentation.music.adapter

import androidx.recyclerview.widget.DiffUtil
import com.test.musicplayer.domain.Music

class MusicDiffUtil: DiffUtil.ItemCallback<Music>() {

    override fun areItemsTheSame(oldItem: Music, newItem: Music): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Music, newItem: Music): Boolean {
        return oldItem == newItem
    }

}