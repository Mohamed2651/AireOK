package com.mohammed.aireok.ui.theme

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import com.mohammed.aireok.R
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mohammed.aireok.network.EstacionResponse
import com.mohammed.aireok.network.ForecastDiaResponse
import com.mohammed.aireok.network.RetrofitClient
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaEstacion(navController: NavController, uid: String, userViewModel: UserViewModel) {
    val context = LocalContext.current
    val snackbarState = remember { SnackbarHostState() }
    var estacion by remember { mutableStateOf<EstacionResponse?>(null) }
    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    var nombreParaNotif by remember { mutableStateOf("") }
    var estacionParaCompartir by remember { mutableStateOf<EstacionResponse?>(null) }

    val notifLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        val nom = nombreParaNotif.also { nombreParaNotif = "" }
        if (granted && nom.isNotBlank()) mostrarNotificacion(context, nom)
    }

    val storageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            scope.launch { snackbarState.showSnackbar("Permiso de almacenamiento denegado") }
        } else {
            val e = estacion ?: return@rememberLauncherForActivityResult
            scope.launch {
                ejecutarExportYNotificacion(context, e, snackbarState) { nom ->
                    nombreParaNotif = nom
                    notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    val contactosLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        val e = estacionParaCompartir ?: return@rememberLauncherForActivityResult
        estacionParaCompartir = null
        compartirPdf(context, e)
    }

    fun cargar() {
        scope.launch {
            cargando = true; error = null
            Log.d("AireOK", "Cargando estación uid='$uid'")
            try {
                estacion = RetrofitClient.api.estacion(uid)
                Log.d("AireOK", "Estación cargada: ${estacion?.nombre}, ica=${estacion?.ica}, humedad=${estacion?.humedad}")
            } catch (e: Exception) {
                Log.e("AireOK", "Error cargando estación: ${e::class.simpleName}: ${e.message}")
                error = if (e is java.net.SocketTimeoutException)
                    "El servidor tardó demasiado en responder. Inténtalo de nuevo."
                else
                    "No se pudo cargar la estación"
            } finally {
                cargando = false
            }
        }
    }

    LaunchedEffect(uid) { cargar() }
    LaunchedEffect(Unit) { userViewModel.cargarFavoritos() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        estacion?.nombre?.split(",")?.firstOrNull()?.trim()?.ifBlank { "Estación" } ?: "Detalle",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = MaterialTheme.colorScheme.primary)
                    }
                },
                actions = {
                    if (estacion != null && !cargando) {
                        val esFav = uid in userViewModel.favoritosUids
                        IconButton(onClick = {
                            val e = estacion ?: return@IconButton
                            userViewModel.toggleFavorito(uid, e)
                            scope.launch {
                                snackbarState.showSnackbar(
                                    if (!esFav) "Añadido a favoritos" else "Eliminado de favoritos"
                                )
                            }
                        }) {
                            Icon(
                                painterResource( if (esFav) R.drawable.baseline_favorite_24 else R.drawable.baseline_favorite_border_24),
                                contentDescription = if (esFav) "Quitar favorito" else "Añadir favorito",
                                tint = if (esFav) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = {
                            val e = estacion ?: return@IconButton
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                                estacionParaCompartir = e
                                contactosLauncher.launch(Manifest.permission.READ_CONTACTS)
                            } else {
                                compartirPdf(context, e)
                            }
                        }) {
                            Icon(Icons.Filled.Share, "Compartir PDF", tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = {
                            val e = estacion ?: return@IconButton
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
                                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            ) {
                                storageLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            } else {
                                scope.launch {
                                    ejecutarExportYNotificacion(context, e, snackbarState) { nom ->
                                        nombreParaNotif = nom
                                        notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                }
                            }
                        }) {
                            Icon(Icons.Filled.Download, "Exportar PDF", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
            when {
                cargando -> CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                error != null -> VistaError(error!!, onAccion = { cargar() })
                estacion != null && estacion!!.idEstacion == "undefined" ->
                    VistaError("Estación no encontrada", labelBoton = "Volver", onAccion = { navController.popBackStack() })
                estacion != null -> DetalleContenido(estacion!!)
                else -> VistaError("No se pudieron cargar los datos", onAccion = { cargar() })
            }
        }
    }
}

// ─── Error ────────────────────────────────────────────────────────────────────

@Composable
private fun VistaError(mensaje: String, labelBoton: String = "Reintentar", onAccion: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
        Icon(Icons.Filled.CloudOff, null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(64.dp))
        Spacer(Modifier.height(12.dp))
        Text(mensaje, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onAccion) { Text(labelBoton) }
    }
}

// ─── Contenido principal ──────────────────────────────────────────────────────

@Composable
private fun DetalleContenido(e: EstacionResponse) {
    val icaColor = colorIca(e.ica)
    val datosObsoletos = !e.datosFrescos && esFechaObsoleta(e.ultimaFecha)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (datosObsoletos) BannerDatosObsoletos(e.ultimaFecha)
        TarjetaIca(e, icaColor)
        if (e.temperatura != null || e.humedad != null) TarjetaClima(e)
        TarjetaContaminantes(e)
        e.forecast?.uvi?.takeIf { it.isNotEmpty() }?.let { TarjetaUv(it) }
        e.forecast?.let { fc ->
            val tieneDatos = !fc.pm25.isNullOrEmpty() || !fc.o3.isNullOrEmpty()
            if (tieneDatos) TarjetaForecast(fc)
        }
        val lat = e.latDouble; val lon = e.lonDouble
        if (lat != null && lon != null) TarjetaUbicacion(lat, lon)
        Spacer(Modifier.height(8.dp))
    }
}

