package com.app.aprendequechua.parametrizadas

import com.app.aprendequechua.utils.LoginValidator
import com.app.aprendequechua.utils.LoginValidator.ValidationResult
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class LoginValidatorTest(
    private val email: String,
    private val password: String,
    private val expected: String
) {

    @Test
    fun validarLoginConDiferentesDatos() {
        val result = LoginValidator.validateLogin(email, password)

        when (result) {
            is ValidationResult.Success -> assertEquals("Success", expected)
            is ValidationResult.Error -> assertEquals(expected, result.message)
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "Email: {0}, Password: {1} => Esperado: {2}")
        fun datos(): Collection<Array<String>> = listOf(
            arrayOf("", "123456", "Este campo es obligatorio"),
            arrayOf("correo_invalido", "123456", "Correo inválido"),
            arrayOf("usuario@gmail.com", "", "Este campo es obligatorio"),
            arrayOf("usuario@gmail.com", "123", "Mínimo 6 caracteres"),
            arrayOf("usuario@gmail.com", "123456", "Success")
        )
    }
}
