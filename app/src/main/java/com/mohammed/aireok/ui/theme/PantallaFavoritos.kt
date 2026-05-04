package com.mohammed.aireok.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.mohammed.aireok.R
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mohammed.aireok.network.EstacionResponse

@Composable
fun PantallaFavoritos(navController: NavController, userViewModel: UserViewModel) {
    LaunchedEffect(Unit) { userViewModel.cargarEstacionesFavoritas() }

    val favoritos = userViewModel.estacionesFavoritas
    val cargando = userViewModel.cargandoFavoritos

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            cargando -> CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
            favoritos.isEmpty() -> EstadoVacioFavoritos()
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favoritos, key = { it.resolvedUid }) { estacion ->
                    TarjetaFavorito(
                        estacion = estacion,
                        onClick = { navController.navigate(Pantalla.Estacion.conUid(estacion.resolvedUid)) },
                        onEliminar = { userViewModel.toggleFavorito(estacion.resolvedUid, estacion) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EstadoVacioFavoritos() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painterResource(R.drawable.baseline_favorite_border_24),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(72.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Sin estaciones favoritas",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Pulsa el corazón en la pantalla de una estación para añadirla aquí",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TarjetaFavorito(
    estacion: EstacionResponse,
    onClick: () -> Unit,
    onEliminar: () -> Unit
) {
    val icaColor = colorIca(estacion.ica)

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(icaColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    estacion.ica?.toString() ?: "—",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                val partes = estacion.nombre.split(",")
                Text(
                    partes.firstOrNull()?.trim() ?: "Estación",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                if (partes.size > 1) {
                    Text(
                        partes.drop(1).joinToString(",").trim(),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
                Text(
                    etiquetaIca(estacion.ica),
                    fontSize = 12.sp,
                    color = icaColor,
                    fontWeight = FontWeight.Medium
                )
            }
            IconButton(onClick = onEliminar) {
                Icon(painterResource(R.drawable.baseline_favorite_24), "Quitar favorito", tint = green)
            }
        }
    }
}

