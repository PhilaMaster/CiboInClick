package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import java.text.SimpleDateFormat
import java.util.*


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private val mioDb = MyDbHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val conLay3 = findViewById<ConstraintLayout>(R.id.constraintLayout3)
        val screenSize = ScreenUtils.getScreenSize(this)
        val screenWidth = screenSize.x
        conLay3.layoutParams.width = screenWidth

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
        val mappa4 = findViewById<ImageButton>(R.id.imageButton4)
        mappa4.setOnClickListener {
            val intent = Intent(this, Mappa::class.java)
            intent.putExtra("Chiave", "*")
            startActivity(intent)
        }

        val cambiaLingua = findViewById<Button>(R.id.cambiaLingua)
        cambiaLingua.setOnClickListener{
            setLocale(this,"en")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        this.gestioneOrario()
        this.gestioneVisibilitaBottoneUlt()
        this.gestionePrenotazioneStessoGiorno()

    }

    private fun gestioneOrario() {
        val textView2: TextView = findViewById(R.id.prezzo)
        val screenSize = ScreenUtils.getScreenSize(this)
        val screenWidth = screenSize.x
        textView2.layoutParams.width = screenWidth

        val currentTime = Calendar.getInstance().time
        val hourFormat = SimpleDateFormat("HH", Locale.getDefault())

        // Aggiorno il text view in base all'orario
        when (hourFormat.format(currentTime).toInt()) {
            in 5..13 -> {
                textView2.text = getString(R.string.mangiareGiorno)
            }
            in 14..17 -> {
                textView2.text = getString(R.string.mangiarePomeriggio)
            }
            in 18..22 -> {
                textView2.text = getString(R.string.mangiareSera)
            }
            else -> textView2.text = getString(R.string.mangiareNotte)
        }
    }

    private fun gestioneVisibilitaBottoneUlt() {
        //In questo metodo gestisco la comparsa/scomparsa del bottone e della text view
        //riguardanti il locale prenotato più spesso
        val db: SQLiteDatabase = mioDb.writableDatabase
        val button1: Button = findViewById(R.id.bottoneUltimaPren)
        val text1: TextView = findViewById(R.id.textUltimaPrent)
        val screenSize = ScreenUtils.getScreenSize(this)
        val screenWidth = screenSize.x
        text1.layoutParams.width = screenWidth

        button1.visibility = View.GONE
        text1.visibility = View.GONE


        val cursor = db.rawQuery(
            "SELECT r.nome, r._id,m.tipo " +
                    "FROM Ristorante r ,Menu m " +
                    "WHERE r._id=m.ristorante " +
                    "ORDER BY r.numPrenotazioni DESC;", null
        )

        if (cursor.moveToNext()) {
            val IndexRist = cursor.getColumnIndex("nome")
            val nome = cursor.getString(IndexRist)

            val IndexId = cursor.getColumnIndex("_id")
            val id = cursor.getString(IndexId)

            val IndexTipo = cursor.getColumnIndex("tipo")
            val tipo = cursor.getString(IndexTipo)

            button1.visibility = View.VISIBLE
            text1.visibility = View.VISIBLE
            button1.text = getString(R.string.prenota, nome)
            text1.text = getString(R.string.ultimaPText,nome)
            button1.setOnClickListener {
                val intent = Intent(this, Ristorante::class.java)
                intent.putExtra("ChiaveId", id)
                intent.putExtra("ChiaveTipo", tipo)
                startActivity(intent)
            }
        }
        cursor.close()
    }

       private fun giornoCorrente(lingua:Boolean): String {
           //false -> ricevo nome settimana in italiano, per fare la query
           //true  -> ricevo nome settimana nella lingua dell'app, per le textView
            val calendar = Calendar.getInstance()
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

           if (!lingua){
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
           return when (dayOfWeek) {
               Calendar.SUNDAY -> getString(R.string.domenica)
               Calendar.MONDAY -> getString(R.string.lunedì)
               Calendar.TUESDAY -> getString(R.string.martedì)
               Calendar.WEDNESDAY -> getString(R.string.mercoledì)
               Calendar.THURSDAY -> getString(R.string.giovedì)
               Calendar.FRIDAY -> getString(R.string.venerdì)
               Calendar.SATURDAY -> getString(R.string.sabato)
               else -> ""
           }
        }

        private fun gestionePrenotazioneStessoGiorno(){
            val db: SQLiteDatabase = mioDb.writableDatabase
            val button2: Button = findViewById(R.id.bottoneUltimoGiorno)
            val text2: TextView = findViewById(R.id.textUltimoGiorno)
            button2.visibility = View.GONE
            text2.visibility = View.GONE
            val screenSize = ScreenUtils.getScreenSize(this)
            val screenWidth = screenSize.x
            text2.layoutParams.width = screenWidth



            val giornoAttuale = giornoCorrente(false)
            val giornoAttualeLang = giornoCorrente(true)
            val selectionArgs = arrayOf(giornoAttuale)
               val cursor = db.rawQuery(
                "SELECT r.nome, r._id, m.tipo " +
                        "FROM Ristorante r, Menu m " +
                        "WHERE r._id = m.ristorante AND r.ultimoGiorno = ?;", selectionArgs)

                if (cursor.moveToNext()) {
                    val IndexRist = cursor.getColumnIndex("nome")
                    val nome = cursor.getString(IndexRist)

                    val IndexId = cursor.getColumnIndex("_id")
                    val id = cursor.getString(IndexId)
                    val IndexTipo = cursor.getColumnIndex("tipo")
                    val tipo = cursor.getString(IndexTipo)

                    button2.visibility = View.VISIBLE
                    text2.visibility = View.VISIBLE
                    button2.text = getString(R.string.prenota,nome)
                    text2.text = getString(R.string.ultimoGText,giornoAttualeLang,nome)

                    button2.setOnClickListener {
                        val intent = Intent(this, Ristorante::class.java)
                        intent.putExtra("ChiaveId", id)
                        intent.putExtra("ChiaveTipo", tipo)
                        startActivity(intent)
                    }
                }
            cursor.close()

        }

    private fun setLocale(activity: Activity, @Suppress("SameParameterValue") languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources = activity.resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    }



