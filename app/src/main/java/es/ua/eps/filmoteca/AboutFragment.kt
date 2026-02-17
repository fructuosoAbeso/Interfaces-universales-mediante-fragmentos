package es.ua.eps.filmoteca

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflamos el layout XML que ya tienes: activity_about.xml
        val view = inflater.inflate(R.layout.activity_about, container, false)

        // Configuración de botones (Lógica XML)
        view.findViewById<Button>(R.id.btnWeb).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.example.com"))
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.btnSoporte).setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:soporte@example.com")
            }
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.btnVolver).setOnClickListener {
            // En fragmentos usamos popBackStack para volver atrás
            parentFragmentManager.popBackStack()
        }

        return view
    }
}