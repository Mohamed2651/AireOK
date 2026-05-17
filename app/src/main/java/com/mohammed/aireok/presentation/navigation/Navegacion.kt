package com.mohammed.aireok.presentation.navigation

import android.net.Uri
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.mohammed.aireok.domain.entity.auth.UserEntity
import com.mohammed.aireok.presentation.auth.login.LoginScreen
import com.mohammed.aireok.presentation.splash.SplashScreen
import com.mohammed.aireok.presentation.auth.recuperarPassword.RecuperarPasswordScreen
import com.mohammed.aireok.presentation.auth.registro.RegistroScreen
import com.mohammed.aireok.presentation.auth.resetPassword.ResetPasswordScreen
import com.mohammed.aireok.presentation.consejos.ConsejosScreen
import com.mohammed.aireok.presentation.estacion.buscar.BuscarScreen
import com.mohammed.aireok.presentation.estacion.detail.EstacionDetailScreen
import com.mohammed.aireok.presentation.estacion.home.HomeScreen
import com.mohammed.aireok.presentation.estacion.mapa.MapaScreen
import com.mohammed.aireok.presentation.favoritos.FavoritosScreen
import com.mohammed.aireok.presentation.perfil.CambiarEmailScreen
import com.mohammed.aireok.presentation.perfil.CambiarPasswordScreen
import com.mohammed.aireok.presentation.perfil.PerfilScreen
import com.mohammed.aireok.ui.theme.greenBlue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private data class ItemNav(val ruta: String, val icono: ImageVector, val etiqueta: String)

private val itemsNav = listOf(
    ItemNav(Pantalla.Home.ruta, Icons.Filled.Home, "Inicio"),
    ItemNav(Pantalla.Buscar.ruta, Icons.Filled.Search, "Buscar"),
    ItemNav(Pantalla.Mapa.ruta, Icons.Filled.Map, "Mapa"),
    ItemNav(Pantalla.Perfil.ruta, Icons.Filled.Person, "Perfil"),
)

