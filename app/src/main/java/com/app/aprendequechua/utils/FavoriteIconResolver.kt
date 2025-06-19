package com.app.aprendequechua.utils

import com.app.aprendequechua.R

object FavoriteIconResolver {
    fun getIconResource(isFavorite: Boolean): Int {
        return if (isFavorite) R.drawable.ic_star_filled else R.drawable.ic_star_outline
    }
}