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

class EjerciciosBasicosFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var containerEjercicios: LinearLayout
    private val idLeccion = "L01" // Esto debería llegar dinámicamente más adelante

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_ejercicios_basicos, container, false)

        view.findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        containerEjercicios = view.findViewById(R.id.containerEjercicios)
        db = FirebaseFirestore.getInstance()

        cargarEjerciciosDesdeFirestore(idLeccion, inflater)

        return view
    }

    private fun cargarEjerciciosDesdeFirestore(idLeccion: String, inflater: LayoutInflater) {
        db.collection("lecciones_basico")
            .document(idLeccion)
            .collection("ejercicios")
            .get()
            .addOnSuccessListener { docs ->
                for (doc in docs) {
                    val tipo = doc.getString("tipo") ?: continue

                    when (tipo) {
                        "teoria" -> {
                            val cardTeoria = inflater.inflate(R.layout.item_ejercicio_teoria, containerEjercicios, false)
                            val txtTitulo = cardTeoria.findViewById<TextView>(R.id.txtTituloTeoria)
                            val txtContenido = cardTeoria.findViewById<TextView>(R.id.txtContenidoTeoria)
                            val imgTeoria = cardTeoria.findViewById<ImageView>(R.id.imgTeoria)

                            txtTitulo.text = doc.getString("titulo") ?: ""
                            txtContenido.text = doc.getString("contenido") ?: ""

                            val imagenNombre = doc.getString("imagen")
                            if (!imagenNombre.isNullOrEmpty()) {
                                val resId = resources.getIdentifier(imagenNombre, "drawable", requireContext().packageName)
                                if (resId != 0) {
                                    imgTeoria.setImageResource(resId)
                                    imgTeoria.visibility = View.VISIBLE
                                }
                            }

                            containerEjercicios.addView(cardTeoria)
                        }

                        "quiz" -> {
                            val cardQuiz = inflater.inflate(R.layout.item_ejercicio_quiz, containerEjercicios, false)
                            val txtPregunta = cardQuiz.findViewById<TextView>(R.id.txtPreguntaQuiz)
                            val radioGroup = cardQuiz.findViewById<RadioGroup>(R.id.radioGroupOpciones)
                            val btnComprobar = cardQuiz.findViewById<Button>(R.id.btnComprobar)

                            txtPregunta.text = doc.getString("pregunta") ?: ""
                            val opciones = doc.get("opciones") as? List<*>
                            val correcta = doc.getString("respuesta_correcta")

                            opciones?.forEach { opcion ->
                                val radioButton = RadioButton(requireContext())
                                radioButton.text = opcion.toString()
                                radioButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_950))
                                radioGroup.addView(radioButton)
                            }

                            btnComprobar.setOnClickListener {
                                val selectedId = radioGroup.checkedRadioButtonId
                                if (selectedId != -1) {
                                    val rbSeleccionado = radioGroup.findViewById<RadioButton>(selectedId)
                                    val texto = rbSeleccionado.text.toString()
                                    if (texto == correcta) {
                                        Toast.makeText(requireContext(), "¡Correcto!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(requireContext(), "Incorrecto", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }

                            containerEjercicios.addView(cardQuiz)
                        }
                    }
                }

                // ✅ Agrega botón de finalizar
                agregarBotonFinalizar()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al cargar ejercicios", Toast.LENGTH_SHORT).show()
            }
    }

    private fun agregarBotonFinalizar() {
        val btnFinalizar = MaterialButton(requireContext()).apply {
            text = "Finalizar lección"
            setTextColor(ContextCompat.getColor(context, R.color.white))
            backgroundTintList = ContextCompat.getColorStateList(context, R.color.color_600)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 32
            }
            setOnClickListener {
                finalizarLeccion(idLeccion)
            }
        }
        containerEjercicios.addView(btnFinalizar)
    }

    private fun finalizarLeccion(leccionId: String) {
        val leccionRef = db.collection("lecciones_basico")

        leccionRef.document(leccionId)
            .update("completado", true)
            .addOnSuccessListener {
                val siguienteId = calcularSiguienteLeccion(leccionId)
                if (siguienteId != null) {
                    leccionRef.document(siguienteId)
                        .update("completado", true)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "¡Lección finalizada! Se desbloqueó la siguiente.", Toast.LENGTH_SHORT).show()
                            irAFragmentoDeLecciones()
                        }
                } else {
                    Toast.makeText(requireContext(), "¡Has completado todas las lecciones!", Toast.LENGTH_LONG).show()
                    irAFragmentoDeLecciones()
                }
            }
    }

    private fun calcularSiguienteLeccion(leccionId: String): String? {
        val numero = leccionId.removePrefix("L").toIntOrNull()
        return if (numero != null && numero < 8) {
            "L" + String.format("%02d", numero + 1)
        } else null
    }

    private fun irAFragmentoDeLecciones() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, LeccionFragment()) // Asegúrate que sea el ID correcto
            .commit()
    }
}
