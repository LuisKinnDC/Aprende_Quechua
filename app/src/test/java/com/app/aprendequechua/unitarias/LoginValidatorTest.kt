package com.app.aprendequechua.unitarias

import com.app.aprendequechua.utils.LoginValidator
import org.junit.Assert.*
import org.junit.Test

class LoginValidatorTest {

    @Test
    fun `email vacío devuelve error`() {
        val result = LoginValidator.validateLogin("", "123456")
        assertTrue(result is LoginValidator.ValidationResult.Error)
        assertEquals("Este campo es obligatorio", (result as LoginValidator.ValidationResult.Error).message)
    }

    @Test
    fun `email inválido devuelve error`() {
        val result = LoginValidator.validateLogin("juan.gmail.com", "123456")
        assertTrue(result is LoginValidator.ValidationResult.Error)
        assertEquals("Correo inválido", (result as LoginValidator.ValidationResult.Error).message)
    }

    @Test
    fun `contraseña vacía devuelve error`() {
        val result = LoginValidator.validateLogin("juan@gmail.com", "")
        assertTrue(result is LoginValidator.ValidationResult.Error)
        assertEquals("Este campo es obligatorio", (result as LoginValidator.ValidationResult.Error).message)
    }

    @Test
    fun `contraseña corta devuelve error`() {
        val result = LoginValidator.validateLogin("juan@gmail.com", "123")
        assertTrue(result is LoginValidator.ValidationResult.Error)
        assertEquals("Mínimo 6 caracteres", (result as LoginValidator.ValidationResult.Error).message)
    }

    @Test
    fun `credenciales válidas devuelven éxito`() {
        val result = LoginValidator.validateLogin("juan@gmail.com", "123456")
        assertTrue(result is LoginValidator.ValidationResult.Success)
    }
}
