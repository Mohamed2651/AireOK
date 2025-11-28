package com.mohammed.aireok.ui.theme

import android.content.Context

data class Usuario(
    val id: Int,
    val nombre: String,
    val email: String,
    val rol: String
)

object DBTest {

    fun obtenerUsuarios(context: Context): List<Usuario> {
        val lista = mutableListOf<Usuario>()

        val db = context.openOrCreateDatabase("mi_base_datos.sqlite", Context.MODE_PRIVATE, null)
        val cursor = db.rawQuery("SELECT id, nombre, email, rol FROM usuarios", null)

        while (cursor.moveToNext()) {
            lista.add(
                Usuario(
                    id = cursor.getInt(0),
                    nombre = cursor.getString(1),
                    email = cursor.getString(2),
                    rol = cursor.getString(3)
                )
            )
        }

        cursor.close()
        db.close()

        return lista
    }
    fun validarUsuario(context: Context, nombre: String, contrasena: String): Boolean {
        val db = context.openOrCreateDatabase("mi_base_datos.sqlite", Context.MODE_PRIVATE, null)
        val cursor = db.rawQuery(
            "SELECT id FROM usuarios WHERE nombre = ? AND contrasena_hash = ?",
            arrayOf(nombre, contrasena)
        )
        val existe = cursor.moveToFirst() // true si hay al menos un resultado
        cursor.close()
        db.close()
        return existe
    }
}





