import android.content.Context
import java.io.FileOutputStream

object DataBaseCopier {

    fun copiar(context: Context, nombreDB: String) {
        val rutaDB = context.getDatabasePath(nombreDB)

        if (rutaDB.exists()) return

        // Crear carpeta si no existe
        rutaDB.parentFile?.mkdirs()

        // Abrir assets
        val input = context.assets.open(nombreDB)
        val output = FileOutputStream(rutaDB, false) // false = sobrescribir

        val buffer = ByteArray(1024)
        var length: Int
        while (input.read(buffer).also { length = it } > 0) {
            output.write(buffer, 0, length)
        }

        output.flush()
        output.close()
        input.close()
    }

    fun insertarUsuario(
        context: Context,
        nombre: String,
        email: String,
        password: String,
        rol: String = "registrado"
    ): Boolean {

        return try {
            val db = context.openOrCreateDatabase("mi_base_datos.sqlite", Context.MODE_PRIVATE, null)
            val insertSQL = """
            INSERT INTO usuarios (nombre, email, contrasena_hash, rol)
            VALUES (?, ?, ?, ?)
        """.trimIndent()

            db.execSQL(insertSQL, arrayOf(nombre, email, password, rol))
            db.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

}
