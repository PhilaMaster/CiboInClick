package com.example.myapplication

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.Cursor
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class Recensione : AppCompatActivity() {
    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recensione)


        val dbHelper = MyDbHelper(this)
        val db = dbHelper.writableDatabase


        //  Inserisco nell'etichetta del nome ristorante
        val idRistorante = intent.getStringExtra("idRistorante")
        //val idRistorante = "1" verifica prima di unire i progetti con un id specifico
        val array = arrayOf(idRistorante)
        val queryNome = "SELECT nome FROM Ristorante WHERE _id = ?"
        val qualeRistorante = db.rawQuery(queryNome, array)
        // Controlla se il cursore ha valori
        if (qualeRistorante.moveToFirst()) {
            // Prendi il valore dal cursore
            val nomeRistorante = qualeRistorante.getString(qualeRistorante.getColumnIndex("nome"))

            // Inserisci i valori in TextView
            val etichettaRistorante = findViewById<TextView>(R.id.prezzo)
            etichettaRistorante.text = nomeRistorante
        } else {
            // Gestisco il caso in cui non trovo ristoranti
            val etichettaRistorante = findViewById<TextView>(R.id.prezzo)
            etichettaRistorante.text = "Ristorante non trovato :("
        }


        qualeRistorante.close()


        //query per le stelle
        val cursor = db.rawQuery("SELECT votoPrezzo, votoCibo FROM Recensione WHERE ristorante=?", array)
        var totaleStelleCibo = 0.0
        var totaleStellePrezzo = 0.0
        var conteggio = 0

        if (cursor.moveToFirst()) {
            do {
                val indexSCibo = cursor.getColumnIndex("votoPrezzo")
                val stelleCibo = cursor.getDouble(indexSCibo)
                val indexSPrezzo = cursor.getColumnIndex("votoCibo")
                val stellePrezzo = cursor.getDouble(indexSPrezzo)

                totaleStelleCibo += stelleCibo
                totaleStellePrezzo += stellePrezzo
                conteggio++
            } while (cursor.moveToNext())
        }

        // Calcola le medie
        val mediaStelleCibo = if (conteggio > 0) totaleStelleCibo / conteggio else 0.0
        val mediaStellePrezzo = if (conteggio > 0) totaleStellePrezzo / conteggio else 0.0

        // Setto i valori nelle stelle
        val stelleCiboIndicatore = findViewById<RatingBar>(R.id.ratingBar3)
        stelleCiboIndicatore.rating = mediaStelleCibo.toFloat()
        val stellePrezzoIndicatore = findViewById<RatingBar>(R.id.ratingBar4)
        stellePrezzoIndicatore.rating = mediaStellePrezzo.toFloat()



        // Metodo di utilità per creare TextView per le etichette
        fun createLabelTextView(text: String): TextView {
            val textView = TextView(this)
            textView.text = text
            textView.setTextColor(resources.getColor(android.R.color.white))
            textView.setPadding(8, 8, 8, 8)
            return textView
        }

        // Metodo di utilità per creare TextView per i dati
        fun createDataTextView(text: String): TextView {
            val textView = TextView(this)
            textView.text = text
            textView.setTextColor(resources.getColor(android.R.color.black))
            textView.setPadding(8, 8, 8, 8)
            return textView
        }


        // Popola dinamicamente la ScrollView con le recensioni
        fun mostraRecensioni(cursor: Cursor) {
            val tableLayout = findViewById<TableLayout>(R.id.tableLayout)

            // Rimuovi tutte le righe precedenti per evitare duplicati
            tableLayout.removeAllViews()

            // Creazione della riga delle etichette
            val labelRow = TableRow(this)
            labelRow.setBackgroundColor(resources.getColor(android.R.color.holo_purple))
            labelRow.addView(createLabelTextView(getString(R.string.nomeR)))
            labelRow.addView(createLabelTextView(getString(R.string.ciboR)))
            labelRow.addView(createLabelTextView(getString(R.string.prezzoR)))
            tableLayout.addView(labelRow)

            // Variabile per alternare i colori
            var isWhiteBackground = false

            // Itera sul cursore e popola la tabella con le recensioni
            if (cursor.moveToFirst()) {
                do {
                    val nomeRecensore = cursor.getString(cursor.getColumnIndex("nomeRecensore"))
                    val votoPrezzo = cursor.getDouble(cursor.getColumnIndex("votoPrezzo"))
                    val votoCibo = cursor.getDouble(cursor.getColumnIndex("votoCibo"))

                    // Creazione di una nuova riga
                    val row = TableRow(this)
                    val layoutParams = TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT
                    )
                    row.layoutParams = layoutParams

                    // Alternanza del colore di sfondo
                    if (isWhiteBackground) {
                        row.setBackgroundColor(resources.getColor(android.R.color.white))
                    } else {
                        row.setBackgroundColor(resources.getColor(android.R.color.darker_gray))
                    }

                    // Aggiunta di TextView alla riga
                    row.addView(createDataTextView(nomeRecensore))
                    row.addView(createDataTextView(votoCibo.toString()))
                    row.addView(createDataTextView(votoPrezzo.toString()))

                    // Aggiungi la riga al TableLayout
                    tableLayout.addView(row)

                    // Inverti lo stato del colore di sfondo
                    isWhiteBackground = !isWhiteBackground
                } while (cursor.moveToNext())
            }

            // Chiudi il cursore
            cursor.close()
        }



        // Chiamato per inizializzare e aggiornare la visualizzazione delle recensioni
        fun aggiornaRecensioni() {
            val recensioniCursor = dbHelper.getRecensioniByRistoranteId(idRistorante.toString()) // ID del ristorante corrente
            mostraRecensioni(recensioniCursor)
        }

        aggiornaRecensioni()


        //  Funzione quando clicco il bottone conferma: invia la rercensione nel database
        val barraInserisciVotoCibo = findViewById<RatingBar>(R.id.ratingBar8)
        val barraInserisciVotoPrezzo = findViewById<RatingBar>(R.id.ratingBar9)
        val inserisciNome = findViewById<EditText>(R.id.editText2)

        val bottoneDiConferma = findViewById<Button>(R.id.button)
        bottoneDiConferma.setOnClickListener {
            // Recupera i valori dai widget
            val numStelleCibo = barraInserisciVotoCibo.rating
            val numStellePrezzo = barraInserisciVotoPrezzo.rating
            val nomeDaInserire = inserisciNome.text.toString()

            // Controlla se sono stati inseriti tutti i valori necessari
            if (nomeDaInserire.isNotEmpty() && numStelleCibo > 0 && numStellePrezzo > 0) {
                // Crea un oggetto ContentValues per inserire i dati nel database
                val values = ContentValues()
                values.put("ristorante", idRistorante) //id ristorante corrente
                values.put("nomeRecensore", nomeDaInserire)
                values.put("votoPrezzo", numStellePrezzo)
                values.put("votoCibo", numStelleCibo)

                // Inserisci i dati nella tabella "Recensione"
                val result = db.insert("Recensione", null, values)
                Toast.makeText(
                    this@Recensione,
                    "Recensione inviata con successo!",
                    Toast.LENGTH_SHORT
                ).show()
                // Controlla se l'inserimento è avvenuto con successo

            } else {
                // Mostra un messaggio di errore se non tutti i campi sono stati compilati
                Toast.makeText(
                    this@Recensione,
                    "Inserisci il tuo nome e valuta entrambe le stelle",
                    Toast.LENGTH_SHORT
                ).show()
            }
            //aggiorna le recensioni da mostrare nella tabella
            aggiornaRecensioni()
        }

    }

}
