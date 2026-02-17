package es.ua.eps.filmoteca

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ListView
import androidx.fragment.app.ListFragment

class FilmListFragment : ListFragment() {

    interface OnFilmSelectedListener {
        fun onFilmSelected(position: Int)
    }

    private var callback: OnFilmSelectedListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as? OnFilmSelectedListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Usamos el adaptador que ya tienes creado
        val miAdaptador = FilmAdapter(requireContext(), FilmDataSource.films)

        // Lo asignamos a la lista del ListFragment
        this.listAdapter = miAdaptador
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        // EJERCICIO 2: Avisar a la actividad de la pulsación
        callback?.onFilmSelected(position)
    }
}