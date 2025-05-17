package com.app.aprendequechua.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.aprendequechua.R
import com.app.aprendequechua.adapters.DictionaryAdapter
import com.app.aprendequechua.models.Palabra
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class DiccionarioFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: DictionaryAdapter
    private var currentQuery = ""

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
        val recyclerResults = view.findViewById<RecyclerView>(R.id.recyclerResults)

        // Configurar RecyclerView
        adapter = DictionaryAdapter()
        recyclerResults.layoutManager = LinearLayoutManager(requireContext())
        recyclerResults.adapter = adapter

        // Configurar TextWatcher para la búsqueda en tiempo real
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query != currentQuery) {
                    currentQuery = query
                    if (query.isNotEmpty()) {
                        searchWord(query)
                    } else {
                        adapter.submitList(emptyList())
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun searchWord(query: String) {
        val queryLower = query.lowercase()

        db.collection("palabras")
            .orderBy("palabraQuechuaLower")
            .startAt(queryLower)
            .endAt("$queryLower\uf8ff")
            .limit(10)
            .get()
            .addOnSuccessListener { documents ->
                val palabras = mutableListOf<Palabra>()
                for (document in documents) {
                    val palabra = document.toObject(Palabra::class.java)
                    palabras.add(palabra)
                }
                adapter.submitList(palabras)
            }
            .addOnFailureListener { exception ->
                println("Error al buscar palabras: ${exception.message}")
            }
    }
}