// ─── Tarjeta ICA ─────────────────────────────────────────────────────────────

@Composable
private fun TarjetaIca(e: EstacionResponse, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.horizontalGradient(listOf(color, color.copy(alpha = 0.65f))))
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                val partes = e.nombre.split(",")
                Text(partes.firstOrNull()?.trim() ?: "Estación", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                if (partes.size > 1)
                    Text(partes.drop(1).joinToString(",").trim(), color = Color.White.copy(0.8f), fontSize = 13.sp)
                if (!e.dominanteEfectivo.isNullOrBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Filled.Air, null, tint = Color.White.copy(0.8f), modifier = Modifier.size(13.dp))
                        Text("Dominante: ${e.dominanteEfectivo}", color = Color.White.copy(0.85f), fontSize = 12.sp)
                    }
                }
                if (!e.ultimaFecha.isNullOrBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Filled.Schedule, null, tint = Color.White.copy(0.7f), modifier = Modifier.size(12.dp))
                        Text(formatearFecha(e.ultimaFecha ?: ""), color = Color.White.copy(0.7f), fontSize = 11.sp)
                    }
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.size(76.dp).clip(CircleShape).background(Color.White.copy(0.22f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(e.ica?.toString() ?: "—", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                }
                Spacer(Modifier.height(6.dp))
                Text(etiquetaIca(e.ica), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
            }
        }
    }
}

// ─── Tarjeta Clima ────────────────────────────────────────────────────────────

@Composable
private fun TarjetaClima(e: EstacionResponse) {
    val uvHoy = e.forecast?.uvi?.firstOrNull()?.avg

    SeccionCard(titulo = "Condiciones atmosféricas", icono = Icons.Filled.WbSunny) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            if (e.temperatura != null) {
                ItemClima(
                    icono = Icons.Filled.Thermostat,
                    valor = "%.1f°C".format(e.temperatura),
                    etiqueta = "Temperatura",
                    color = when {
                        e.temperatura < 0  -> Color(0xFF2196F3)
                        e.temperatura < 10 -> Color(0xFF03A9F4)
                        e.temperatura < 20 -> green
                        e.temperatura < 30 -> orangeYellow
                        else               -> orangeRed
                    }
                )
            }
            if (e.humedad != null) {
                ItemClima(
                    icono = Icons.Filled.WaterDrop,
                    valor = "%.0f%%".format(e.humedad),
                    etiqueta = "Humedad",
                    color = Color(0xFF2196F3)
                )
            }
            if (uvHoy != null) {
                ItemClima(
                    icono = Icons.Filled.LightMode,
                    valor = "%.0f".format(uvHoy),
                    etiqueta = "Índice UV",
                    color = when {
                        uvHoy < 3  -> green
                        uvHoy < 6  -> Color(0xFFFFC107)
                        uvHoy < 8  -> Color(0xFFFF9800)
                        uvHoy < 11 -> Color(0xFFF44336)
                        else       -> Color(0xFF9C27B0)
                    }
                )
            }
        }
    }
}

@Composable
private fun ItemClima(icono: ImageVector, valor: String, etiqueta: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(
            modifier = Modifier.size(52.dp).clip(CircleShape).background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icono, null, tint = color, modifier = Modifier.size(26.dp))
        }
        Text(valor, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Text(etiqueta, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
    }
}

