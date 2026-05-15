package com.mohammed.aireok.presentation.consejos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohammed.aireok.ui.theme.orange

@Composable
fun ConsejosScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(Color(0xFF1B5E20), Color(0xFF00695C))), shape = RoundedCornerShape(20.dp))
                    .padding(24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Cuida el Planeta", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text("Pequeños cambios, gran impacto\npara el medioambiente", color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp, lineHeight = 20.sp)
                    }
                    Box(
                        modifier = Modifier.size(60.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Eco, null, tint = Color.White, modifier = Modifier.size(36.dp))
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        CategoriaConsejos(
            icono = Icons.Filled.Air, titulo = "Mejora la Calidad del Aire", color = Color(0xFF1565C0),
            consejos = listOf(
                "Evita quemar residuos al aire libre",
                "No dejes el motor del coche encendido en parado",
                "Reduce el uso de aerosoles y productos con VOCs",
                "Usa la calefacción solo cuando sea necesario",
                "Planta árboles: absorben CO₂ y filtran partículas",
                "Apoya las iniciativas de energía renovable en tu zona"
            )
        )
        Spacer(Modifier.height(12.dp))
        CategoriaConsejos(
            icono = Icons.Filled.Recycling, titulo = "Recicla Correctamente", color = Color(0xFF2E7D32),
            consejos = listOf(
                "Separa papel, plástico, vidrio y residuos orgánicos",
                "Lleva pilas y electrónicos al punto limpio",
                "Reutiliza bolsas y envases siempre que puedas",
                "Composta los restos de comida en casa",
                "Elige productos con poco o sin embalaje",
                "Dona ropa y objetos en lugar de tirarlos a la basura"
            )
        )
        Spacer(Modifier.height(12.dp))
        CategoriaConsejos(
            icono = Icons.Filled.Bolt, titulo = "Ahorra Energía", color = orange,
            consejos = listOf(
                "Apaga las luces al salir de cada habitación",
                "Sustituye bombillas por LEDs de bajo consumo",
                "Desconecta los aparatos que no uses del enchufe",
                "Mantén el termostato entre 19 °C y 21 °C en invierno",
                "Aprovecha la luz natural durante el día",
                "Lava la ropa en frío y espera a tener carga completa"
            )
        )
        Spacer(Modifier.height(12.dp))
        CategoriaConsejos(
            icono = Icons.Filled.DirectionsBike, titulo = "Movilidad Sostenible", color = Color(0xFF6A1B9A),
            consejos = listOf(
                "Usa el transporte público siempre que sea posible",
                "Comparte el coche con compañeros de trabajo",
                "Elige la bicicleta o patinete para trayectos cortos",
                "Considera vehículos eléctricos o híbridos",
                "Camina: es gratis, saludable y sin emisiones",
                "Planifica rutas eficientes para reducir el consumo"
            )
        )
        Spacer(Modifier.height(12.dp))
        CategoriaConsejos(
            icono = Icons.Filled.WaterDrop, titulo = "Cuida el Agua", color = Color(0xFF00838F),
            consejos = listOf(
                "Cierra el grifo mientras te cepillas los dientes",
                "Repara fugas y grifos que gotean sin esperar",
                "Dúchate en lugar de bañarte para ahorrar hasta 150 L",
                "Usa el lavavajillas solo cuando esté lleno",
                "Riega las plantas al amanecer o al atardecer",
                "Recoge agua de lluvia para regar el jardín"
            )
        )
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun CategoriaConsejos(icono: ImageVector, titulo: String, color: Color, consejos: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)).background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icono, null, tint = color, modifier = Modifier.size(24.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text(titulo, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(Modifier.height(10.dp))
            consejos.forEach { consejo ->
                Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
                    Box(modifier = Modifier.padding(top = 5.dp).size(7.dp).clip(CircleShape).background(color))
                    Spacer(Modifier.width(10.dp))
                    Text(consejo, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 18.sp)
                }
            }
        }
    }
}
