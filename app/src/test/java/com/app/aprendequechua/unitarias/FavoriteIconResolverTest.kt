package com.app.aprendequechua.unitarias

import com.app.aprendequechua.utils.FavoriteIconResolver
import com.app.aprendequechua.R
import org.junit.Assert.assertEquals
import kotlin.test.Test

class FavoriteIconResolverTest {

    @Test
    fun `cuando es favorito retorna icono lleno`() {
        val icon = FavoriteIconResolver.getIconResource(true)
        assertEquals(R.drawable.ic_star_filled, icon)
    }

    @Test
    fun `cuando no es favorito retorna icono vacío`() {
        val icon = FavoriteIconResolver.getIconResource(false)
        assertEquals(R.drawable.ic_star_outline, icon)
    }
}