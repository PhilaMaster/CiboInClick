package com.example.myapplication

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.time.DayOfWeek
import java.time.LocalDate

class Ristorante : AppCompatActivity() {
    private var numPrenotazioni = 0
    private var nomeRistorante:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ristorante)

        //ricevo i valori dalle activity precedenti tramite intents
        val idRistorante = "1"//la ricevo da andrea
        val tipoRistorante = "panino"//ricevo da andrea
        var telefonoRistorante: String? = null

        //inizializzo database e prendo i valori del ristorante
        val dbHelper = MyDbHelper(this)
        val db: SQLiteDatabase = dbHelper.writableDatabase //apro il db
        var columns = arrayOf("nome","telefono","numPrenotazioni")
        var where = "_id = ?"
        var whereArgs = arrayOf(idRistorante)
        var cursor = db.query("Ristorante",columns,where,whereArgs,null,null,null)

        if(cursor.moveToFirst()){
            val indexNome = cursor.getColumnIndex("nome")
            nomeRistorante = cursor.getString(indexNome)

            val indexPrezzo = cursor.getColumnIndex("telefono")
            telefonoRistorante = cursor.getString(indexPrezzo)

            val indexPrenotazioni = cursor.getColumnIndex("numPrenotazioni")
            numPrenotazioni = cursor.getInt(indexPrenotazioni)
        }
        cursor.close()


        //inizializzo stringhe
        inizializzaTesto(nomeRistorante,telefonoRistorante)


        val tableLayout= findViewById<TableLayout>(R.id.tableLayout)

        //query per riempire il menù del ristorante
        columns = arrayOf("nome","prezzo","tipo")
        where = "ristorante = ?"
        whereArgs = arrayOf(idRistorante)

        cursor = db.query("Menu",columns,where,whereArgs,null,null,null)


        if(cursor.moveToFirst()){
            do{
                val indexNome = cursor.getColumnIndex("nome")
                val nome = cursor.getString(indexNome)

                val indexPrezzo = cursor.getColumnIndex("prezzo")
                val prezzo = cursor.getDouble(indexPrezzo)

                val indexTipoprodotto = cursor.getColumnIndex("tipo")
                val tipoprodotto = cursor.getString(indexTipoprodotto)

                val tRow = TableRow(this)
                var tView = TextView(this)

                //testo1: prodotto
                tView.text = nome
                tView.textSize = 20f
                tView.textAlignment = View.TEXT_ALIGNMENT_CENTER
                tRow.addView(tView)

                //testo2: prezzo
                tView = TextView(this)

                tView.text = getString(R.string.prezzo, prezzo)
                tView.textSize = 20f
                tView.textAlignment = View.TEXT_ALIGNMENT_CENTER
                tRow.addView(tView)

                //testo3: tipoProdotto
                tView = TextView(this)

                var tipo:String = "N"
                when(tipoprodotto){
                    "panino" -> tipo = "PA"
                    "pizza" -> tipo = "PI"
                    "sushi" -> tipo = "SU"
                }
                tView.text = tipo
                tView.textSize = 20f
                tView.textAlignment = View.TEXT_ALIGNMENT_CENTER
                tRow.addView(tView)

                tableLayout.addView(tRow)
            } while (cursor.moveToNext())
            cursor.close()
        }


        //inizializzo bottoni e listener
        val buttonTelefono = findViewById<Button>(R.id.buttonTelefono)
        buttonTelefono.setOnClickListener{
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$telefonoRistorante"))
            startActivity(intent)
            Toast.makeText(this,"Prenotazione effettuata con successo, aggiornamento database in corso...",Toast.LENGTH_LONG).show()
            aggiungiPrenotazione(idRistorante,getGiornoString(LocalDate.now().dayOfWeek),db)
        }

        val buttonRecensioni = findViewById<Button>(R.id.buttonRecensioni)
        buttonRecensioni.setOnClickListener{

            val intent = Intent(this,Recensione::class.java)
            intent.putExtra("idRistorante",idRistorante)
            startActivity(intent)

            //codice di debug per resettare il database
            //dbHelper.deleteDatabase(this)
            //Toast.makeText(this,"Database eliminato",Toast.LENGTH_SHORT).show()
        }
    }

    internal fun aggiungiPrenotazione(idRistorante: String, giornoString: String, db:SQLiteDatabase) {
        numPrenotazioni++
        val stringhe = arrayOf(numPrenotazioni, giornoString, idRistorante)
        db.execSQL("UPDATE Ristorante SET numPrenotazioni=?, ultimoGiorno = ? WHERE _id=?", stringhe)
        //aggiorno scritta di benvenuto con il nuovo contatore
        val tvNomeRistorante = findViewById<TextView>(R.id.nomeRistorante)
        tvNomeRistorante.text = getString(R.string.benvenuto,nomeRistorante,numPrenotazioni)
    }

    private fun inizializzaTesto(nomeRistorante:String?,telefonoRistorante:String?){
        if ( (nomeRistorante==null) || (telefonoRistorante==null)){//controllo sulle query
            Log.e("stringheNull","Errore stringhe null nomeRistorante o TelefonoRistorante. Query non funzionante")
        }



        //mytextview.setText(Html.fromHtml(sourceString));
        val tvNomeRistorante = findViewById<TextView>(R.id.nomeRistorante)
        tvNomeRistorante.text = getString(R.string.benvenuto,nomeRistorante,numPrenotazioni)
        val buttonTelefono = findViewById<Button>(R.id.buttonTelefono)
        buttonTelefono.text = getString(R.string.chiamaTel, telefonoRistorante)//"Chiama +39$telefonoRistorante"
    }

    //per avere il giorno della settimana attuale -> LocalDate.now().dayOfWeek
    private fun getGiornoString(giorno:DayOfWeek): String {
        when(giorno){
            DayOfWeek.MONDAY -> return getString(R.string.lunedì)
            DayOfWeek.TUESDAY -> return getString(R.string.martedì)
            DayOfWeek.WEDNESDAY -> return getString(R.string.mercoledì)
            DayOfWeek.THURSDAY -> return getString(R.string.giovedì)
            DayOfWeek.FRIDAY -> return getString(R.string.venerdì)
            DayOfWeek.SATURDAY -> return getString(R.string.sabato)
            DayOfWeek.SUNDAY -> return getString(R.string.domenica)
        }
    }
}