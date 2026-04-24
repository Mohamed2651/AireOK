package com.mohammed.aireok.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohammed.aireok.network.EstacionResponse

fun colorIca(ica: Int?): Color = when {
    ica == null -> grey
    ica <= 50   -> green
    ica <= 100  -> yellow
    ica <= 150  -> orangeYellow
    ica <= 200  -> orangeRed
    ica <= 300  -> violet
    else        -> red
}

fun etiquetaIca(ica: Int?): String = when {
    ica == null -> "Sin datos"
    ica <= 50   -> "Bueno"
    ica <= 100  -> "Moderado"
    ica <= 150  -> "Dañino sensibles"
    ica <= 200  -> "Dañino"
    ica <= 300  -> "Muy dañino"
    else        -> "Peligroso"
}

