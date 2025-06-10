package com.app.aprendequechua.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.gridlayout.widget.GridLayout
import com.app.aprendequechua.R
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random

data class Carta(val nombre: String = "", val imagenUrl: String = "")

class MemoramasFragment : Fragment() {

    private lateinit var gridLayout: GridLayout
    private lateinit var scoreTextView: TextView
    private lateinit var timerTextView: TextView
    private lateinit var restartButton: MaterialButton
    private lateinit var helpButton: MaterialButton

    private val db = FirebaseFirestore.getInstance()
    private var cartas: List<Carta> = listOf()
    private var cartasSeleccionadas = mutableListOf<View>()
    private var nombresSeleccionados = mutableListOf<String>()
    private var cartasDescubiertas = mutableSetOf<View>()
    private var puntaje = 0
    private var ayudaUsada = false
    private lateinit var countDownTimer: CountDownTimer
    private var tiempoRestante: Long = 60000 // 60 segundos

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_memoramas, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        gridLayout = view.findViewById(R.id.gameGrid)
        scoreTextView = view.findViewById(R.id.scoreTextView)
        timerTextView = view.findViewById(R.id.timerTextView)
        restartButton = view.findViewById(R.id.restartButton)
        helpButton = view.findViewById(R.id.helpButton)

        restartButton.setOnClickListener { reiniciarJuego() }
        helpButton.setOnClickListener { mostrarAyuda() }

        cargarCartasDesdeFirestore()
    }

    private fun cargarCartasDesdeFirestore() {
        db.collection("memoramas")
            .get()
            .addOnSuccessListener { result ->
                val listaOriginal = result.documents.mapNotNull { it.toObject(Carta::class.java) }.shuffled()
                if (listaOriginal.size >= 5) {
                    val pares = listaOriginal.take(4) // 4 cartas únicas
                    val comodin = listaOriginal[4]    // 1 carta impar

                    cartas = (pares + pares + listOf(comodin)).shuffled()
                    configurarGrid()
                    iniciarTemporizador()
                } else {
                    Toast.makeText(context, "Se requieren al menos 5 cartas", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al cargar datos", Toast.LENGTH_SHORT).show()
            }
    }


    private fun configurarGrid() {
        val totalCartas = cartas.size
        val (filas, columnas) = calcularFilasYColumnas(totalCartas)

        gridLayout.columnCount = columnas
        gridLayout.rowCount = filas

        gridLayout.post {
            gridLayout.removeAllViews()

            val totalWidth = gridLayout.width
            val totalHeight = gridLayout.height

            val cartaWidth = (totalWidth / columnas) - 16
            val cartaHeight = (totalHeight / filas) - 16
            val cartaSize = minOf(cartaWidth, cartaHeight) // para que sean cuadradas

            cartas.forEachIndexed { index, carta ->
                val cardView = layoutInflater.inflate(R.layout.card_memorama, gridLayout, false) as CardView
                val imageView = cardView.findViewById<ImageView>(R.id.imgCarta)
                val textView = cardView.findViewById<TextView>(R.id.txtNombreCarta)

                imageView.setImageResource(R.drawable.ic_card_back)
                textView.text = ""

                val params = GridLayout.LayoutParams().apply {
                    width = cartaSize
                    height = cartaSize
                    rowSpec = GridLayout.spec(index / columnas)
                    columnSpec = GridLayout.spec(index % columnas)
                    setMargins(8, 8, 8, 8)
                }

                cardView.layoutParams = params
                cardView.setOnClickListener {
                    if (cartasDescubiertas.contains(cardView) || cartasSeleccionadas.contains(cardView)) return@setOnClickListener
                    descubrirCarta(cardView, carta)
                }

                gridLayout.addView(cardView)
            }
        }
    }


    private fun descubrirCarta(cardView: View, carta: Carta) {
        val imageView = cardView.findViewById<ImageView>(R.id.imgCarta)
        val textView = cardView.findViewById<TextView>(R.id.txtNombreCarta)

        Glide.with(this).load(carta.imagenUrl).into(imageView)
        textView.text = carta.nombre

        cartasSeleccionadas.add(cardView)
        nombresSeleccionados.add(carta.nombre)

        if (cartasSeleccionadas.size == 2) {
            val esMatch = nombresSeleccionados[0] == nombresSeleccionados[1]
            gridLayout.postDelayed({
                if (esMatch) {
                    cartasDescubiertas.addAll(cartasSeleccionadas)
                    puntaje += 10
                    scoreTextView.text = "Puntaje: $puntaje"
                    if (cartasDescubiertas.size == cartas.size) {
                        Toast.makeText(context, "¡Ganaste!", Toast.LENGTH_LONG).show()
                    }
                } else {
                    cartasSeleccionadas.forEach {
                        val img = it.findViewById<ImageView>(R.id.imgCarta)
                        val txt = it.findViewById<TextView>(R.id.txtNombreCarta)
                        img.setImageResource(R.drawable.ic_card_back)
                        txt.text = ""
                    }
                }
                cartasSeleccionadas.clear()
                nombresSeleccionados.clear()
            }, 800)
        }
    }

    private fun mostrarAyuda() {
        if (ayudaUsada) return
        ayudaUsada = true

        for (i in 0 until gridLayout.childCount) {
            val cartaView = gridLayout.getChildAt(i)
            if (!cartasDescubiertas.contains(cartaView)) {
                val carta = cartas[i]
                val imageView = cartaView.findViewById<ImageView>(R.id.imgCarta)
                val textView = cartaView.findViewById<TextView>(R.id.txtNombreCarta)
                Glide.with(this).load(carta.imagenUrl).into(imageView)
                textView.text = carta.nombre
            }
        }

        gridLayout.postDelayed({
            for (i in 0 until gridLayout.childCount) {
                val cartaView = gridLayout.getChildAt(i)
                if (!cartasDescubiertas.contains(cartaView)) {
                    val imageView = cartaView.findViewById<ImageView>(R.id.imgCarta)
                    val textView = cartaView.findViewById<TextView>(R.id.txtNombreCarta)
                    imageView.setImageResource(R.drawable.ic_card_back)
                    textView.text = ""
                }
            }
        }, 2000) // Mostrar las cartas durante 2 segundos
    }

    private fun reiniciarJuego() {
        puntaje = 0
        ayudaUsada = false
        cartasDescubiertas.clear()
        scoreTextView.text = "Puntaje: 0"
        cartasSeleccionadas.clear()
        nombresSeleccionados.clear()
        tiempoRestante = 60000 // <-- importante
        countDownTimer.cancel()
        cargarCartasDesdeFirestore()
        iniciarTemporizador()
    }


    private fun iniciarTemporizador() {
        countDownTimer = object : CountDownTimer(tiempoRestante, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tiempoRestante = millisUntilFinished
                val segundos = (millisUntilFinished / 1000) % 60
                val minutos = (millisUntilFinished / 1000) / 60
                timerTextView.text = String.format("Tiempo: %02d:%02d", minutos, segundos)
            }

            override fun onFinish() {
                Toast.makeText(context, "¡Se acabó el tiempo!", Toast.LENGTH_LONG).show()
            }
        }.start()
    }

    private fun calcularFilasYColumnas(numCartas: Int): Pair<Int, Int> {
        var columnas = kotlin.math.sqrt(numCartas.toDouble()).toInt()
        if (columnas * columnas < numCartas) columnas++

        val filas = if (numCartas % columnas == 0) {
            numCartas / columnas
        } else {
            (numCartas / columnas) + 1
        }

        return Pair(filas, columnas)
    }

}
