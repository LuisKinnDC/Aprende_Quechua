package com.app.aprendequechua.fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.aprendequechua.R
import com.app.aprendequechua.adapters.DictionaryAdapter
import com.app.aprendequechua.models.Palabra
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DiccionarioFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: DictionaryAdapter
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_diccionario, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        val searchInput = view.findViewById<EditText>(R.id.searchInput)
        val recyclerResults = view.findViewById<RecyclerView>(R.id.recyclerResults)
        val ListfabFavorites = view.findViewById<View>(R.id.listfabFavorites)

        // Inicializar el adaptador con callbacks
        adapter = DictionaryAdapter(
            palabras = emptyList(),
            onAudioClick = { urlPronunciacion ->
                playAudioFromUrl(urlPronunciacion)
            },
            onFavoriteClick = { palabra, isFavorite ->
                toggleFavorite(palabra, isFavorite)
            }
        )

        recyclerResults.layoutManager = LinearLayoutManager(requireContext())
        recyclerResults.adapter = adapter

        // Configurar el botón flotante para navegar a la pantalla de favoritos
        ListfabFavorites.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, FavoritesFragment())
                .addToBackStack(null)
                .commit()
        }

        // Búsqueda en tiempo real
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    filterWords(query)
                } else {
                    adapter.submitList(emptyList())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterWords(query: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // Obtener favoritos del usuario
        db.collection("favoritos")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { favoritosSnapshot ->
                val favoritosIds = favoritosSnapshot.documents.mapNotNull { it.getString("palabraId") }

                // Luego obtener todas las palabras
                db.collection("palabras")
                    .get()
                    .addOnSuccessListener { snapshot ->
                        val palabrasFiltradas = snapshot.documents.mapNotNull { it.toObject(Palabra::class.java) }
                            .filter {
                                it.palabraQuechuaLower.contains(query.lowercase()) ||
                                        it.significado.lowercase().contains(query.lowercase())
                            }
                            .map { palabra ->
                                palabra.isFavorite = favoritosIds.contains(palabra.palabraQuechuaLower)
                                palabra
                            }

                        adapter.submitList(palabrasFiltradas)
                    }
                    .addOnFailureListener {
                        adapter.submitList(emptyList())
                        Toast.makeText(requireContext(), "Error al cargar palabras", Toast.LENGTH_SHORT).show()
                    }
            }
    }

    private fun toggleFavorite(palabra: Palabra, isFavorite: Boolean) {
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

    // Función para reproducir audio desde URL pública (GitHub Pages)
    private fun playAudioFromUrl(audioUrl: String) {
        releaseMediaPlayer() // Liberar si ya hay uno activo

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
                    Toast.makeText(requireContext(), "Error al reproducir el audio", Toast.LENGTH_SHORT).show()
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