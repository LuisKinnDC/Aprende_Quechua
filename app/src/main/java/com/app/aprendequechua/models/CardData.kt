package com.app.aprendequechua.models

data class CardData(
    val imageResId: Int,
    val label: String,
    var isMatched: Boolean = false
)