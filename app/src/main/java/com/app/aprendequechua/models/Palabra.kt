package com.app.aprendequechua.models

data class Palabra(
    val id: String = "",
    val palabraQuechua: String = "",
    val traduccionEspanol: String = "",
    val ejemploQuechua: String = "",
    val ejemploEspanol: String = "",
    val pronunciacion: String = ""
)