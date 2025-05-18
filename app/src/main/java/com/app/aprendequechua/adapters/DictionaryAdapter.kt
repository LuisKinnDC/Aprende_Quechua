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
) : RecyclerView.Adapter<DictionaryAdapter.WordViewHolder>() {

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

            // Configurar favorito
            btnFavorite.setImageResource(
                if (isFavorite) R.drawable.ic_star_filled else R.drawable.ic_star_outline)
            btnFavorite.setOnClickListener {
                onFavoriteClick(palabra, !isFavorite)
            }

            // Configurar audio
            btnAudio.setOnClickListener {
                onAudioClick(palabra.urlPronunciacion)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dictionary_result, parent, false)
        return WordViewHolder(view)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val palabra = palabras[position]
        holder.bind(palabra, palabra.isFavorite)
    }

    override fun getItemCount(): Int = palabras.size

    fun submitList(newList: List<Palabra>) {
        palabras = newList
        notifyDataSetChanged()
    }
}