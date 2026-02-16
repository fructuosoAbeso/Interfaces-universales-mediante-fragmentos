package es.ua.eps.filmoteca

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.android.material.appbar.MaterialToolbar

// -------------------------------------------------------------------
// MODO: Cambia entre XML (Layouts) y Compose
// ------------------------------------------------------------------
class FilmListActivity : AppCompatActivity() {

    private val mode = Mode.Layouts   // Cambiar a Compose si deseas

    // Lista de películas observable
    private val films = FilmDataSource.films

    // ActionMode para borrado múltiple (solo XML)
    private var actionMode: ActionMode? = null
    private val selectedItems = mutableSetOf<Int>()

    // -------------------------------------------------------------------
    // Activity
    // -------------------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
    }

    private fun initUI() {
        when (mode) {
            Mode.Layouts -> initLayouts()
            Mode.Compose -> initCompose()
        }
    }

    // -------------------------------------------------------------------
    // XML - ListView + ActionMode
    // -------------------------------------------------------------------
    private fun initLayouts() {
        setContentView(R.layout.activity_film_list)

        // Toolbar azul
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val listView = findViewById<ListView>(R.id.listPeliculas)
        val adapter = FilmAdapter(this, films)
        listView.adapter = adapter

        // Click
        listView.setOnItemClickListener { _, _, position, _ ->
            if (actionMode != null) {
                toggleSelection(position)
            } else {
                val intent = Intent(this, FilmDataActivity::class.java)
                intent.putExtra(FilmDataActivity.EXTRA_FILM_INDEX, position)
                startActivity(intent)
            }
        }

        // LongClick -> ActionMode
        listView.setOnItemLongClickListener { _, _, position, _ ->
            if (actionMode == null) actionMode = startSupportActionMode(actionModeCallback)
            toggleSelection(position)
            true
        }
    }

    private fun toggleSelection(position: Int) {
        if (selectedItems.contains(position)) selectedItems.remove(position)
        else selectedItems.add(position)

        actionMode?.title = "${selectedItems.size} seleccionados"
        (findViewById<ListView>(R.id.listPeliculas).adapter as? FilmAdapter)?.notifyDataSetChanged()

        if (selectedItems.isEmpty()) actionMode?.finish()
    }

    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            menuInflater.inflate(R.menu.menu_delete, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?) = false

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            if (item?.itemId == R.id.action_delete) {
                selectedItems.sortedDescending().forEach { films.removeAt(it) }
                selectedItems.clear()
                (findViewById<ListView>(R.id.listPeliculas).adapter as? FilmAdapter)?.notifyDataSetChanged()
                mode?.finish()
                return true
            }
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            selectedItems.clear()
            actionMode = null
            (findViewById<ListView>(R.id.listPeliculas).adapter as? FilmAdapter)?.notifyDataSetChanged()
        }
    }

    // -------------------------------------------------------------------
    // Menú
    // -------------------------------------------------------------------
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_films, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        // Botón HOME
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }

        when (item.itemId) {

            R.id.action_add_film -> {
                val newFilm = Film().apply {
                    title = "Nueva película"
                    director = "Director desconocido"
                    year = 2024
                    genre = Film.GENRE_DRAMA
                    format = Film.FORMAT_DIGITAL
                    comments = ""
                    imdbUrl = ""
                    imageResId = R.mipmap.ic_launcher
                }

                films.add(newFilm)

                if (mode == Mode.Layouts) {
                    (findViewById<ListView>(R.id.listPeliculas).adapter as? FilmAdapter)?.notifyDataSetChanged()
                }

                return true
            }

            R.id.action_about -> {
                startActivity(Intent(this, AboutActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // -------------------------------------------------------------------
    // COMPOSE
    // -------------------------------------------------------------------
    private fun initCompose() {
        setContent {
            FilmotecaTheme {
                FilmListScreen()
            }
        }
    }

    // Tema Compose
    @Composable
    fun FilmotecaTheme(darkTheme: Boolean = true, content: @Composable () -> Unit) {
        val colors = if (darkTheme) DarkColors else LightColors
        MaterialTheme(colorScheme = colors, content = content)
    }

    private val DarkColors = darkColorScheme(
        primary = Color(0xFF2196F3),
        onPrimary = Color.White,
        background = Color(0xFF121212),
        onBackground = Color.White,
        surface = Color(0xFF1E1E1E),
        onSurface = Color.White
    )

    private val LightColors = lightColorScheme()

    // Item Compose
    @Composable
    fun FilmItem(film: Film, onClick: () -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(onClick = onClick, onLongClick = {}),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = if (film.imageResId != 0) film.imageResId else R.mipmap.ic_launcher),
                contentDescription = film.title,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = film.title ?: "<Sin título>", style = MaterialTheme.typography.titleMedium)
                Text(text = "Director: ${film.director}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Año: ${film.year}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }

    // Lista Compose
    @Composable
    fun FilmListScreen() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            LazyColumn {
                itemsIndexed(films) { index, film ->
                    FilmItem(film) {
                        val intent = Intent(this@FilmListActivity, FilmDataActivity::class.java)
                        intent.putExtra(FilmDataActivity.EXTRA_FILM_INDEX, index)
                        startActivity(intent)
                    }
                }
            }
        }
    }
}
