package com.app.aprendequechua.utils

object ValidacionRegistro {

    fun validarCampos(
        nombre: String,
        email: String,
        password: String,
        confirmarContrasena: String
    ): ResultadoValidacion {
        if (nombre.isEmpty()) {
            return ResultadoValidacion.Error("El nombre no puede estar vacío")
        }

        if (!isValidEmail(email)) {
            return ResultadoValidacion.Error("Por favor, ingresa un email válido")
        }

        if (password.isEmpty() || password.length < 6) {
            return ResultadoValidacion.Error("La contraseña debe tener al menos 6 caracteres")
        }

        if (password != confirmarContrasena) {
            return ResultadoValidacion.Error("Las contraseñas no coinciden")
        }

        return ResultadoValidacion.Success
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}".toRegex()
        return emailRegex.matches(email)
    }

    sealed class ResultadoValidacion {
        object Success : ResultadoValidacion()
        data class Error(val mensaje: String) : ResultadoValidacion()
    }
}