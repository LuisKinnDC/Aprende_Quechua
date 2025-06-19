package com.app.aprendequechua.parametrizadas

import com.app.aprendequechua.utils.ValidacionRegistro
import com.app.aprendequechua.utils.ValidacionRegistro.ResultadoValidacion
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class ValidacionRegistroTest(
    private val nombre: String,
    private val email: String,
    private val password: String,
    private val confirmarContrasena: String,
    private val resultadoEsperado: String
) {

    @Test
    fun validarCamposDeRegistroConDiferentesDatos() {
        val result = ValidacionRegistro.validarCampos(nombre, email, password, confirmarContrasena)

        when (result) {
            is ResultadoValidacion.Success -> assertEquals("Success", resultadoEsperado)
            is ResultadoValidacion.Error -> assertEquals(resultadoEsperado, result.mensaje)
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "Nombre: {0}, Email: {1}, Pass: {2}, ConfirmPass: {3} => Esperado: {4}")
        fun datos(): Collection<Array<String>> = listOf(
            arrayOf("", "test@mail.com", "123456", "123456", "El nombre no puede estar vacío"),
            arrayOf("Juan", "correo_invalido", "123456", "123456", "Por favor, ingresa un email válido"),
            arrayOf("Juan", "test@mail.com", "123", "123", "La contraseña debe tener al menos 6 caracteres"),
            arrayOf("Juan", "test@mail.com", "123456", "654321", "Las contraseñas no coinciden"),
            arrayOf("Juan", "test@mail.com", "123456", "123456", "Success")
        )
    }
}
