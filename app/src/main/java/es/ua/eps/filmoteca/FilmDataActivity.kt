package es.ua.eps.filmoteca

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class FilmDataActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_FILM_INDEX = "EXTRA_FILM_INDEX"
    }

    // MODO: Cambia a Mode.Compose para ver la versión de Compose
    private val mode = Mode.Layouts

    // Configuración de colores para Compose (Azul y Blanco)
    private val DarkColors = darkColorScheme(
        primary = Color(0xFF2196F3), // Azul AppBar
        onPrimary = Color.White,
        background = Color(0xFF121212),
        onBackground = Color.White
    )

    private lateinit var film: Film
    private var filmIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Configuración de la ActionBar (Azul por el Tema)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // 2. Obtener datos de la película
        filmIndex = intent.getIntExtra(EXTRA_FILM_INDEX, -1)
        if (filmIndex in FilmDataSource.films.indices) {
            film = FilmDataSource.films[filmIndex]
        } else {
            finish()
            return
        }

        initUI()
    }

    // 3. Navegación Up: Regresa a la lista principal
    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this, FilmListActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        return true
    }

    private fun initUI() {
        when (mode) {
            Mode.Layouts -> initLayouts()
            Mode.Compose -> initCompose()
        }
    }

    // ----------------------------
    // VERSIÓN XML (LAYOUTS)
    // ----------------------------
    private fun initLayouts() {
        setContentView(R.layout.activity_film_data)
        supportActionBar?.title = "Detalles (XML)"

        val imgCartel = findViewById<ImageView>(R.id.imgCartel)
        val tvNombre = findViewById<TextView>(R.id.tvNombrePelicula)
        val tvDirector = findViewById<TextView>(R.id.tvDirector)
        val tvAnyo = findViewById<TextView>(R.id.tvAnyo)
        val tvGenero = findViewById<TextView>(R.id.tvGenero)
        val tvFormato = findViewById<TextView>(R.id.tvFormato)

        // Asignar datos
        imgCartel.setImageResource(if (film.imageResId != 0) film.imageResId else R.mipmap.ic_launcher)
        tvNombre.text = film.title
        tvDirector.text = "Director: ${film.director}"
        tvAnyo.text = "Año: ${film.year}"
        tvGenero.text = "Género: ${genreToString(film.genre)}"
        tvFormato.text = "Formato: ${formatToString(film.format)}"

        // Botón IMDB
        findViewById<Button>(R.id.btnVerImdb).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(film.imdbUrl))
            startActivity(intent)
        }

        // Botón Editar
        findViewById<Button>(R.id.btnEditarPelicula).setOnClickListener {
            val intent = Intent(this, FilmEditActivity::class.java)
            intent.putExtra(EXTRA_FILM_INDEX, filmIndex)
            startActivity(intent)
        }

        // Botón Volver
        findViewById<Button>(R.id.btnVolverPrincipal).setOnClickListener {
            finish()
        }
    }

    // ----------------------------
    // VERSIÓN COMPOSE
    // ----------------------------
    private fun initCompose() {
        supportActionBar?.title = "Detalles (Compose)"
        setContent {
            MaterialTheme(colorScheme = DarkColors) {
                FilmDataScreen()
            }
        }
    }

    @Composable
    fun FilmDataScreen() {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.Top) {
                    Image(
                        painter = painterResource(id = film.imageResId),
                        contentDescription = "Cartel",
                        modifier = Modifier.size(width = 130.dp, height = 190.dp),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(text = film.title ?: "", fontSize = 22.sp, color = Color.White)
                        Text(text = "Director: ${film.director}", fontSize = 16.sp, color = Color.LightGray)
                        Text(text = "Año: ${film.year}", fontSize = 16.sp, color = Color.LightGray)
                        Text(text = "Género: ${genreToString(film.genre)}", fontSize = 16.sp, color = Color.LightGray)
                        Text(text = "Formato: ${formatToString(film.format)}", fontSize = 16.sp, color = Color.LightGray)

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(film.imdbUrl))
                                startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                        ) {
                            Text("Ver en IMDb")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val intent = Intent(this@FilmDataActivity, FilmEditActivity::class.java)
                        intent.putExtra(EXTRA_FILM_INDEX, filmIndex)
                        startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Editar Película")
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { finish() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Volver")
                }
            }
        }
    }

    private fun genreToString(genre: Int) = when (genre) {
        Film.GENRE_ACTION -> "Acción"
        Film.GENRE_COMEDY -> "Comedia"
        Film.GENRE_DRAMA -> "Drama"
        Film.GENRE_SCIFI -> "Ciencia Ficción"
        Film.GENRE_HORROR -> "Terror"
        else -> "Desconocido"
    }

    private fun formatToString(format: Int) = when (format) {
        Film.FORMAT_DVD -> "DVD"
        Film.FORMAT_BLURAY -> "BluRay"
        Film.FORMAT_DIGITAL -> "Digital"
        else -> "Desconocido"
    }
}