package com.app.aprendequechua.unitarias

import com.app.aprendequechua.utils.PalabraUtils
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import kotlin.test.Test

class PalabraUtilsTest{

 @Test
fun testToggleFavorite() {
    assertTrue(PalabraUtils.toggleFavorite(false))
    assertFalse(PalabraUtils.toggleFavorite(true))
}
}