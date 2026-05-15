package com.mohammed.aireok.presentation.common

import androidx.compose.ui.graphics.Color
import com.mohammed.aireok.ui.theme.green
import com.mohammed.aireok.ui.theme.grey
import com.mohammed.aireok.ui.theme.orangeRed
import com.mohammed.aireok.ui.theme.orangeYellow
import com.mohammed.aireok.ui.theme.red
import com.mohammed.aireok.ui.theme.violet
import com.mohammed.aireok.ui.theme.yellow

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
