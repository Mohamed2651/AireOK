package com.mohammed.aireok.ui.theme

import androidx.compose.ui.graphics.Color

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

