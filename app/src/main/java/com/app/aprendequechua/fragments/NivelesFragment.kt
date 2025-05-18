package com.app.aprendequechua.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.aprendequechua.R
import com.app.aprendequechua.adapters.NivelAdapter
import com.app.aprendequechua.models.Nivel
import com.google.firebase.firestore.FirebaseFirestore

class NivelesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NivelAdapter
    private val nivelesList = mutableListOf<Nivel>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_niveles, container, false)

        recyclerView = view.findViewById(R.id.recyclerNiveles)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = NivelAdapter(nivelesList) { nivel ->
            // Aquí puedes navegar a otro fragmento con las lecciones del nivel
            // Por ahora, solo mostrar log o mensaje
            println("Nivel seleccionado: ${nivel.nombre}")
        }
        recyclerView.adapter = adapter

        cargarNiveles()

        return view
    }

    private fun cargarNiveles() {
        db.collection("niveles")
            .get()
            .addOnSuccessListener { snapshot ->
                nivelesList.clear()
                for (doc in snapshot.documents) {
                    val nivel = Nivel(
                        id = doc.id,
                        nombre = doc.getString("nombre") ?: "",
                        descripcion = doc.getString("descripcion") ?: "",
                        imagenUrl = doc.getString("imagenUrl") ?: ""
                    )
                    nivelesList.add(nivel)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                // manejar error
            }
    }


}
