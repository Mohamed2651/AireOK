package com.mohammed.aireok

import DataBaseCopier
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mohammed.aireok.ui.theme.AireOKTheme
import com.mohammed.aireok.ui.theme.Navegacion

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1️⃣ Copiar la BD una vez, fuera de Compose

        // 2️⃣ Leer datos desde la BD
        DataBaseCopier.copiar(this, "mi_base_datos.sqlite")

        setContent {
            AireOKTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    // 3️⃣ Mostrar en pantalla la lista
                    Navegacion(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AireOKTheme {
        Greeting("Android")
    }
}