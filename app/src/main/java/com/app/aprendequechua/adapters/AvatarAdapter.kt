package com.app.aprendequechua.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.app.aprendequechua.R
import com.bumptech.glide.Glide

class AvatarAdapter(
    private val avatars: List<String>,
    private val onAvatarSelected: (String) -> Unit
) : RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_avatar, parent, false)
        return AvatarViewHolder(view)
    }

    override fun onBindViewHolder(holder: AvatarViewHolder, position: Int) {
        val avatarUrl = avatars[position]
        holder.bind(avatarUrl)
    }

    override fun getItemCount(): Int = avatars.size

    inner class AvatarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageViewAvatar)

        fun bind(avatarUrl: String) {
            Glide.with(itemView.context)
                .load(avatarUrl)
                .placeholder(R.drawable.ic_defect_profile)
                .into(imageView)

            itemView.setOnClickListener {
                onAvatarSelected(avatarUrl)
            }
        }
    }
}