// ─── Tarjeta Contaminantes ────────────────────────────────────────────────────

@Composable
private fun TarjetaContaminantes(e: EstacionResponse) {
    val c = e.contaminantes
    val lista = listOfNotNull(
        c?.pm25?.let { "PM2.5" to it },
        c?.pm10?.let { "PM10"  to it },
        c?.no2?.let  { "NO₂"   to it },
        c?.o3?.let   { "O₃"    to it },
        c?.so2?.let  { "SO₂"   to it },
        c?.co?.let   { "CO"    to it }
    )

    SeccionCard(titulo = "Contaminantes", icono = Icons.Filled.Science) {
        if (lista.isEmpty()) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Filled.Info, null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(20.dp))
                Text("No hay datos de contaminantes", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                lista.forEachIndexed { i, (nombre, valor) ->
                    FilaContaminante(nombre, valor)
                    if (i < lista.lastIndex)
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(vertical = 6.dp))
                }
            }
        }
    }
}

@Composable
private fun FilaContaminante(nombre: String, valor: Double) {
    val umbral = umbralesContaminante(nombre)
    val porcentaje = if (umbral > 0) (valor / umbral).coerceIn(0.0, 1.0).toFloat() else 0f
    val color = colorBarraContaminante(porcentaje)

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(nombre, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer, textAlign = TextAlign.Center)
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(nombreCompletoContaminante(nombre), fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                Text("%.1f µg/m³".format(valor), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = color)
            }
            LinearProgressIndicator(
                progress = { porcentaje },
                modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(3.dp)),
                color = color,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

private fun umbralesContaminante(nombre: String): Double = when (nombre) {
    "PM2.5" -> 75.0
    "PM10"  -> 150.0
    "NO₂"   -> 200.0
    "O₃"    -> 180.0
    "SO₂"   -> 350.0
    "CO"    -> 10.0
    else    -> 100.0
}

private fun nombreCompletoContaminante(nombre: String): String = when (nombre) {
    "PM2.5" -> "Partículas finas"
    "PM10"  -> "Partículas gruesas"
    "NO₂"   -> "Dióxido de nitrógeno"
    "O₃"    -> "Ozono"
    "SO₂"   -> "Dióxido de azufre"
    "CO"    -> "Monóxido de carbono"
    else    -> nombre
}

private fun colorBarraContaminante(pct: Float): Color = when {
    pct < 0.33f -> green
    pct < 0.66f -> Color(0xFFFFC107)
    pct < 0.85f -> Color(0xFFFF9800)
    else        -> orangeRed
}

// ─── Tarjeta UV ───────────────────────────────────────────────────────────────

@Composable
private fun TarjetaUv(uvDias: List<ForecastDiaResponse>) {
    val hoy = uvDias.firstOrNull()?.avg ?: return
    val (etiqueta, consejo) = when {
        hoy < 3  -> "Bajo" to "Sin precaución especial necesaria"
        hoy < 6  -> "Moderado" to "Protección solar recomendada al mediodía"
        hoy < 8  -> "Alto" to "Reduce la exposición entre 10h y 16h"
        hoy < 11 -> "Muy alto" to "Usa protección solar, sombrero y gafas"
        else     -> "Extremo" to "Evita la exposición al sol directa"
    }
    val colorUv = when {
        hoy < 3  -> green
        hoy < 6  -> Color(0xFFFFC107)
        hoy < 8  -> Color(0xFFFF9800)
        hoy < 11 -> Color(0xFFF44336)
        else     -> Color(0xFF9C27B0)
    }

    SeccionCard(titulo = "Índice UV", icono = Icons.Filled.LightMode) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(
                modifier = Modifier.size(60.dp).clip(CircleShape).background(colorUv.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text("%.0f".format(hoy), fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = colorUv)
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(etiqueta, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colorUv)
                Text(consejo, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

// ─── Tarjeta Forecast ─────────────────────────────────────────────────────────

@Composable
private fun TarjetaForecast(forecast: com.mohammed.aireok.network.ForecastResponse) {
    val dias = (forecast.pm25 ?: forecast.o3 ?: return).take(5)
    val usandoPm25 = forecast.pm25 != null
    val etiqueta = if (usandoPm25) "PM2.5" else "O₃"

    SeccionCard(titulo = "Previsión 5 días · $etiqueta", icono = Icons.Filled.CalendarMonth) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            dias.forEach { dia -> FilaDiaForecast(dia) }
        }
    }
}

@Composable
private fun FilaDiaForecast(dia: ForecastDiaResponse) {
    val nombre = try {
        val d = LocalDate.parse(dia.day)
        d.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("es"))
    } catch (e: Exception) { dia.day?.takeLast(5) ?: "—" }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(nombre, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
        Text(
            "%.0f".format(dia.avg ?: 0.0),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text("µg/m³", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            Text("↑%.0f".format(dia.max ?: 0.0), fontSize = 9.sp, color = Color(0xFFF44336))
            Text("↓%.0f".format(dia.min ?: 0.0), fontSize = 9.sp, color = green)
        }
    }
}

// ─── Tarjeta Ubicación ────────────────────────────────────────────────────────

@Composable
private fun TarjetaUbicacion(lat: Double, lon: Double) {
    SeccionCard(titulo = "Ubicación", icono = Icons.Filled.LocationOn) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(Icons.Filled.PinDrop, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
            Text("%.5f, %.5f".format(lat, lon), fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// ─── Componente reutilizable de sección ───────────────────────────────────────

@Composable
private fun SeccionCard(
    titulo: String,
    icono: ImageVector,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(icono, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                Text(titulo, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            contenido()
        }
    }
}

// ─── Banner datos obsoletos ───────────────────────────────────────────────────

@Composable
private fun BannerDatosObsoletos(ultimaActualizacion: String?) {
    val texto = if (ultimaActualizacion.isNullOrBlank())
        "Sin fecha de actualización disponible"
    else
        "Última medición: ${ultimaActualizacion.substringBefore("T")}. Puede que los datos no sean actuales."

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFFFF3E0)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(Icons.Filled.Warning, null, tint = Color(0xFFF57C00), modifier = Modifier.size(18.dp))
            Text(texto, fontSize = 12.sp, color = Color(0xFF5D3A00), lineHeight = 17.sp)
        }
    }
}

private fun esFechaObsoleta(fecha: String?): Boolean {
    if (fecha.isNullOrBlank()) return true
    return try {
        val dt = OffsetDateTime.parse(fecha)
        val horasDesde = (System.currentTimeMillis() - dt.toInstant().toEpochMilli()) / 3_600_000L
        horasDesde > 24
    } catch (e: Exception) { true }
}

private fun formatearFecha(fecha: String): String = try {
    "Actualizado: " + fecha.substringBefore("T") + " " + fecha.substringAfter("T").take(5)
} catch (e: Exception) { fecha }

// ─── Exportar PDF + Notificación ─────────────────────────────────────────────

private suspend fun ejecutarExportYNotificacion(
    context: Context,
    e: EstacionResponse,
    snackbarState: SnackbarHostState,
    onPedirPermNotif: (String) -> Unit
) {
    val ok = exportarPdf(context, e)
    if (ok) {
        val nom = e.nombre.split(",").firstOrNull()?.trim() ?: "estación"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                mostrarNotificacion(context, nom)
            } else {
                onPedirPermNotif(nom)
            }
        } else {
            mostrarNotificacion(context, nom)
        }
        snackbarState.showSnackbar("PDF guardado en Descargas")
    } else {
        snackbarState.showSnackbar("Error al exportar el PDF")
    }
}

private fun mostrarNotificacion(context: Context, nombreEstacion: String) {
    val channelId = "aireok_descargas"
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(channelId, "Descargas AireOK", NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = "Notificaciones cuando se descarga un informe PDF"
        }
        manager.createNotificationChannel(channel)
    }
    val notif = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.stat_sys_download_done)
        .setContentTitle("Informe descargado")
        .setContentText("Informe de $nombreEstacion guardado en Descargas")
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()
    manager.notify(1001, notif)
}

