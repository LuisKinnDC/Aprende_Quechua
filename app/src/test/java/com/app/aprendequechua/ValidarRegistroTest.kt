package com.app.aprendequechua

import com.app.aprendequechua.utils.ValidacionRegistro
import org.junit.Assert.*
import org.junit.Test

class ValidarRegistroTest {

    @Test
    fun validarCampos_conEmailInvalido() {
        val resultado = ValidacionRegistro.validarCampos("Juan", "juan.gmail.com", "123456", "123456")
        assertTrue(resultado is ValidacionRegistro.ResultadoValidacion.Error)
        assertEquals("Por favor, ingresa un email válido", (resultado as ValidacionRegistro.ResultadoValidacion.Error).mensaje)
    }

    @Test
    fun validarCampos_conContraseñaCorta() {
        val resultado = ValidacionRegistro.validarCampos("Juan", "juan@gmail.com", "123", "123")
        assertTrue(resultado is ValidacionRegistro.ResultadoValidacion.Error)
        assertEquals("La contraseña debe tener al menos 6 caracteres", (resultado as ValidacionRegistro.ResultadoValidacion.Error).mensaje)
    }

    @Test
    fun validarCampos_conContraseñasDiferentes() {
        val resultado = ValidacionRegistro.validarCampos("Juan", "juan@gmail.com", "123456", "abcdef")
        assertTrue(resultado is ValidacionRegistro.ResultadoValidacion.Error)
        assertEquals("Las contraseñas no coinciden", (resultado as ValidacionRegistro.ResultadoValidacion.Error).mensaje)
    }

    @Test
    fun validarCampos_conDatosValidos() {
        val resultado = ValidacionRegistro.validarCampos("Juan", "juan@gmail.com", "123456", "123456")
        assertTrue(resultado is ValidacionRegistro.ResultadoValidacion.Success)
    }
}