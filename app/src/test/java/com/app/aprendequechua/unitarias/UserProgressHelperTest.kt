package com.app.aprendequechua.unitarias

import com.app.aprendequechua.utils.UserProgressHelper
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UserProgressHelperTest {

    @Test
    fun `debeResetearProgreso retorna true cuando fechas son diferentes`() {
        val result = UserProgressHelper.debeResetearProgreso("2025-06-01", "2025-06-03")
        assertTrue(result)
    }

    @Test
    fun `debeResetearProgreso retorna false cuando fechas son iguales`() {
        val result = UserProgressHelper.debeResetearProgreso("2025-06-03", "2025-06-03")
        assertFalse(result)
    }
}
