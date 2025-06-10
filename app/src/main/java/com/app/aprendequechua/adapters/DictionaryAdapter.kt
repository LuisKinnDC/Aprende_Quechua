package com.app.aprendequechua.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.aprendequechua.R
import com.app.aprendequechua.models.Palabra

class DictionaryAdapter(
    private var palabras: List<Palabra>,
    private val onAudioClick: (String) -> Unit,
    private val onFavoriteClick: (Palabra, Boolean) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_EMPTY = 0
        private const val TYPE_ITEM = 1
    }

    // ViewHolder para items normales
    inner class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtPalabraQuechua: TextView = itemView.findViewById(R.id.txtPalabraQuechua)
        private val txtSignificado: TextView = itemView.findViewById(R.id.txtSignificado)
        private val txtEjemploQuechua: TextView = itemView.findViewById(R.id.txtEjemploQuechua)
        private val txtEjemploEspanol: TextView = itemView.findViewById(R.id.txtEjemploEspanol)
        private val btnFavorite: ImageButton = itemView.findViewById(R.id.ImgBtnFav)
        private val btnAudio: ImageButton = itemView.findViewById(R.id.btnUrl_pronunciacion)

        fun bind(palabra: Palabra, isFavorite: Boolean) {
            txtPalabraQuechua.text = palabra.palabraQuechua
            txtSignificado.text = palabra.significado
            txtEjemploQuechua.text = palabra.ejemploQuechua
            txtEjemploEspanol.text = palabra.ejemploEspanol

            btnFavorite.setImageResource(
                if (isFavorite) R.drawable.ic_star_filled else R.drawable.ic_star_outline)
            btnFavorite.setOnClickListener {
                onFavoriteClick(palabra, !isFavorite)
            }

            btnAudio.setOnClickListener {
                palabra.urlPronunciacion?.let { url -> onAudioClick(url) }
            }
        }
    }

    // ViewHolder para estado vacío
    inner class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Puedes agregar referencias a los elementos del layout de estado vacío si necesitas interactuar con ellos
    }

    override fun getItemViewType(position: Int): Int {
        return if (palabras.isEmpty()) TYPE_EMPTY else TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_EMPTY -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_empty_state, parent, false)
                EmptyViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_dictionary_result, parent, false)
                WordViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is WordViewHolder -> {
                val palabra = palabras[position]
                holder.bind(palabra, palabra.isFavorite)
            }
            is EmptyViewHolder -> {
                // Puedes configurar elementos del estado vacío aquí si es necesario
            }
        }
    }

    override fun getItemCount(): Int {
        return if (palabras.isEmpty()) 1 else palabras.size
    }

    fun submitList(newList: List<Palabra>) {
        palabras = newList
        notifyDataSetChanged()
    }
}