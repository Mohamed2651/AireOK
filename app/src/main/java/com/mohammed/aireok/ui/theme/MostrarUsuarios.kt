package com.mohammed.aireok.ui.theme

// Import necesarios
import android.R.attr.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp



@Composable
fun MostrarUsuarios(modifier: Modifier) {

    val context = LocalContext.current

    // Cargar la lista desde la base de datos y mantenerla con rememberSaveable
    var listaUsuarios by remember { mutableStateOf(DBTest.obtenerUsuarios(context)) }

    LazyColumn( modifier = Modifier.padding(top = 30.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(listaUsuarios) { u ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("ID: ${u.id}")
                    Text("Nombre: ${u.nombre}")
                    Text("Email: ${u.email}")
                    Text("Rol: ${u.rol}")
                }
            }
        }
    }
}
