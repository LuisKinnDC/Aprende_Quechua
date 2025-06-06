package com.app.aprendequechua.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    // LiveData para almacenar la URL del avatar
    private val _avatarUrl = MutableLiveData<String>()
    val avatarUrl: LiveData<String> = _avatarUrl

    // Función para actualizar la URL del avatar
    fun updateAvatarUrl(newUrl: String) {
        _avatarUrl.value = newUrl
    }
}