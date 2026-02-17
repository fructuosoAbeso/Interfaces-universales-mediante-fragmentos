package es.ua.eps.filmoteca

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

class FilmDataFragment : Fragment() {

    // Guardamos el índice actual para que no se pierda al rotar
    private var currentFilmIndex: Int = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflamos el layout (Android elegirá automáticamente el normal o el land)
        return inflater.inflate(R.layout.activity_film_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Detectamos en qué modo estamos
        val isTablet = activity?.findViewById<View>(R.id.list_fragment) != null

        // 2. Botón VOLVER / CERRAR
        val btnVolver = view.findViewById<Button>(R.id.btnVolverPrincipal)
        if (isTablet) {
            // En Tablet, el botón "Cerrar" no suele ser necesario o se oculta
            btnVolver?.visibility = View.GONE
        } else {
            btnVolver?.visibility = View.VISIBLE
            btnVolver?.setOnClickListener {
                parentFragmentManager.popBackStack() // Vuelve a la lista en móvil
            }
        }

        // 3. Botón EDITAR
        view.findViewById<Button>(R.id.btnEditarPelicula)?.setOnClickListener {
            if (currentFilmIndex != -1) {
                (activity as? MainActivity)?.navegarAEditar(currentFilmIndex)
            }
        }

        // 4. Botón IMDB
        view.findViewById<Button>(R.id.btnVerImdb)?.setOnClickListener {
            val film = FilmDataSource.films.getOrNull(currentFilmIndex)
            film?.imdbUrl?.let { url ->
                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
                startActivity(intent)
            }
        }

        // Recuperar datos si venimos de una rotación
        val index = arguments?.getInt("INDEX", -1) ?: -1
        if (index != -1) setFilm(index)
    }

    fun setFilm(index: Int) {
        currentFilmIndex = index
        // Actualizamos los argumentos para que, si rotamos, el fragmento sepa qué película mostrar
        arguments?.putInt("INDEX", index)

        val film = FilmDataSource.films.getOrNull(index) ?: return

        // Importante: Usamos view?. para asegurar que la vista existe
        view?.let { v ->
            v.findViewById<TextView>(R.id.tvNombrePelicula)?.text = film.title
            v.findViewById<TextView>(R.id.tvDirector)?.text = "Director: ${film.director}"
            v.findViewById<TextView>(R.id.tvAnyo)?.text = "Año: ${film.year}"
            v.findViewById<TextView>(R.id.tvGenero)?.text = "Género: ${film.genre}" // O usa el array de strings
            v.findViewById<TextView>(R.id.tvFormato)?.text = "Formato: ${film.format}"

            val imgCartel = v.findViewById<ImageView>(R.id.imgCartel)
            imgCartel?.setImageResource(if (film.imageResId != 0) film.imageResId else R.mipmap.ic_launcher)
        }
    }

    companion object {
        fun newInstance(index: Int): FilmDataFragment {
            val f = FilmDataFragment()
            val args = Bundle()
            args.putInt("INDEX", index)
            f.arguments = args
            return f
        }
    }
}