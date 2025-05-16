package com.app.aprendequechua.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.aprendequechua.R
import com.app.aprendequechua.adapters.DictionaryAdapter
import com.app.aprendequechua.models.Palabra
import com.google.firebase.firestore.FirebaseFirestore

class DiccionarioFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: DictionaryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_diccionario, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar Firebase
        db = FirebaseFirestore.getInstance()
        val searchInput = view.findViewById<EditText>(R.id.searchInput)
        val btnSearch = view.findViewById<ImageButton>(R.id.btnSearch)
        val recyclerResults = view.findViewById<RecyclerView>(R.id.recyclerResults)

        // Configurar RecyclerView
        adapter = DictionaryAdapter()
        recyclerResults.layoutManager = LinearLayoutManager(requireContext())
        recyclerResults.adapter = adapter

        // Configurar botón de búsqueda
        btnSearch.setOnClickListener {
            val query = searchInput.text.toString().trim()
            if (query.isNotEmpty()) {
                searchWord(query)
            }
        }
    }

    private fun searchWord(query: String) {
        db.collection("palabras")
            .whereEqualTo("palabraQuechua", query) // Filtrar por palabra en quechua
            .get()
            .addOnSuccessListener { documents ->
                val palabras = mutableListOf<Palabra>()
                for (document in documents) {
                    val palabra = document.toObject(Palabra::class.java)
                    palabras.add(palabra)
                }
                adapter.submitList(palabras) // Actualizar el RecyclerView
            }
            .addOnFailureListener { exception ->
                println("Error al buscar palabras: ${exception.message}")
            }
    }
}