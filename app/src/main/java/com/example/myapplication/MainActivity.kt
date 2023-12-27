package com.example.myapplication
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val mioDb = MyDbHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val mappa = findViewById<ImageButton>(R.id.imageButton)
        mappa.setOnClickListener {
            val intent = Intent(this, Mappa::class.java)
            intent.putExtra("Chiave", "pizza")
            startActivity(intent)
        }
        val mappa2 = findViewById<ImageButton>(R.id.imageButton2)
        mappa2.setOnClickListener {
            val intent = Intent(this, Mappa::class.java)
            intent.putExtra("Chiave", "panino")
            startActivity(intent)
        }
        val mappa3 = findViewById<ImageButton>(R.id.imageButton3)
        mappa3.setOnClickListener {
            val intent = Intent(this, Mappa::class.java)
            intent.putExtra("Chiave", "sushi")
            startActivity(intent)
        }

        this.gestioneOrario()
        this.gestioneVisibilitàBottoneUlt()
        this.gestionePrenotazioneStessoGiorno() //Devo mandare i put extra a Pasquale

    }

    fun gestioneOrario() {
        val textView2: TextView = findViewById(R.id.textView2)

        val currentTime = Calendar.getInstance().time
        val hourFormat = SimpleDateFormat("HH", Locale.getDefault())
        val currentHour = hourFormat.format(currentTime).toInt()

        // Aggiorno il text view in base all'orario
        when {
            currentHour in 0..13 -> {
                textView2.text = "Buongiorno, si avvicina l'ora di pranzo. Cosa vuoi mangiare?"
            }

            currentHour in 14..17 -> {
                textView2.text = "Buon pomeriggio, cosa vuoi mangiare?"
            }

            else -> {
                textView2.text = "Buonasera, si avvicina l'ora di cena. Cosa vuoi mangiare?"
            }
        }
    }

    fun gestioneVisibilitàBottoneUlt() {
        //In questo metodo gestisco la comparsa/scomparsa del bottone e della text view
        //riguardanti il locale prenotato più spesso
        var db: SQLiteDatabase = mioDb.writableDatabase
        val button1: Button = findViewById(R.id.bottoneUltimaPren)
        val text1: TextView = findViewById(R.id.textUltimaPrent)

        button1.visibility = View.GONE
        text1.visibility = View.GONE


        val cursor = db.rawQuery(
            "SELECT r.nome " +
                    "FROM Ristorante r " +
                    "ORDER BY r.numPrenotazioni DESC;", null
        )

        if (cursor.moveToNext()) {
            val IndexRist = cursor.getColumnIndex("nome")
            val nome = cursor.getString(IndexRist)

            button1.visibility = View.VISIBLE
            text1.visibility = View.VISIBLE
            button1.text = "Prenota $nome"
            text1.text = "L'ultima volta hai prenotato da $nome"
        }
    }

       private fun giornoCorrente(): String {
            val calendar = Calendar.getInstance()
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

            return when (dayOfWeek) {
                Calendar.SUNDAY -> "domenica"
                Calendar.MONDAY -> "lunedì"
                Calendar.TUESDAY -> "martedì"
                Calendar.WEDNESDAY -> "mercoledì"
                Calendar.THURSDAY -> "giovedì"
                Calendar.FRIDAY -> "venerdì"
                Calendar.SATURDAY -> "sabato"
                else -> ""
            }
        }

        fun gestionePrenotazioneStessoGiorno(){
            var db: SQLiteDatabase = mioDb.writableDatabase
            val button2: Button = findViewById(R.id.bottoneUltimoGiorno)
            val text2: TextView = findViewById(R.id.textUltimoGiorno)
            button2.visibility = View.GONE
            text2.visibility = View.GONE

            val giornoAttuale = giornoCorrente()
            val selectionArgs = arrayOf(giornoAttuale)
               val cursor = db.rawQuery(
                "SELECT r.nome " +
                        "FROM Ristorante r " +
                        "WHERE r.ultimoGiorno = ?;", selectionArgs)

                if (cursor.moveToNext()) {
                    val IndexRist = cursor.getColumnIndex("nome")
                    val nome = cursor.getString(IndexRist)

                    button2.visibility = View.VISIBLE
                    text2.visibility = View.VISIBLE
                    button2.text = "Prenota $nome"
                    text2.text = "$giornoAttuale scorso sei stato da $nome. Vuoi riandarci?"

                }

        }



    }



