package com.app.aprendequechua.models

data class Adivinanza(
    val pregunta: String = "",
    val opcion1: String = "",
    val opcion2: String = "",
    val opcion3: String = "",
    val opcion4: String = "",
    val dificultad: String = "",
    val respuesta_correcta: String = "",
    val pista: String = ""
)