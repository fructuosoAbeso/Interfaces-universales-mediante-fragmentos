package es.ua.eps.filmoteca

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment

class FilmEditFragment : Fragment() {

    private lateinit var film: Film
    private var filmIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Recuperamos el índice de los argumentos
        filmIndex = arguments?.getInt(ARG_FILM_INDEX, -1) ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflamos el layout XML: activity_film_edit.xml
        val view = inflater.inflate(R.layout.activity_film_edit, container, false)

        if (filmIndex in FilmDataSource.films.indices) {
            film = FilmDataSource.films[filmIndex]
            initLayout(view)
        }

        return view
    }

    private fun initLayout(view: View) {
        val etTitulo = view.findViewById<EditText>(R.id.etTitulo)
        val etDirector = view.findViewById<EditText>(R.id.etDirector)
        val etAnyo = view.findViewById<EditText>(R.id.etAnyo)
        val spinnerGenero = view.findViewById<Spinner>(R.id.spinnerGenero)
        val spinnerFormato = view.findViewById<Spinner>(R.id.spinnerFormato)
        val etImdb = view.findViewById<EditText>(R.id.etImdb)
        val etNotas = view.findViewById<EditText>(R.id.etNotas)
        val imgCartel = view.findViewById<ImageView>(R.id.imgCartel)

        // Cargar datos actuales de la película
        etTitulo.setText(film.title)
        etDirector.setText(film.director)
        etAnyo.setText(film.year.toString())
        spinnerGenero.setSelection(film.genre)
        spinnerFormato.setSelection(film.format)
        etImdb.setText(film.imdbUrl)
        etNotas.setText(film.comments)
        imgCartel.setImageResource(if (film.imageResId != 0) film.imageResId else R.mipmap.ic_launcher)

        view.findViewById<Button>(R.id.btnGuardar).setOnClickListener {
            saveFilmData(
                etTitulo.text.toString(), etDirector.text.toString(),
                etAnyo.text.toString(), spinnerGenero.selectedItemPosition,
                spinnerFormato.selectedItemPosition, etImdb.text.toString(),
                etNotas.text.toString()
            )
            // Volver atrás en la pila de fragmentos
            parentFragmentManager.popBackStack()
        }

        view.findViewById<Button>(R.id.btnCancelar).setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun saveFilmData(t: String, d: String, a: String, g: Int, f: Int, i: String, n: String) {
        film.title = t
        film.director = d
        film.year = a.toIntOrNull() ?: film.year
        film.genre = g
        film.format = f
        film.imdbUrl = i
        film.comments = n
    }

    companion object {
        private const val ARG_FILM_INDEX = "film_index"

        fun newInstance(index: Int): FilmEditFragment {
            val fragment = FilmEditFragment()
            val args = Bundle()
            args.putInt(ARG_FILM_INDEX, index)
            fragment.arguments = args
            return fragment
        }
    }
}