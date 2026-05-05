package com.mohammed.aireok.ui.theme

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.mohammed.aireok.network.EstacionResponse
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PantallaMapa(navController: NavController, userViewModel: UserViewModel) {
    val context = LocalContext.current

    var estacionSeleccionada by remember { mutableStateOf<EstacionResponse?>(null) }
    var tienePermiso by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
        )
    }

    val lanzadorPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido -> tienePermiso = concedido }

    // Cargar todas las estaciones para el mapa
    LaunchedEffect(Unit) { userViewModel.cargarEstacionesMapa() }

    // Configurar osmdroid una sola vez
    remember {
        Configuration.getInstance().apply {
            load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
            userAgentValue = context.packageName
        }
    }

    // Crear el MapView una sola vez
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            minZoomLevel = 4.0
            controller.setZoom(6.0)
            controller.setCenter(GeoPoint(40.2, -3.5)) // Centro de España
        }
    }

    // Overlay de ubicación del usuario
    val locationOverlay = remember {
        MyLocationNewOverlay(GpsMyLocationProvider(context), mapView)
    }

    // Añadir/quitar overlays cuando cargan las estaciones
    val estaciones = userViewModel.todasEstaciones
    LaunchedEffect(estaciones) {
        if (estaciones.isEmpty()) return@LaunchedEffect

        // Limpiar marcadores y polígonos anteriores (conservar overlay de ubicación)
        mapView.overlays.removeAll { it is Marker || it is Polygon }

        // ── Capa 1: halos de color sobre cada ciudad española con datos ───────
        CIUDADES_ESPANA.forEach { ciudad ->
            val ica = icaCiudad(ciudad, estaciones) ?: return@forEach
            Polygon(mapView).apply {
                points = puntosCirculo(ciudad.lat, ciudad.lon, 40_000.0)
                fillPaint.color  = colorIcaAndroid(ica)
                fillPaint.alpha  = 60
                outlinePaint.color       = colorIcaAndroid(ica)
                outlinePaint.alpha       = 110
                outlinePaint.strokeWidth = 3f
                infoWindow = null
                setOnClickListener { _, _, _ -> false }
                mapView.overlays.add(this)
            }
        }

        // ── Capa 2: marcadores individuales de estación (encima de los halos) ─
        estaciones.forEach { estacion ->
            val lat = estacion.latDouble ?: return@forEach
            val lon = estacion.lonDouble ?: return@forEach
            Marker(mapView).apply {
                position = GeoPoint(lat, lon)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                icon = crearIconoMarcador(context, estacion.ica)
                infoWindow = null
                setOnMarkerClickListener { _, _ ->
                    estacionSeleccionada = if (
                        estacionSeleccionada?.resolvedUid == estacion.resolvedUid
                    ) null else estacion
                    true
                }
                mapView.overlays.add(this)
            }
        }
        mapView.invalidate()
    }

    // Gestionar el overlay de ubicación según permiso
    LaunchedEffect(tienePermiso) {
        if (tienePermiso) {
            locationOverlay.enableMyLocation()
            if (locationOverlay !in mapView.overlays) {
                mapView.overlays.add(locationOverlay)
            }
        } else {
            locationOverlay.disableMyLocation()
        }
        mapView.invalidate()
    }

    // Ciclo de vida del MapView
    DisposableEffect(context) {
        val lifecycle = (context as ComponentActivity).lifecycle
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE  -> mapView.onPause()
                else -> {}
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
            mapView.onDetach()
        }
    }

    // ─── UI ───────────────────────────────────────────────────────────────────
    Box(modifier = Modifier.fillMaxSize()) {

        // Mapa OSM
        AndroidView(factory = { mapView }, modifier = Modifier.fillMaxSize())

        // Badge de cantidad de estaciones (esquina superior derecha)
        if (estaciones.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 12.dp, end = 12.dp),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Filled.LocationOn, null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        "${estaciones.size} estaciones",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Indicador de carga
        if (userViewModel.cargandoMapa) {
            Surface(
                modifier = Modifier.align(Alignment.Center),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.5.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("Cargando estaciones…", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }

        // FAB de ubicación
        FloatingActionButton(
            onClick = {
                if (tienePermiso) {
                    locationOverlay.myLocation?.let { loc ->
                        mapView.controller.animateTo(loc)
                        mapView.controller.setZoom(13.0)
                    } ?: locationOverlay.enableFollowLocation()
                } else {
                    lanzadorPermiso.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp, end = 16.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Icon(
                if (tienePermiso) Icons.Filled.MyLocation else Icons.Filled.LocationOff,
                contentDescription = "Mi ubicación"
            )
        }

        // Tarjeta de estación seleccionada (desliza desde abajo)
        AnimatedVisibility(
            visible = estacionSeleccionada != null,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp, start = 12.dp, end = 12.dp)
        ) {
            estacionSeleccionada?.let { estacion ->
                TarjetaEstacionMapa(
                    estacion = estacion,
                    onVerDetalle = {
                        val uid = estacion.resolvedUid
                        if (uid.isNotBlank()) navController.navigate(Pantalla.Estacion.conUid(uid))
                    },
                    onCerrar = { estacionSeleccionada = null }
                )
            }
        }
    }
}

// ─── Tarjeta de info al pulsar un marcador ────────────────────────────────────

@Composable
private fun TarjetaEstacionMapa(
    estacion: EstacionResponse,
    onVerDetalle: () -> Unit,
    onCerrar: () -> Unit
) {
    val color = colorIca(estacion.ica)
    val partes = estacion.nombre.split(",")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Badge ICA
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        estacion.ica?.toString() ?: "—",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = color
                    )
                    Text(
                        "ICA",
                        fontSize = 9.sp,
                        color = color.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Nombre y etiqueta
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    partes.firstOrNull()?.trim() ?: "Estación",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
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
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(color.copy(alpha = 0.12f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        etiquetaIca(estacion.ica),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = color
                    )
                }
            }

            // Acciones
            Column(verticalArrangement = Arrangement.spacedBy(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onCerrar,
                    modifier = Modifier
                        .size(32.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                ) {
                    Icon(Icons.Filled.Close, "Cerrar", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                }
                IconButton(
                    onClick = onVerDetalle,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .size(32.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, "Ver detalle", tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

// ─── Icono de marcador generado con Canvas ────────────────────────────────────

private fun crearIconoMarcador(context: Context, ica: Int?): BitmapDrawable {
    val density = context.resources.displayMetrics.density
    val sizePx = (38 * density).toInt()
    val bmp = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val cx = sizePx / 2f
    val cy = sizePx / 2f
    val radius = cx - 2f * density

    // Sombra
    paint.color = android.graphics.Color.argb(50, 0, 0, 0)
    canvas.drawCircle(cx + density, cy + density * 1.2f, radius, paint)

    // Círculo de color ICA
    paint.color = colorIcaAndroid(ica)
    canvas.drawCircle(cx, cy, radius, paint)

    // Borde blanco
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 2.5f * density
    paint.color = android.graphics.Color.WHITE
    canvas.drawCircle(cx, cy, radius - density, paint)

    // Texto ICA
    paint.style = Paint.Style.FILL
    paint.color = android.graphics.Color.WHITE
    paint.textSize = if ((ica ?: 0) >= 100) 11f * density else 13f * density
    paint.textAlign = Paint.Align.CENTER
    paint.isFakeBoldText = true
    val text = ica?.toString() ?: "?"
    canvas.drawText(text, cx, cy - (paint.descent() + paint.ascent()) / 2f, paint)

    return BitmapDrawable(context.resources, bmp)
}

private fun colorIcaAndroid(ica: Int?): Int = when {
    ica == null -> android.graphics.Color.rgb(120, 144, 156)
    ica <= 50   -> android.graphics.Color.rgb(56, 142, 60)
    ica <= 100  -> android.graphics.Color.rgb(249, 168, 37)
    ica <= 150  -> android.graphics.Color.rgb(239, 108, 0)
    ica <= 200  -> android.graphics.Color.rgb(211, 47, 47)
    ica <= 300  -> android.graphics.Color.rgb(123, 31, 162)
    else        -> android.graphics.Color.rgb(74, 0, 0)
}

// ─── Halos de ciudad ─────────────────────────────────────────────────────────

private data class CiudadEspana(val nombre: String, val lat: Double, val lon: Double)

private val CIUDADES_ESPANA = listOf(
    CiudadEspana("Madrid",          40.4168, -3.7038),
    CiudadEspana("Barcelona",       41.3851,  2.1734),
    CiudadEspana("Valencia",        39.4699, -0.3763),
    CiudadEspana("Sevilla",         37.3891, -5.9845),
    CiudadEspana("Zaragoza",        41.6488, -0.8891),
    CiudadEspana("Málaga",          36.7213, -4.4217),
    CiudadEspana("Murcia",          37.9922, -1.1307),
    CiudadEspana("Palma",           39.5696,  2.6502),
    CiudadEspana("Bilbao",          43.2627, -2.9253),
    CiudadEspana("Alicante",        38.3452, -0.4815),
    CiudadEspana("Córdoba",         37.8882, -4.7794),
    CiudadEspana("Valladolid",      41.6523, -4.7245),
    CiudadEspana("Vigo",            42.2406, -8.7207),
    CiudadEspana("Gijón",           43.5453, -5.6627),
    CiudadEspana("Vitoria-Gasteiz", 42.8467, -2.6726),
    CiudadEspana("A Coruña",        43.3713, -8.4196),
    CiudadEspana("Granada",         37.1773, -3.5986),
    CiudadEspana("S.C. Tenerife",   28.4636, -16.2518),
    CiudadEspana("Pamplona",        42.8125, -1.6458),
    CiudadEspana("Almería",         36.8340, -2.4637),
    CiudadEspana("San Sebastián",   43.3183, -1.9812),
    CiudadEspana("Burgos",          42.3439, -3.6966),
    CiudadEspana("Huelva",          37.2614, -6.9447),
    CiudadEspana("Santander",       43.4623, -3.8099),
    CiudadEspana("Las Palmas",      28.1235, -15.4363),
    CiudadEspana("Logroño",         42.4627, -2.4449),
    CiudadEspana("Salamanca",       40.9701, -5.6635),
    CiudadEspana("Cádiz",           36.5348, -6.2989),
    CiudadEspana("Oviedo",          43.3619, -5.8494),
    CiudadEspana("Jaén",            37.7796, -3.7849),
)

// ICA de la estación más cercana a la ciudad (máx. 40 km); null si no hay datos
private fun icaCiudad(ciudad: CiudadEspana, estaciones: List<EstacionResponse>): Int? {
    val conDatos = estaciones.filter { it.ica != null && it.latDouble != null && it.lonDouble != null }
    val cercana = conDatos.minByOrNull { e ->
        val dLat = e.latDouble!! - ciudad.lat
        val dLon = e.lonDouble!! - ciudad.lon
        dLat * dLat + dLon * dLon
    } ?: return null
    val dLat = cercana.latDouble!! - ciudad.lat
    val dLon = cercana.lonDouble!! - ciudad.lon
    val distKm = Math.sqrt(dLat * dLat + dLon * dLon) * 111.0
    return if (distKm <= 40.0) cercana.ica else null
}

private fun puntosCirculo(lat: Double, lon: Double, radioMetros: Double): List<GeoPoint> {
    val n = 40
    return (0..n).map { i ->1
        val a = Math.toRadians(i * 360.0 / n)
        GeoPoint(
            lat + (radioMetros / 111_320.0) * cos(a),
            lon + (radioMetros / (111_320.0 * cos(Math.toRadians(lat)))) * sin(a)
        )
    }
}
