package com.app.aprendequechua.unitarias

import com.app.aprendequechua.utils.FavoriteToggleHelper
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import kotlin.test.Test

class FavoriteToggleHelperTest {
    @Test
    fun `cuando es favorito se convierte en no favorito`() {
        val resultado = FavoriteToggleHelper.toggle(true)
        assertFalse(resultado)
    }

    @Test
    fun `cuando no es favorito se convierte en favorito`() {
        val resultado = FavoriteToggleHelper.toggle(false)
        assertTrue(resultado)
    }
}