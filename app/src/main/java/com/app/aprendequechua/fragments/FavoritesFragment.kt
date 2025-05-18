package com.app.aprendequechua.fragments

import android.content.Intent
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



        // Configurar RecyclerView
        adapter = DictionaryAdapter(
            palabras = emptyList(),
            onAudioClick = { urlPronunciacion ->
                // Reproducir audio
            },
            onFavoriteClick = { palabra, isFavorite ->
                toggleFavorite(palabra, isFavorite)
            }
        )
        recyclerFavorites.layoutManager = LinearLayoutManager(requireContext())
        recyclerFavorites.adapter = adapter

        // Cargar palabras favoritas en tiempo real
        loadFavoritesInRealTime()
    }

    private fun loadFavoritesInRealTime() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("favoritos")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    // Manejar error
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val palabras = mutableListOf<Palabra>()
                    for (document in snapshot.documents) {
                        val palabra = document.toObject(Palabra::class.java)
                        palabra?.let {
                            it.isFavorite = true
                            palabras.add(it)
                        }
                    }
                    adapter.submitList(palabras)
                } else {
                    adapter.submitList(emptyList())
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

}