private val rutasConShell = setOf(
    Pantalla.Home.ruta, Pantalla.Buscar.ruta, Pantalla.Mapa.ruta,
    Pantalla.Perfil.ruta, Pantalla.Consejos.ruta, Pantalla.Favoritos.ruta
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navegacion(
    modifier: Modifier,
    pendingResetToken: String? = null,
    onNavControllerReady: (NavController) -> Unit = {}
) {
    val navController = rememberNavController()
    LaunchedEffect(navController) { onNavControllerReady(navController) }

    val sessionViewModel: SessionViewModel = hiltViewModel()
    val currentUser by sessionViewModel.currentUser.collectAsState()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val mostrarShell = currentRoute in rutasConShell
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(currentRoute) { drawerState.close() }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = mostrarShell && currentRoute != Pantalla.Mapa.ruta,
        drawerContent = {
            if (mostrarShell) {
                MenuLateral(
                    navController = navController,
                    currentUser = currentUser,
                    currentRoute = currentRoute,
                    drawerState = drawerState,
                    scope = scope,
                    onLogout = {
                        navController.navigate(Pantalla.Login.ruta) { popUpTo(0) { inclusive = true } }
                        sessionViewModel.logout()
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = { if (mostrarShell) AireOKTopAppBar(currentRoute, drawerState, scope) },
            bottomBar = { if (mostrarShell) AireOKBottomNav(navController, currentRoute) },
            containerColor = if (mostrarShell) MaterialTheme.colorScheme.background else Color.Transparent
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = if (pendingResetToken != null)
                    "reset-password?token=${Uri.encode(pendingResetToken)}"
                else
                    Pantalla.Splash.ruta,
                modifier = if (mostrarShell) Modifier.padding(innerPadding) else Modifier
            ) {
                composable(Pantalla.Splash.ruta) {
                    SplashScreen(navController = navController)
                }
                composable(Pantalla.Login.ruta) {
                    LoginScreen(navController = navController)
                }
                composable(Pantalla.Registro.ruta) {
                    RegistroScreen(navController = navController)
                }
                composable(Pantalla.RecuperarPassword.ruta) {
                    RecuperarPasswordScreen(navController = navController)
                }
                composable(
                    route = "reset-password?token={token}",
                    arguments = listOf(navArgument("token") {
                        type = NavType.StringType; defaultValue = ""
                    }),
                    deepLinks = listOf(navDeepLink { uriPattern = "aireok://reset-password?token={token}" })
                ) {
                    ResetPasswordScreen(navController = navController)
                }
                composable(Pantalla.Home.ruta) {
                    HomeScreen(navController = navController, currentUser = currentUser)
                }
                composable(Pantalla.Buscar.ruta) {
                    BuscarScreen(navController = navController)
                }
                composable(Pantalla.Mapa.ruta) {
                    MapaScreen(navController = navController)
                }
                composable(Pantalla.Perfil.ruta) {
                    PerfilScreen(
                        navController = navController,
                        onLogout = {
                            navController.navigate(Pantalla.Login.ruta) { popUpTo(0) { inclusive = true } }
                            sessionViewModel.logout()
                        }
                    )
                }
                composable(Pantalla.CambiarEmail.ruta) {
                    CambiarEmailScreen(navController = navController)
                }
                composable(Pantalla.CambiarPassword.ruta) {
                    CambiarPasswordScreen(navController = navController)
                }
                composable(Pantalla.Consejos.ruta) { ConsejosScreen() }
                composable(Pantalla.Favoritos.ruta) {
                    FavoritosScreen(navController = navController)
                }
                composable(
                    route = Pantalla.Estacion.ruta,
                    arguments = listOf(navArgument("uid") { type = NavType.StringType })
                ) {
                    EstacionDetailScreen(navController = navController)
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
        Pantalla.Home.ruta      -> "AireOK"
        Pantalla.Buscar.ruta    -> "Buscar Estaciones"
        Pantalla.Mapa.ruta      -> "Mapa"
        Pantalla.Perfil.ruta    -> "Mi Perfil"
        Pantalla.Consejos.ruta  -> "Consejos Ecológicos"
        Pantalla.Favoritos.ruta -> "Favoritos"
        else                    -> "AireOK"
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
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
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
                onClick = { if (currentRoute != item.ruta) navToTab(navController, item.ruta) },
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

private fun navToTab(navController: NavController, ruta: String) {
    if (ruta == Pantalla.Home.ruta) {
        if (!navController.popBackStack(Pantalla.Home.ruta, inclusive = false)) {
            navController.navigate(Pantalla.Home.ruta)
        }
    } else {
        navController.navigate(ruta) {
            popUpTo(Pantalla.Home.ruta) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }
}

@Composable
private fun MenuLateral(
    navController: NavController,
    currentUser: UserEntity?,
    currentRoute: String?,
    drawerState: DrawerState,
    scope: CoroutineScope,
    onLogout: () -> Unit
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
                    onLogout()
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(listOf(Color(0xFF0D47A1), greenBlue))
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
                        text = currentUser?.nombre?.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    currentUser?.nombre?.ifBlank { "Usuario" } ?: "Usuario",
                    color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp
                )
                if (!currentUser?.email.isNullOrBlank()) {
                    Text(currentUser!!.email, color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
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
                    if (currentRoute != item.ruta) navToTab(navController, item.ruta)
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
            icon = { Icon(Icons.Filled.Eco, null) },
            label = { Text("Consejos Ecológicos") },
            selected = currentRoute == Pantalla.Consejos.ruta,
            onClick = {
                scope.launch { drawerState.close() }
                if (currentRoute != Pantalla.Consejos.ruta) {
                    navController.navigate(Pantalla.Consejos.ruta) {
                        popUpTo(Pantalla.Home.ruta) { saveState = true }
                        launchSingleTop = true; restoreState = true
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

        NavigationDrawerItem(
            icon = { Icon(Icons.Filled.Favorite, null) },
            label = { Text("Favoritos") },
            selected = currentRoute == Pantalla.Favoritos.ruta,
            onClick = {
                scope.launch { drawerState.close() }
                if (currentRoute != Pantalla.Favoritos.ruta) {
                    navController.navigate(Pantalla.Favoritos.ruta) {
                        popUpTo(Pantalla.Home.ruta) { saveState = true }
                        launchSingleTop = true; restoreState = true
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
    }
}
