package es.ua.eps.filmoteca

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), FilmListFragment.OnFilmSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Verificamos si estamos en pantallas pequeñas (FrameLayout existe)
        val isMobile = findViewById<View>(R.id.main_content) != null

        if (isMobile && savedInstanceState == null) {
            // Inicialmente mostramos la lista (Ejercicio 1)
            supportFragmentManager.beginTransaction()
                .add(R.id.main_content, FilmListFragment())
                .commit()
        }
    }

    fun navegarAEditar(index: Int) {
        val editFrag = FilmEditFragment.newInstance(index)

        // Si existe 'data_fragment', estamos en Tablet y editamos en el panel derecho
        val containerId = if (findViewById<View>(R.id.data_fragment) != null) {
            R.id.data_fragment
        } else {
            R.id.main_content // En móvil usamos toda la pantalla
        }

        supportFragmentManager.beginTransaction()
            .replace(containerId, editFrag)
            .addToBackStack(null) // Permite volver al detalle con el botón atrás
            .commit()
    }

    override fun onFilmSelected(position: Int) {
        // Buscamos si el fragmento de datos está cargado estáticamente (Tablet)
        val detailFrag = supportFragmentManager.findFragmentById(R.id.data_fragment) as? FilmDataFragment

        if (detailFrag != null && detailFrag.isInLayout) {
            // Pantalla GRANDE: Actualizamos usando el método público (Ejercicio 2)
            detailFrag.setFilm(position)
        } else {
            // Pantalla PEQUEÑA: Transición al fragmento de datos (Ejercicio 2)
            val newFrag = FilmDataFragment.newInstance(position)
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_content, newFrag)
                .addToBackStack(null) // Para poder volver a la lista
                .commit()
        }
    }
}