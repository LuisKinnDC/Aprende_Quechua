package com.app.aprendequechua.adapters

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class AvatarSpacingDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        // Agregar espaciado uniforme alrededor de cada ítem
        outRect.apply {
            left = spacing
            right = spacing
            top = spacing
            bottom = spacing
        }
    }
}