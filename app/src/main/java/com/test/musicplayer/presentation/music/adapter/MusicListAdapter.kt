package com.test.musicplayer.presentation.music.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.test.musicplayer.databinding.ItemMusicBinding
import com.test.musicplayer.domain.Music

class MusicListAdapter(
    private val musicAdapterCallback: MusicAdapterCallback
): ListAdapter<Music, MusicListAdapter.MusicViewHolder>(MusicDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val binding = ItemMusicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MusicViewHolder(binding, musicAdapterCallback)
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MusicViewHolder(
        private val binding: ItemMusicBinding,
        private val musicAdapterCallback: MusicAdapterCallback
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(music: Music) {
            binding.apply {
                songName.text = music.title
                val minutes: Int = (music.duration/1000) / 60
                val seconds: Int = (music.duration/1000) % 60
                var strTemp = if (minutes < 10) "0$minutes:" else "$minutes:"
                strTemp = if (seconds < 10) strTemp + "0" + seconds.toString() else strTemp + seconds.toString()
                duration.text = strTemp
                root.setOnClickListener {
                    musicAdapterCallback.onMusicClick(music)
                }
            }
        }

    }

}