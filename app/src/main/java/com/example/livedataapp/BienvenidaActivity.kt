package com.example.livedataapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.google.android.material.chip.Chip

class BienvenidaActivity : AppCompatActivity() {
    private lateinit var usuarioLiveData: MutableLiveData<String>
    private val tiempoLiveData = MutableLiveData<Int>()
    private var segundos = 0
    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bienvenida)

        val tvSaludo = findViewById<TextView>(R.id.tvSaludo)
        val chipEstado = findViewById<Chip>(R.id.chipEstado)

        val nombreUsuario = intent.getStringExtra("nombreUsuario") ?: "Invitado"

        // Usando LiveData para actualizar el saludo
        usuarioLiveData = MutableLiveData(nombreUsuario)

        usuarioLiveData.observe(this) { nombre ->
            tvSaludo.text = "Bienvenido, $nombre 👋"
        }

        tiempoLiveData.observe(this) { segundos ->
            chipEstado.text = "Sesión activa: ${segundos}s"
        }

        // Comenzar contador de segundos con LiveData
        handler = Handler(Looper.getMainLooper())
        iniciarContador()
    }
    private fun iniciarContador() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                segundos++
                tiempoLiveData.value = segundos
                handler.postDelayed(this, 1000)
            }
        }, 1000)
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

}
