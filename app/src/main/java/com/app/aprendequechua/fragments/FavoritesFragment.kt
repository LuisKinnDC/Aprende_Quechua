package com.app.aprendequechua.fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.aprendequechua.R
import com.app.aprendequechua.adapters.DictionaryAdapter
import com.app.aprendequechua.models.Palabra
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FavoritesFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: DictionaryAdapter
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar Firebase
        db = FirebaseFirestore.getInstance()

        // Referencias a las vistas
        val recyclerFavorites = view.findViewById<RecyclerView>(R.id.recyclerFavorites)
        val layoutVolver = view.findViewById<LinearLayout>(R.id.layoutVolver)

        layoutVolver.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, DiccionarioFragment())
                .addToBackStack(null)
                .commit()
        }

        // Configurar RecyclerView y Adapter
        adapter = DictionaryAdapter(
            palabras = emptyList(),
            onAudioClick = { urlPronunciacion ->
                playAudioFromUrl(urlPronunciacion)
            },
            onFavoriteClick = { palabra, isFavorite ->
                toggleFavorite(palabra, isFavorite)
            }
        )

        recyclerFavorites.layoutManager = LinearLayoutManager(requireContext())
        recyclerFavorites.adapter = adapter

        // Cargar favoritos en tiempo real
        loadFavoritesInRealTime()
    }

    private fun loadFavoritesInRealTime() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("favoritos")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Toast.makeText(requireContext(), "Error al cargar favoritos", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                val palabras = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(Palabra::class.java)?.apply {
                        isFavorite = true
                    }
                } ?: emptyList()

                adapter.submitList(palabras)
            }
    }

    fun toggleFavorite(palabra: Palabra, isFavorite: Boolean) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val favoritosRef = db.collection("favoritos").document("${userId}_${palabra.palabraQuechuaLower}")

        if (isFavorite) {
            // Agregar a favoritos
            favoritosRef.set(
                mapOf(
                    "userId" to userId,
                    "palabraId" to palabra.palabraQuechuaLower,
                    "palabraQuechua" to palabra.palabraQuechua,
                    "significado" to palabra.significado,
                    "ejemploQuechua" to palabra.ejemploQuechua,
                    "ejemploEspanol" to palabra.ejemploEspanol,
                    "urlPronunciacion" to palabra.urlPronunciacion
                )
            ).addOnSuccessListener {
                palabra.isFavorite = true
                adapter.notifyDataSetChanged()
                Toast.makeText(requireContext(), "${palabra.palabraQuechua} agregado a Favoritos", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Eliminar de favoritos
            favoritosRef.delete().addOnSuccessListener {
                palabra.isFavorite = false
                adapter.notifyDataSetChanged()
                Toast.makeText(requireContext(), "${palabra.palabraQuechua} eliminado de Favoritos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Reproducir audio desde URL pública
    private fun playAudioFromUrl(audioUrl: String) {
        releaseMediaPlayer()

        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(audioUrl)
                prepareAsync()

                setOnPreparedListener {
                    start()
                }

                setOnCompletionListener {
                    releaseMediaPlayer()
                }

                setOnErrorListener { mp, what, extra ->
                    Toast.makeText(requireContext(), "Error al reproducir audio", Toast.LENGTH_SHORT).show()
                    releaseMediaPlayer()
                    true
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "No se pudo reproducir el audio", Toast.LENGTH_SHORT).show()
                releaseMediaPlayer()
            }
        }
    }

    // Liberar recursos del MediaPlayer
    private fun releaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

}