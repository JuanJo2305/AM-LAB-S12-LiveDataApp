package com.example.livedataapp

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        val inputUsuario = findViewById<TextInputEditText>(R.id.inputUsuario)
        val inputPassword = findViewById<TextInputEditText>(R.id.inputPassword)
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)
        val progressFuerza = findViewById<ProgressBar>(R.id.progressFuerza)
        val tvFuerzaTexto = findViewById<TextView>(R.id.tvFuerzaTexto)

        inputUsuario.doAfterTextChanged {
            viewModel.usuario.value = it.toString()
        }

        inputPassword.doAfterTextChanged {
            viewModel.password.value = it.toString()
        }

        viewModel.isFormValid.observe(this) { isValid ->
            btnLogin.isEnabled = isValid
            val color = if (isValid) R.color.rojo_uns else R.color.btn_disabled
            btnLogin.setBackgroundColor(ContextCompat.getColor(this, color))
        }

        viewModel.mensajeError.observe(this) { error ->
            error?.let {
                Snackbar.make(btnLogin, it, Snackbar.LENGTH_SHORT).show()
            }
        }

        viewModel.usuarioAutenticado.observe(this) { user ->
            user?.let {
                val intent = Intent(this, BienvenidaActivity::class.java)
                intent.putExtra("nombreUsuario", it)
                startActivity(intent)
                finish()
            }
        }

        viewModel.fuerzaNumerica.observe(this) {fuerza ->

            if (viewModel.password.value.isNullOrEmpty()) {
                progressFuerza.visibility = View.GONE
                tvFuerzaTexto.visibility = View.GONE
                return@observe
            }

            progressFuerza.visibility = View.VISIBLE
            tvFuerzaTexto.visibility = View.VISIBLE

            progressFuerza.progress = fuerza

            when (fuerza) {
                in 0..39 -> {
                    tvFuerzaTexto.text = "Fuerza: Débil"
                    cambiarColorBarra(progressFuerza, "#FF5252") // rojo

                    Toast.makeText(
                        this,
                        "Su contraseña es débil, por favor cámbiela",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                in 40..69 -> {
                    tvFuerzaTexto.text = "Fuerza: Media"
                    cambiarColorBarra(progressFuerza, "#FFC107") // amarillo
                }
                in 70..100 -> {
                    tvFuerzaTexto.text = "Fuerza: Fuerte"
                    cambiarColorBarra(progressFuerza, "#4CAF50") // verde
                }
            }
        }

        btnLogin.setOnClickListener {
            viewModel.login()
        }
    }

    private fun cambiarColorBarra(bar: ProgressBar, colorHex: String) {
        val clipDrawable = bar.progressDrawable as LayerDrawable
        val progressLayer = clipDrawable.findDrawableByLayerId(android.R.id.progress)
        progressLayer.setColorFilter(Color.parseColor(colorHex), PorterDuff.Mode.SRC_IN)
    }
}