// ─── Guardar PDF ──────────────────────────────────────────────────────────────

private fun exportarPdf(context: Context, e: EstacionResponse): Boolean {
    val doc = crearDocumentoPdf(e) ?: return false
    val nombreSanitizado = e.nombre.split(",").firstOrNull()?.trim()
        ?.replace(Regex("[^a-zA-Z0-9 ]"), "")?.trim()
        ?.replace(" ", "_")?.take(30) ?: "Estacion"
    val ts = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())
    val fileName = "AireOK_${nombreSanitizado}_$ts"
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val cv = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileName.pdf")
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, cv)
                ?: return false
            context.contentResolver.openOutputStream(uri)?.use { doc.writeTo(it) }
        } else {
            val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: return false
            dir.mkdirs()
            File(dir, "$fileName.pdf").outputStream().use { doc.writeTo(it) }
        }
        true
    } catch (_: Exception) { false } finally { doc.close() }
}

private fun crearDocumentoPdf(e: EstacionResponse): PdfDocument? = try {
    val doc = PdfDocument()
    val page = doc.startPage(PdfDocument.PageInfo.Builder(595, 842, 1).create())
    dibujarPdf(page.canvas, e)
    doc.finishPage(page)
    doc
} catch (_: Exception) { null }

private fun dibujarPdf(canvas: android.graphics.Canvas, e: EstacionResponse) {
    val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    val txt  = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }

    // ── Cabecera ──────────────────────────────────────────────────────────────
    fill.color = android.graphics.Color.rgb(13, 71, 161)
    canvas.drawRect(0f, 0f, 595f, 80f, fill)
    fill.color = android.graphics.Color.rgb(0, 137, 123)
    canvas.drawRect(360f, 0f, 595f, 80f, fill)

    txt.color = android.graphics.Color.WHITE; txt.textSize = 28f; txt.isFakeBoldText = true
    canvas.drawText("AireOK", 30f, 52f, txt)
    txt.textSize = 11f; txt.isFakeBoldText = false; txt.alpha = 200
    canvas.drawText("Informe de Calidad del Aire", 30f, 70f, txt)

    val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
    txt.textAlign = Paint.Align.RIGHT
    canvas.drawText(fecha, 565f, 70f, txt)
    txt.textAlign = Paint.Align.LEFT; txt.alpha = 255

    var y = 108f

    // ── Nombre de la estación ─────────────────────────────────────────────────
    val partes = e.nombre.split(",")
    txt.color = android.graphics.Color.rgb(13, 71, 161); txt.textSize = 20f; txt.isFakeBoldText = true
    canvas.drawText(partes.firstOrNull()?.trim() ?: "Estación", 30f, y, txt); y += 22f
    txt.color = android.graphics.Color.rgb(100, 100, 100); txt.textSize = 12f; txt.isFakeBoldText = false
    canvas.drawText(partes.drop(1).joinToString(",").trim().ifBlank { "España" }, 30f, y, txt); y += 28f

    // ── Caja ICA ──────────────────────────────────────────────────────────────
    val icaColor = when {
        e.ica == null -> android.graphics.Color.rgb(120, 144, 156)
        e.ica <= 50   -> android.graphics.Color.rgb(56, 142, 60)
        e.ica <= 100  -> android.graphics.Color.rgb(249, 168, 37)
        e.ica <= 150  -> android.graphics.Color.rgb(239, 108, 0)
        e.ica <= 200  -> android.graphics.Color.rgb(211, 47, 47)
        e.ica <= 300  -> android.graphics.Color.rgb(123, 31, 162)
        else          -> android.graphics.Color.rgb(74, 0, 0)
    }
    val etiqIca = when {
        e.ica == null -> "Sin datos"
        e.ica <= 50   -> "Bueno"
        e.ica <= 100  -> "Moderado"
        e.ica <= 150  -> "Dañino para sensibles"
        e.ica <= 200  -> "Dañino"
        e.ica <= 300  -> "Muy dañino"
        else          -> "Peligroso"
    }
    fill.color = icaColor
    canvas.drawRoundRect(RectF(30f, y, 565f, y + 74f), 12f, 12f, fill)
    fill.color = android.graphics.Color.argb(50, 255, 255, 255)
    canvas.drawCircle(74f, y + 37f, 28f, fill)

    txt.color = android.graphics.Color.WHITE; txt.textSize = 20f; txt.isFakeBoldText = true
    txt.textAlign = Paint.Align.CENTER
    canvas.drawText(e.ica?.toString() ?: "—", 74f, y + 44f, txt)
    txt.textAlign = Paint.Align.LEFT

    txt.textSize = 15f
    canvas.drawText(etiqIca, 118f, y + 30f, txt)
    txt.textSize = 11f; txt.isFakeBoldText = false; txt.alpha = 200
    if (!e.dominanteEfectivo.isNullOrBlank())
        canvas.drawText("Dominante: ${e.dominanteEfectivo}", 118f, y + 48f, txt)
    if (!e.ultimaFecha.isNullOrBlank()) {
        txt.textAlign = Paint.Align.RIGHT
        canvas.drawText(
            "Act: ${e.ultimaFecha!!.substringBefore("T")} ${e.ultimaFecha!!.substringAfter("T","").take(5)}",
            555f, y + 66f, txt
        )
        txt.textAlign = Paint.Align.LEFT
    }
    txt.alpha = 255; y += 92f

    // ── Helpers ───────────────────────────────────────────────────────────────
    fun seccion(titulo: String) {
        txt.color = android.graphics.Color.rgb(13, 71, 161); txt.textSize = 13f; txt.isFakeBoldText = true
        canvas.drawText(titulo, 30f, y, txt); y += 6f
        val p = Paint().apply { color = android.graphics.Color.rgb(13, 71, 161); strokeWidth = 1.5f }
        canvas.drawLine(30f, y, 565f, y, p); y += 14f
        txt.isFakeBoldText = false; txt.color = android.graphics.Color.rgb(50, 50, 50); txt.textSize = 12f
    }

    fun fila(k: String, v: String, sombreado: Boolean) {
        if (sombreado) {
            fill.color = android.graphics.Color.rgb(240, 245, 255)
            canvas.drawRect(30f, y - 13f, 565f, y + 6f, fill)
        }
        canvas.drawText(k, 40f, y, txt)
        txt.textAlign = Paint.Align.RIGHT; txt.isFakeBoldText = true
        canvas.drawText(v, 555f, y, txt)
        txt.textAlign = Paint.Align.LEFT; txt.isFakeBoldText = false; y += 21f
    }

    // ── Contaminantes ─────────────────────────────────────────────────────────
    val c = e.contaminantes
    val contLista = listOfNotNull(
        c?.pm25?.let { "PM2.5 – Partículas finas"      to "%.1f µg/m³".format(it) },
        c?.pm10?.let { "PM10 – Partículas gruesas"      to "%.1f µg/m³".format(it) },
        c?.no2?.let  { "NO2 – Dióxido de nitrógeno"     to "%.1f µg/m³".format(it) },
        c?.o3?.let   { "O3 – Ozono"                     to "%.1f µg/m³".format(it) },
        c?.so2?.let  { "SO2 – Dióxido de azufre"        to "%.1f µg/m³".format(it) },
        c?.co?.let   { "CO – Monóxido de carbono"       to "%.1f µg/m³".format(it) }
    )
    if (contLista.isNotEmpty()) {
        seccion("Contaminantes")
        contLista.forEachIndexed { i, (k, v) -> fila(k, v, i % 2 == 0) }
        y += 10f
    }

    // ── Condiciones atmosféricas ──────────────────────────────────────────────
    if (e.temperatura != null || e.humedad != null) {
        seccion("Condiciones atmosféricas")
        e.temperatura?.let { fila("Temperatura", "%.1f °C".format(it), false) }
        e.humedad?.let      { fila("Humedad relativa", "%.0f %%".format(it), true) }
        y += 10f
    }

    // ── Pie de página ─────────────────────────────────────────────────────────
    val pPie = Paint().apply { color = android.graphics.Color.rgb(200, 200, 200); strokeWidth = 1f }
    canvas.drawLine(30f, 812f, 565f, 812f, pPie)
    val tPie = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = android.graphics.Color.rgb(150, 150, 150); textSize = 9f }
    canvas.drawText("Generado por AireOK · $fecha", 30f, 829f, tPie)
    tPie.textAlign = Paint.Align.RIGHT
    canvas.drawText("Datos de calidad del aire · España", 565f, 829f, tPie)
}

// ─── Compartir PDF ────────────────────────────────────────────────────────────

private fun compartirPdf(context: Context, e: EstacionResponse) {
    val doc = crearDocumentoPdf(e) ?: return
    val nombreSanitizado = e.nombre.split(",").firstOrNull()?.trim()
        ?.replace(Regex("[^a-zA-Z0-9 ]"), "")?.trim()
        ?.replace(" ", "_")?.take(30) ?: "Estacion"
    val ts = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())
    val fileName = "AireOK_${nombreSanitizado}_$ts.pdf"
    try {
        val cacheDir = File(context.cacheDir, "pdfs").also { it.mkdirs() }
        val file = File(cacheDir, fileName)
        file.outputStream().use { doc.writeTo(it) }
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Informe AireOK - ${e.nombre.split(",").firstOrNull()?.trim()}")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Compartir informe PDF"))
    } catch (_: Exception) {
    } finally {
        doc.close()
    }
}
