package es.ua.eps.filmoteca

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class FilmEditActivity : AppCompatActivity() {

    private val mode = Mode.Layouts
    private lateinit var film: Film

    // Colores unificados (Azul y Blanco)
    private val DarkColors = darkColorScheme(
        primary = Color(0xFF2196F3),
        onPrimary = Color.White,
        background = Color(0xFF121212),
        onBackground = Color.White,
        surface = Color(0xFF1E1E1E),
        onSurface = Color.White
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Configuración de la barra superior (se verá azul por el tema)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val index = intent.getIntExtra(FilmDataActivity.EXTRA_FILM_INDEX, -1)
        if (index !in FilmDataSource.films.indices) {
            finish()
            return
        }
        film = FilmDataSource.films[index]

        initUI()
    }

    // 2. Navegación al pulsar la flecha de la barra azul
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

    // -------------------------
    // VERSIÓN XML
    // -------------------------
    private fun initLayouts() {
        setContentView(R.layout.activity_film_edit)
        supportActionBar?.title = "Editar (XML)"

        val etTitulo = findViewById<EditText>(R.id.etTitulo)
        val etDirector = findViewById<EditText>(R.id.etDirector)
        val etAnyo = findViewById<EditText>(R.id.etAnyo)
        val spinnerGenero = findViewById<Spinner>(R.id.spinnerGenero)
        val spinnerFormato = findViewById<Spinner>(R.id.spinnerFormato)
        val etImdb = findViewById<EditText>(R.id.etImdb)
        val etNotas = findViewById<EditText>(R.id.etNotas)
        val imgCartel = findViewById<ImageView>(R.id.imgCartel)

        // Cargar datos actuales
        etTitulo.setText(film.title)
        etDirector.setText(film.director)
        etAnyo.setText(film.year.toString())
        spinnerGenero.setSelection(film.genre)
        spinnerFormato.setSelection(film.format)
        etImdb.setText(film.imdbUrl)
        etNotas.setText(film.comments)
        imgCartel.setImageResource(if (film.imageResId != 0) film.imageResId else R.mipmap.ic_launcher)

        findViewById<Button>(R.id.btnGuardar).setOnClickListener {
            saveFilmData(
                etTitulo.text.toString(), etDirector.text.toString(),
                etAnyo.text.toString(), spinnerGenero.selectedItemPosition,
                spinnerFormato.selectedItemPosition, etImdb.text.toString(),
                etNotas.text.toString()
            )
            setResult(RESULT_OK)
            finish()
        }

        findViewById<Button>(R.id.btnCancelar).setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
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

    // -------------------------
    // VERSIÓN COMPOSE
    // -------------------------
    private fun initCompose() {
        supportActionBar?.title = "Editar (Compose)"
        setContent {
            MaterialTheme(colorScheme = DarkColors) {
                FilmEditScreen(
                    film = film,
                    onGuardar = { setResult(RESULT_OK); finish() },
                    onCancelar = { setResult(RESULT_CANCELED); finish() }
                )
            }
        }
    }

    @Composable
    fun FilmEditScreen(film: Film, onGuardar: () -> Unit, onCancelar: () -> Unit) {
        val scrollState = rememberScrollState()

        var titulo by remember { mutableStateOf(film.title ?: "") }
        var director by remember { mutableStateOf(film.director ?: "") }
        var anyo by remember { mutableStateOf(film.year.toString()) }
        var imdb by remember { mutableStateOf(film.imdbUrl ?: "") }
        var notas by remember { mutableStateOf(film.comments ?: "") }

        val generos = stringArrayResource(id = R.array.array_genero).toList()
        val formatos = stringArrayResource(id = R.array.array_formato).toList()

        var generoExpanded by remember { mutableStateOf(false) }
        var generoSeleccionado by remember { mutableStateOf(generos.getOrElse(film.genre) { generos.first() }) }

        var formatoExpanded by remember { mutableStateOf(false) }
        var formatoSeleccionado by remember { mutableStateOf(formatos.getOrElse(film.format) { formatos.first() }) }

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Editando película", fontSize = 24.sp, color = Color.White)
            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = if (film.imageResId != 0) film.imageResId else R.mipmap.ic_launcher),
                    contentDescription = null,
                    modifier = Modifier.size(width = 110.dp, height = 160.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Button(onClick = { }, modifier = Modifier.fillMaxWidth()) { Text("Foto") }
                    Button(onClick = { }, modifier = Modifier.fillMaxWidth()) { Text("Galería") }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = titulo, onValueChange = { titulo = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = director, onValueChange = { director = it }, label = { Text("Director") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = anyo, onValueChange = { anyo = it }, label = { Text("Año") }, modifier = Modifier.fillMaxWidth())

            // Spinner Género
            Box(modifier = Modifier.padding(top = 8.dp)) {
                OutlinedTextField(
                    value = generoSeleccionado, onValueChange = {}, readOnly = true,
                    label = { Text("Género") }, modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { Icon(painterResource(android.R.drawable.arrow_down_float), null, Modifier.clickable { generoExpanded = true }) }
                )
                DropdownMenu(expanded = generoExpanded, onDismissRequest = { generoExpanded = false }) {
                    generos.forEach { g ->
                        DropdownMenuItem(text = { Text(g) }, onClick = { generoSeleccionado = g; generoExpanded = false })
                    }
                }
            }

            // Spinner Formato
            Box(modifier = Modifier.padding(top = 8.dp)) {
                OutlinedTextField(
                    value = formatoSeleccionado, onValueChange = {}, readOnly = true,
                    label = { Text("Formato") }, modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { Icon(painterResource(android.R.drawable.arrow_down_float), null, Modifier.clickable { formatoExpanded = true }) }
                )
                DropdownMenu(expanded = formatoExpanded, onDismissRequest = { formatoExpanded = false }) {
                    formatos.forEach { f ->
                        DropdownMenuItem(text = { Text(f) }, onClick = { formatoSeleccionado = f; formatoExpanded = false })
                    }
                }
            }

            OutlinedTextField(value = imdb, onValueChange = { imdb = it }, label = { Text("IMDB URL") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = notas, onValueChange = { notas = it }, label = { Text("Comentarios") }, modifier = Modifier.fillMaxWidth().height(120.dp))

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        saveFilmData(titulo, director, anyo, generos.indexOf(generoSeleccionado), formatos.indexOf(formatoSeleccionado), imdb, notas)
                        onGuardar()
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) { Text("Guardar") }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = onCancelar,
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                ) { Text("Cancelar") }
            }
        }
    }
}