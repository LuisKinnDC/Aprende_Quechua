package com.app.aprendequechua.models

data class Palabra(
    val palabraQuechua: String = "",
    val significado: String = "",
    val ejemploQuechua: String = "",
    val ejemploEspanol: String = "",
    val urlPronunciacion: String = "",
    var isFavorite: Boolean = false
) {
    val palabraQuechuaLower: String
        get() = palabraQuechua.lowercase()
}
