
package com.mohammed.aireok.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mohammed.aireok.network.EstacionBusquedaResponse
import kotlinx.coroutines.delay

@Composable
fun PantallaBuscar(navController: NavController, userViewModel: UserViewModel) {
    var consulta by remember { mutableStateOf("") }
    var yaBuscado by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(consulta) {
        if (consulta.trim().isNotEmpty()) {
            delay(300)
            yaBuscado = true
            userViewModel.buscarEstaciones(consulta.trim())
        } else {
            yaBuscado = false
            userViewModel.limpiarBusqueda()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        OutlinedTextField(
            value = consulta,
            onValueChange = { consulta = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Nombre de estación o municipio…") },
            leadingIcon = { Icon(Icons.Filled.Search, null, tint = MaterialTheme.colorScheme.primary) },
            trailingIcon = {
                if (consulta.isNotEmpty()) {
                    IconButton(onClick = { consulta = "" }) {
                        Icon(Icons.Filled.Clear, "Limpiar", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                cursorColor          = MaterialTheme.colorScheme.primary,
                focusedTextColor     = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor   = MaterialTheme.colorScheme.onSurface,
            )
        )

        Spacer(Modifier.height(16.dp))

        when {
            userViewModel.cargandoBusqueda -> {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            userViewModel.errorBusqueda != null -> {
                Text(userViewModel.errorBusqueda!!, color = MaterialTheme.colorScheme.error, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
            }
            userViewModel.resultadosBusqueda.isNotEmpty() -> {
                Text(
                    "${userViewModel.resultadosBusqueda.size} resultado(s) para \"$consulta\"",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(userViewModel.resultadosBusqueda) { estacion ->
                        val icaReal = userViewModel.icasBusqueda[estacion.uid]
                        val datosFrescos = userViewModel.datosFrescoBusqueda[estacion.uid] ?: false
                        EstacionBusquedaCard(estacion, icaReal, datosFrescos) {
                            val uid = estacion.uid?.toString() ?: ""
                            if (uid.isNotBlank()) navController.navigate(Pantalla.Estacion.conUid(uid))
                        }
                    }
                }
            }
            yaBuscado -> SinResultados(consulta)
            else -> BuscarSugerencias()
        }
    }
}

@Composable
private fun EstacionBusquedaCard(
    estacion: EstacionBusquedaResponse,
    icaReal: Int?,
    datosFrescos: Boolean,
    onClick: () -> Unit
) {
    val color = colorIca(icaReal)
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                Box(
                    modifier = Modifier.size(46.dp).clip(RoundedCornerShape(12.dp)).background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(icaReal?.toString() ?: "—", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = color)
                }
                if (datosFrescos) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(green)
                    )
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                val partes = estacion.nombre.split(",")
                Text(partes.firstOrNull()?.trim() ?: "Estación", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface, maxLines = 1)
                Text(partes.drop(1).joinToString(",").trim().ifBlank { "España" }, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
            }
            Box(
                modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(color.copy(alpha = 0.12f)).padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(etiquetaIca(icaReal), fontSize = 11.sp, fontWeight = FontWeight.Medium, color = color)
            }
        }
    }
}

@Composable
private fun SinResultados(consulta: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))
        Icon(Icons.Filled.Search, null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(64.dp))
        Spacer(Modifier.height(16.dp))
        Text(
            "Sin resultados para \"$consulta\"",
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "Prueba con otro nombre o municipio",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun BuscarSugerencias() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))
        Icon(Icons.Filled.Search, null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(64.dp))
        Spacer(Modifier.height(16.dp))
        Text(
            "Busca por nombre de estación\no municipio",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )
    }
}
