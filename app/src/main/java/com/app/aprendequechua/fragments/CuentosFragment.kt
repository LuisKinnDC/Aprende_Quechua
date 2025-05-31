package com.app.aprendequechua.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.app.aprendequechua.R

class CuentosFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var tvStoryTitle: TextView
    private lateinit var tvStoryAuthor: TextView
    private lateinit var tvStoryContent: TextView
    private lateinit var tvAuthorBio: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla el layout para este fragment
        val view = inflater.inflate(R.layout.fragment_cuentos, container, false)

        // Inicializa Firestore
        db = FirebaseFirestore.getInstance()

        // Referencias a las vistas
        tvStoryTitle = view.findViewById(R.id.tvStoryTitle)
        tvStoryAuthor = view.findViewById(R.id.tvStoryAuthor)
        tvStoryContent = view.findViewById(R.id.tvStoryContent)
        tvAuthorBio = view.findViewById(R.id.tvAuthorBio)

        // Cargar un cuento aleatorio desde Firestore
        loadRandomStory()

        return view
    }

    private fun loadRandomStory() {
        val cuentosCollection = db.collection("cuentos")
        cuentosCollection
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    // Seleccionar un cuento aleatorio
                    val allStories = querySnapshot.documents.mapNotNull { it.toObject(Story::class.java) }
                    val randomStory = allStories.randomOrNull()

                    if (randomStory != null) {
                        // Mostrar los detalles del cuento
                        tvStoryTitle.text = randomStory.titulo
                        tvStoryAuthor.text = "Por: ${randomStory.autor}"
                        tvStoryContent.text = randomStory.contenido
                        tvAuthorBio.text = randomStory.biografiaAutor
                    } else {
                        Toast.makeText(context, "No hay cuentos disponibles.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "No hay cuentos disponibles.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error al cargar los cuentos: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

data class Story(
    val titulo: String = "",
    val contenido: String = "",
    val autor: String = "Autor Desconocido",
    val biografiaAutor: String = "Información no disponible"
)