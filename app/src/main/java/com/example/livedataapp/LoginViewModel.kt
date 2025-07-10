package com.example.livedataapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    val usuario = MutableLiveData("")
    val password = MutableLiveData("")
    val mensajeError = MutableLiveData<String?>()
    val usuarioAutenticado = MutableLiveData<String?>()

    private val usuariosValidos = mapOf(
        "juanjo" to "123456",
        "dj" to "abcdef"
    )

    val isFormValid: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(usuario) { value = validarCampos() }
        addSource(password) { value = validarCampos() }
    }

    val fuerzaNumerica = MediatorLiveData<Int>().apply {
        addSource(password) { pass ->
            value = when {
                pass.length < 6 -> 20
                pass.length < 10 && pass.any { it.isDigit() } && pass.any { it.isLetter() } -> 60
                pass.matches(Regex("(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{10,}")) -> 100
                else -> 40
            }
        }
    }




    private fun validarCampos(): Boolean {
        return !usuario.value.isNullOrBlank() && !password.value.isNullOrBlank()
    }

    fun login() {
        val user = usuario.value ?: ""
        val pass = password.value ?: ""

        if (usuariosValidos[user] == pass) {
            usuarioAutenticado.value = user
            mensajeError.value = null
        } else {
            mensajeError.value = "Credenciales incorrectas"
            usuarioAutenticado.value = null
        }
    }
}
