package com.app.aprendequechua.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.aprendequechua.R
import com.app.aprendequechua.models.Nivel
import com.bumptech.glide.Glide

class NivelAdapter(
    private val niveles: List<Nivel>,
    private val onClick: (Nivel) -> Unit
) : RecyclerView.Adapter<NivelAdapter.NivelViewHolder>() {

    class NivelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNombreNivel: TextView = itemView.findViewById(R.id.txtNombreNivel)
        val txtDescripcionNivel: TextView = itemView.findViewById(R.id.txtDescripcionNivel)
        val imgNivel: ImageView = itemView.findViewById(R.id.imgNivel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NivelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_nivel, parent, false)
        return NivelViewHolder(view)
    }

    override fun onBindViewHolder(holder: NivelViewHolder, position: Int) {
        val nivel = niveles[position]
        holder.txtNombreNivel.text = nivel.nombre
        holder.txtDescripcionNivel.text = nivel.descripcion

        Glide.with(holder.itemView.context)
            .load(nivel.imagenUrl)
            .into(holder.imgNivel)

        holder.itemView.setOnClickListener {
            onClick(nivel)
        }
    }

    override fun getItemCount(): Int = niveles.size
}
