package com.mohammed.aireok.ui.theme

// No necesitas importar 'constrainAs' ni 'createRefs' explícitamente si usas el bloque de abajo correctamente
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import kotlin.system.exitProcess

@Composable
fun PantallaHome(navController: NavController, userViewModel: UserViewModel) {
    var mostrarDialogo by remember { mutableStateOf(false) }

    // Intercepta el botón físico "atrás"
    BackHandler {
        mostrarDialogo = true
    }

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("¿Salir de la app?") },
            text = { Text("¿Seguro que quieres cerrar la aplicación?") },
            confirmButton = {
                Button(onClick = { exitProcess(0) }) {
                    Text("Sí")
                }
            },
            dismissButton = {
                Button(onClick = { mostrarDialogo = false }) {
                    Text("No")
                }
            }
        )
    }

    // ConstraintLayout
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (texto, perfil, perfiltexto) = createRefs()

        Text(
            text = "Bienvenido, ${userViewModel.nombre} ",
            modifier = Modifier.constrainAs(texto){
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
        )
    }
}
