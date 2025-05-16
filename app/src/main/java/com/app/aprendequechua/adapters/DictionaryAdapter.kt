package com.app.aprendequechua.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.aprendequechua.R
import com.app.aprendequechua.models.Palabra

class DictionaryAdapter : RecyclerView.Adapter<DictionaryAdapter.ViewHolder>() {

    private var palabras = mutableListOf<Palabra>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtOriginalWord: TextView = itemView.findViewById(R.id.txtOriginalWord)
        private val txtTranslation: TextView = itemView.findViewById(R.id.txtTranslation)
        private val txtExample: TextView = itemView.findViewById(R.id.txtExample)

        fun bind(palabra: Palabra) {
            txtOriginalWord.text = palabra.palabraQuechua
            txtTranslation.text = palabra.traduccionEspanol
            txtExample.text = palabra.ejemploQuechua
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dictionary_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(palabras[position])
    }

    override fun getItemCount(): Int = palabras.size

    fun submitList(newList: List<Palabra>) {
        palabras.clear()
        palabras.addAll(newList)
        notifyDataSetChanged()
    }
}