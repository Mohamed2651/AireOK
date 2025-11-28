import android.content.Context
import java.io.FileOutputStream

object DataBaseCopier {

    fun copiar(context: Context, nombreDB: String) {
        val rutaDB = context.getDatabasePath(nombreDB)

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
}
