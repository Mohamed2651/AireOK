package com.mohammed.aireok.ui.theme

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mohammed.aireok.network.EstacionResponse
import kotlin.system.exitProcess

@Composable
fun PantallaHome(navController: NavController, userViewModel: UserViewModel) {
    var mostrarDialogo by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { userViewModel.cargarEstaciones() }

    BackHandler { mostrarDialogo = true }

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("¿Salir de la app?") },
            text = { Text("¿Seguro que quieres cerrar la aplicación?") },
            confirmButton = {
                TextButton(onClick = { exitProcess(0) }) {
                    Text("Sí", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) { Text("No") }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Header bienvenida (gradiente de marca, se mantiene en ambos modos)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(listOf(Color(0xFF0D47A1), Color(0xFF00897B))),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Hola, ${userViewModel.usuarioNombre.ifBlank { "usuario" }} 👋",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Consulta la calidad del aire\nde tu zona",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Air, null, tint = Color.White, modifier = Modifier.size(36.dp))
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            "Índice de Calidad del Aire",
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(12.dp))
        IcaLegendaCard()

        Spacer(Modifier.height(20.dp))

        Text(
            "Accesos rápidos",
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AccesoRapidoCard(Icons.Filled.LocationOn, "Mi zona", "Estación cercana", Color(0xFF1565C0), Modifier.weight(1f))
            AccesoRapidoCard(Icons.Filled.Search, "Buscar", "Por nombre", Color(0xFF00897B), Modifier.weight(1f))
        }

        Spacer(Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AccesoRapidoCard(Icons.Filled.Map, "Mapa", "144 estaciones", Color(0xFF7B1FA2), Modifier.weight(1f))
            AccesoRapidoCard(Icons.Filled.BarChart, "Historial", "Últimos 7 días", Color(0xFFE65100), Modifier.weight(1f))
        }

        Spacer(Modifier.height(20.dp))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Estaciones en España",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )
            if (userViewModel.cargandoEstaciones) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.primary)
            }
        }
        Spacer(Modifier.height(10.dp))

        if (userViewModel.errorEstaciones != null) {
            Text(userViewModel.errorEstaciones!!, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
        }

        userViewModel.estaciones.forEach { estacion ->
            EstacionCard(estacion) {
                val uid = estacion.resolvedUid
                if (uid.isNotBlank()) navController.navigate(Pantalla.Estacion.conUid(uid))
            }
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(16.dp))
    }
}

private fun colorIca(ica: Int?): Color = when {
    ica == null -> Color(0xFF9E9E9E)
    ica <= 50   -> Color(0xFF4CAF50)
    ica <= 100  -> Color(0xFFFFC107)
    ica <= 150  -> Color(0xFFFF9800)
    ica <= 200  -> Color(0xFFF44336)
    ica <= 300  -> Color(0xFF9C27B0)
    else        -> Color(0xFF7B0000)
}

private fun etiquetaIca(ica: Int?): String = when {
    ica == null -> "Sin datos"
    ica <= 50   -> "Bueno"
    ica <= 100  -> "Moderado"
    ica <= 150  -> "Dañino sensibles"
    ica <= 200  -> "Dañino"
    ica <= 300  -> "Muy dañino"
    else        -> "Peligroso"
}

@Composable
private fun EstacionCard(estacion: EstacionResponse, onClick: () -> Unit) {
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
            val color = colorIca(estacion.ica)
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = estacion.ica?.toString() ?: "—",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = color
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                val partes = estacion.nombre.split(",")
                Text(
                    partes.firstOrNull()?.trim()?.ifBlank { "Estación" } ?: "Estación",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        partes.drop(1).joinToString(",").trim().ifBlank { "España" },
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                    if (estacion.datosFrescos) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF4CAF50)))
                    }
                }
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.12f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(etiquetaIca(estacion.ica), fontSize = 11.sp, fontWeight = FontWeight.Medium, color = color)
            }
        }
    }
}

@Composable
private fun IcaLegendaCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            IcaNivel("0 – 50", "Bueno", Color(0xFF4CAF50))
            IcaNivel("51 – 100", "Moderado", Color(0xFFFFC107))
            IcaNivel("101 – 150", "Dañino sensibles", Color(0xFFFF9800))
            IcaNivel("151 – 200", "Dañino", Color(0xFFF44336))
            IcaNivel("201 – 300", "Muy dañino", Color(0xFF9C27B0))
            IcaNivel("300+", "Peligroso", Color(0xFF7B0000))
        }
    }
}

@Composable
private fun IcaNivel(rango: String, etiqueta: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(10.dp))
        Text(etiqueta, fontSize = 13.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurface)
        Text(rango, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun AccesoRapidoCard(
    icono: ImageVector,
    titulo: String,
    subtitulo: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.Start) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icono, null, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.height(10.dp))
            Text(titulo, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(subtitulo, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
