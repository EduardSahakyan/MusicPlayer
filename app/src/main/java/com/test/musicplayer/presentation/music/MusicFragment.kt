package com.test.musicplayer.presentation.music

import android.content.ComponentName
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.test.musicplayer.databinding.FragmentMusicBinding
import com.test.musicplayer.domain.Music
import com.test.musicplayer.presentation.music.adapter.MusicAdapterCallback
import com.test.musicplayer.presentation.music.adapter.MusicListAdapter
import com.test.musicplayer.presentation.services.MusicService
import com.test.musicplayer.presentation.services.MusicServiceCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class MusicFragment: Fragment() {

    private var _binding: FragmentMusicBinding? = null
    private val binding: FragmentMusicBinding
        get() = _binding ?: throw IllegalStateException("FragmentMusicBinding is null")

    private val viewModel: MusicViewModel by viewModels()
    private var currentMusic: Music? = null

    private var mService: MusicService? = null
    private var mBound: Boolean = false

    private val musicServiceCallback = object : MusicServiceCallback {
        override fun onStop() {
            binding.musicStatusBar.visibility = View.GONE
        }

        override fun onPause(isPaused: Boolean) {
            if (isPaused) {
                binding.playPauseBtn.setImageDrawable(ContextCompat.getDrawable(requireContext(), android.R.drawable.ic_media_play))
            } else {
                binding.playPauseBtn.setImageDrawable(ContextCompat.getDrawable(requireContext(), android.R.drawable.ic_media_pause))
            }
        }

        override fun onTrackChange(music: Music) {
            binding.apply {
                musicStatusBar.visibility = View.VISIBLE
                songTitle.text = music.title
            }
        }
    }

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as MusicService.LocalBinder
            mService = binder.getService()
            mBound = true
            mService?.setupServiceCallback(musicServiceCallback)
            mService?.setupMusic(viewModel.state.value.music)
            currentMusic?.let {
                mService?.play(it)
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mService?.setupServiceCallback(null)
            mService = null
            mBound = false
        }
    }


    private val musicAdapterCallback = object : MusicAdapterCallback {
        override fun onMusicClick(music: Music) {
            currentMusic = music
            if (mBound) {
                mService?.play(music)
            } else {
                val intent = Intent(requireContext(), MusicService::class.java)
                requireContext().bindService(intent, connection, BIND_AUTO_CREATE)
            }
        }
    }
    private val musicAdapter = MusicListAdapter(musicAdapterCallback)

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            viewModel.getMusic()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMusicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listeners()
        setupAdapter()
        observers()
        permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun observers() {
        viewModel.state
            .onEach {
                musicAdapter.submitList(it.music)
            }
            .launchIn(lifecycleScope)
    }

    private fun setupAdapter() {
        binding.apply {
            rvMusic.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rvMusic.adapter = musicAdapter
        }
    }

    private fun listeners() = with(binding) {
        closeAudioBtn.setOnClickListener {
            mService?.stop()
        }
        nextBtn.setOnClickListener {
            mService?.next()
        }
        prvBtn.setOnClickListener {
            mService?.prev()
        }
        playPauseBtn.setOnClickListener {
            mService?.pause()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}