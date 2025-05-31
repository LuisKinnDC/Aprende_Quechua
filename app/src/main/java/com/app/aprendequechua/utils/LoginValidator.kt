package com.app.aprendequechua.utils

object LoginValidator {

    // Patrón de email usando regex estándar de Kotlin
    private val EMAIL_REGEX = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}".toRegex()

    fun validateLogin(email: String, password: String): ValidationResult {
        return when {
            email.isEmpty() -> ValidationResult.Error("Este campo es obligatorio")
            !isValidEmail(email) -> ValidationResult.Error("Correo inválido")
            password.isEmpty() -> ValidationResult.Error("Este campo es obligatorio")
            password.length < 6 -> ValidationResult.Error("Mínimo 6 caracteres")
            else -> ValidationResult.Success
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return EMAIL_REGEX.matches(email)
    }

    sealed class ValidationResult {
        object Success : ValidationResult()
        data class Error(val message: String) : ValidationResult()
    }
}