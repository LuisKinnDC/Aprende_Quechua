package com.app.aprendequechua.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.app.aprendequechua.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore

class LeccionAvanzadoFragment : Fragment() {

    private lateinit var containerLecciones: LinearLayout
    private lateinit var db: FirebaseFirestore

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_leccion, container, false)

        view.findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        containerLecciones = view.findViewById(R.id.containerLecciones)
        db = FirebaseFirestore.getInstance()

        cargarLeccionesDesdeFirestore(inflater)

        return view
    }

    private fun cargarLeccionesDesdeFirestore(inflater: LayoutInflater) {
        db.collection("lecciones_avanzado").get()
            .addOnSuccessListener { documentos ->
                for (doc in documentos) {
                    val idLeccion = doc.id
                    val titulo = doc.getString("titulo") ?: "Sin título"
                    val descripcion = doc.getString("descripcion") ?: "Sin descripción"
                    val icono = doc.getString("icono") ?: "ic_lesson"
                    val completado = doc.getBoolean("completado") ?: false

                    val itemView = inflater.inflate(R.layout.item_leccion, containerLecciones, false)

                    val txtTitulo = itemView.findViewById<TextView>(R.id.txtTituloLeccion)
                    val txtDescripcion = itemView.findViewById<TextView>(R.id.txtDescripcionLeccion)
                    val imgIcono = itemView.findViewById<ImageView>(R.id.imgIconoLeccion)
                    val imgEstado = itemView.findViewById<ImageView>(R.id.imgEstadoLeccion)
                    val btnPracticar = itemView.findViewById<MaterialButton>(R.id.btnPracticar)

                    txtTitulo.text = titulo
                    txtDescripcion.text = descripcion

                    val resId = resources.getIdentifier(icono, "drawable", requireContext().packageName)
                    if (resId != 0) {
                        imgIcono.setImageResource(resId)
                    } else {
                        imgIcono.setImageResource(R.drawable.ic_lesson_1)
                    }

                    if (completado) {
                        imgEstado.setImageResource(R.drawable.ic_check_circle)
                        btnPracticar.isEnabled = true
                        btnPracticar.text = "Practicar"
                        btnPracticar.setBackgroundTintList(
                            ContextCompat.getColorStateList(requireContext(), R.color.color_600)
                        )
                    } else {
                        imgEstado.setImageResource(R.drawable.ic_lock)
                        btnPracticar.isEnabled = false
                        btnPracticar.text = "Bloqueado"
                        btnPracticar.setBackgroundTintList(
                            ContextCompat.getColorStateList(requireContext(), R.color.color_300)
                        )
                    }

                    btnPracticar.setOnClickListener {
                        val fragment = EjerciciosAvanzadoFragment().apply {
                            arguments = Bundle().apply {
                                putString("idLeccion", idLeccion)
                            }
                        }

                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainer, fragment)
                            .addToBackStack(null)
                            .commit()
                    }

                    containerLecciones.addView(itemView)
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al cargar lecciones avanzadas", Toast.LENGTH_SHORT).show()
            }
    }
}
