package com.mohammed.aireok.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class Pantalla(val ruta: String) {
    object Login : Pantalla("login")
    object Home : Pantalla("home")
    object Registro : Pantalla("registro")
    object Buscar : Pantalla("buscar")
    object Mapa : Pantalla("mapa")
    object Perfil : Pantalla("perfil")
    object Estacion : Pantalla("estacion/{uid}") {
        fun conUid(uid: String) = "estacion/$uid"
    }
}

private data class ItemNav(val ruta: String, val icono: ImageVector, val etiqueta: String)

private val itemsNav = listOf(
    ItemNav(Pantalla.Home.ruta, Icons.Filled.Home, "Inicio"),
    ItemNav(Pantalla.Buscar.ruta, Icons.Filled.Search, "Buscar"),
    ItemNav(Pantalla.Mapa.ruta, Icons.Filled.Map, "Mapa"),
    ItemNav(Pantalla.Perfil.ruta, Icons.Filled.Person, "Perfil"),
)

private val rutasConShell = setOf(
    Pantalla.Home.ruta,
    Pantalla.Buscar.ruta,
    Pantalla.Mapa.ruta,
    Pantalla.Perfil.ruta
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navegacion(modifier: Modifier) {
    val navController = rememberNavController()
    val userViewModel: UserViewModel = viewModel()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val mostrarShell = currentRoute in rutasConShell
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Cerrar siempre el drawer al salir del shell (login/registro),
    // así al volver a entrar nunca aparece abierto
    LaunchedEffect(mostrarShell) {
        if (!mostrarShell) drawerState.close()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = mostrarShell && currentRoute != Pantalla.Mapa.ruta,
        drawerContent = {
            if (mostrarShell) {
                MenuLateral(
                    navController = navController,
                    userViewModel = userViewModel,
                    currentRoute = currentRoute,
                    drawerState = drawerState,
                    scope = scope
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                if (mostrarShell) {
                    AireOKTopAppBar(currentRoute = currentRoute, drawerState = drawerState, scope = scope)
                }
            },
            bottomBar = {
                if (mostrarShell) {
                    AireOKBottomNav(navController = navController, currentRoute = currentRoute)
                }
            },
            containerColor = if (mostrarShell) MaterialTheme.colorScheme.background else Color.Transparent
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Pantalla.Login.ruta,
                modifier = if (mostrarShell) Modifier.padding(innerPadding) else Modifier
            ) {
                composable(Pantalla.Login.ruta) { PantallaLogin(navController, userViewModel) }
                composable(Pantalla.Registro.ruta) { PantallaRegistro(navController, userViewModel) }
                composable(Pantalla.Home.ruta) { PantallaHome(navController, userViewModel) }
                composable(Pantalla.Buscar.ruta) { PantallaBuscar(navController, userViewModel) }
                composable(Pantalla.Mapa.ruta) { PantallaMapa(navController, userViewModel) }
                composable(Pantalla.Perfil.ruta) { PantallaPerfil(navController, userViewModel) }
                composable(
                    route = Pantalla.Estacion.ruta,
                    arguments = listOf(navArgument("uid") { type = NavType.StringType })
                ) { backStackEntry ->
                    val uid = backStackEntry.arguments?.getString("uid") ?: return@composable
                    PantallaEstacion(navController, uid)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AireOKTopAppBar(
    currentRoute: String?,
    drawerState: DrawerState,
    scope: CoroutineScope
) {
    val titulo = when (currentRoute) {
        Pantalla.Home.ruta   -> "AireOK"
        Pantalla.Buscar.ruta -> "Buscar Estaciones"
        Pantalla.Mapa.ruta   -> "Mapa"
        Pantalla.Perfil.ruta -> "Mi Perfil"
        else                 -> "AireOK"
    }
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (currentRoute == Pantalla.Home.ruta) {
                    Icon(Icons.Filled.Air, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(8.dp))
                }
                Text(titulo, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        },
        navigationIcon = {
            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                Icon(Icons.Filled.Menu, "Abrir menú", tint = MaterialTheme.colorScheme.primary)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun AireOKBottomNav(navController: NavController, currentRoute: String?) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        itemsNav.forEach { item ->
            val seleccionado = currentRoute == item.ruta
            NavigationBarItem(
                selected = seleccionado,
                onClick = {
                    if (currentRoute != item.ruta) {
                        navController.navigate(item.ruta) {
                            popUpTo(Pantalla.Home.ruta) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(item.icono, item.etiqueta) },
                label = { Text(item.etiqueta, fontSize = 11.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
private fun MenuLateral(
    navController: NavController,
    userViewModel: UserViewModel,
    currentRoute: String?,
    drawerState: DrawerState,
    scope: CoroutineScope
) {
    var mostrarCerrarSesion by remember { mutableStateOf(false) }

    if (mostrarCerrarSesion) {
        AlertDialog(
            onDismissRequest = { mostrarCerrarSesion = false },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Quieres cerrar sesión?") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarCerrarSesion = false
                    scope.launch { drawerState.close() }
                    userViewModel.logout {
                        navController.navigate(Pantalla.Login.ruta) { popUpTo(0) { inclusive = true } }
                    }
                }) { Text("Sí", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCerrarSesion = false }) { Text("Cancelar") }
            }
        )
    }

    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.width(300.dp)
    ) {
        // Cabecera con gradiente de marca (se mantiene en ambos modos)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFF0D47A1), Color(0xFF00897B))
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userViewModel.usuarioNombre.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    userViewModel.usuarioNombre.ifBlank { "Usuario" },
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                if (userViewModel.usuarioEmail.isNotBlank()) {
                    Text(
                        userViewModel.usuarioEmail,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        itemsNav.forEach { item ->
            NavigationDrawerItem(
                icon = { Icon(item.icono, null) },
                label = { Text(item.etiqueta) },
                selected = currentRoute == item.ruta,
                onClick = {
                    scope.launch { drawerState.close() }
                    if (currentRoute != item.ruta) {
                        navController.navigate(item.ruta) {
                            popUpTo(Pantalla.Home.ruta) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        NavigationDrawerItem(
            icon = { Icon(Icons.Filled.Logout, null) },
            label = { Text("Cerrar sesión") },
            selected = false,
            onClick = { mostrarCerrarSesion = true },
            colors = NavigationDrawerItemDefaults.colors(
                unselectedIconColor = MaterialTheme.colorScheme.error,
                unselectedTextColor = MaterialTheme.colorScheme.error
            ),
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        NavigationDrawerItem(
            icon = { Icon(Icons.Filled.Info, null) },
            label = { Text("Sobre AireOK") },
            selected = false,
            onClick = { scope.launch { drawerState.close() } },
            colors = NavigationDrawerItemDefaults.colors(
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}
