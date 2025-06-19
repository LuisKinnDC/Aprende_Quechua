package com.app.aprendequechua.fragments

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.app.aprendequechua.R
import com.google.firebase.firestore.FirebaseFirestore

class PronunciacionFragment : Fragment() {

    private lateinit var wordsContainer: LinearLayout
    private lateinit var btnPlay: ImageButton
    private lateinit var seekBar: SeekBar
    private lateinit var tvCurrentTime: TextView
    private lateinit var speedGroup: RadioGroup

    private var mediaPlayer: MediaPlayer? = null
    private var currentAudioUrl: String? = null
    private val handler = Handler(Looper.getMainLooper())
    private var playbackSpeed = 1.0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pronunciacion, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wordsContainer = view.findViewById(R.id.wordsContainer)
        btnPlay = view.findViewById(R.id.btnPlay)
        seekBar = view.findViewById(R.id.seekBarProgress)
        tvCurrentTime = view.findViewById(R.id.tvCurrentTime)
        speedGroup = view.findViewById(R.id.speedRadioGroup)

        fetchWordsFromFirebase()
        setupPlaybackControls()
        setupSpeedControl()
    }

    private fun fetchWordsFromFirebase() {
        val db = FirebaseFirestore.getInstance()

        db.collection("palabras")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val texto = document.getString("palabraQuechua") ?: continue
                    val audioUrl = document.getString("urlPronunciacion") ?: continue
                    addWordToUI(texto, audioUrl)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error al cargar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    @SuppressLint("ResourceType")
    private fun addWordToUI(texto: String, audioUrl: String) {
        val textView = TextView(requireContext()).apply {
            text = texto
            textSize = 20f
            setPadding(16, 16, 16, 16)
            setTextColor(resources.getColor(R.color.color_600, null))
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            isClickable = true
            isFocusable = true
        }

        textView.setOnClickListener {
            playAudioFromUrl(audioUrl)
        }

        wordsContainer.addView(textView)
    }

    private fun playAudioFromUrl(url: String) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            setOnPreparedListener {
                it.setPlaybackSpeed(playbackSpeed)
                seekBar.max = it.duration
                it.start()
                btnPlay.setImageResource(R.drawable.ic_pause)
                updateSeekBar()
            }
            setOnCompletionListener {
                btnPlay.setImageResource(R.drawable.ic_play)
                seekBar.progress = 0
                tvCurrentTime.text = "0:00"
            }
            prepareAsync()
        }
        currentAudioUrl = url
    }

    private fun setupPlaybackControls() {
        btnPlay.setOnClickListener {
            if (mediaPlayer == null && currentAudioUrl != null) {
                playAudioFromUrl(currentAudioUrl!!)
            } else {
                mediaPlayer?.let {
                    if (it.isPlaying) {
                        it.pause()
                        btnPlay.setImageResource(R.drawable.ic_play)
                    } else {
                        it.setPlaybackSpeed(playbackSpeed)
                        it.start()
                        btnPlay.setImageResource(R.drawable.ic_pause)
                        updateSeekBar()
                    }
                }
            }
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) mediaPlayer?.seekTo(progress)
            }

            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })
    }

    private fun updateSeekBar() {
        mediaPlayer?.let {
            seekBar.progress = it.currentPosition
            tvCurrentTime.text = formatTime(it.currentPosition)
            if (it.isPlaying) {
                handler.postDelayed({ updateSeekBar() }, 1000)
            }
        }
    }

    private fun formatTime(ms: Int): String {
        val seconds = ms / 1000
        val minutes = seconds / 60
        val secs = seconds % 60
        return String.format("%d:%02d", minutes, secs)
    }

    private fun setupSpeedControl() {
        speedGroup.setOnCheckedChangeListener { _, checkedId ->
            playbackSpeed = when (checkedId) {
                R.id.radioSlow -> 0.5f
                R.id.radioNormal -> 1.0f
                R.id.radioFast -> 1.5f
                else -> 1.0f
            }

            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.setPlaybackSpeed(playbackSpeed)
                }
            }
        }
    }

    // Extensión para velocidad
    private fun MediaPlayer.setPlaybackSpeed(speed: Float) {
        try {
            val playbackParams = this.playbackParams
            playbackParams.speed = speed
            this.playbackParams = playbackParams
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Este dispositivo no soporta velocidad modificada", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        mediaPlayer = null
        handler.removeCallbacksAndMessages(null)
    }
}
