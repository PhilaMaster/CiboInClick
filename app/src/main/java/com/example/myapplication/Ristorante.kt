package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
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
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ristorante)

        //ricevo i valori dalle activity precedenti tramite intents
        val idRistorante = intent.getStringExtra("ChiaveId")
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

        //inizializzo tableLayout: conterrà i vari prodotti del menù
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

                //immagine tipoProdotto
                val imageview = ImageView(this)
                var originalDrawable = resources.getDrawable(R.drawable.all_foods, null)
                when(tipoprodotto){
                    "panino" -> originalDrawable = resources.getDrawable(R.drawable.hamburger_top, null)
                    "pizza" -> originalDrawable = resources.getDrawable(R.drawable.pizza2, null)
                    "sushi" -> originalDrawable = resources.getDrawable(R.drawable.sushi, null)
                }

                val originalBitmap = (originalDrawable as BitmapDrawable).bitmap

                //ridimensiono l'immagine
                val newWidthInDp = 12
                val newHeightInDp = 12
                val density = resources.displayMetrics.density
                val newWidth = (newWidthInDp * density).toInt()
                val newHeight = (newHeightInDp * density).toInt()

                val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
                val resizedDrawable = BitmapDrawable(resources, resizedBitmap)
                imageview.setImageDrawable(resizedDrawable)


                tRow.gravity=1
                tRow.addView(imageview)

                tableLayout.addView(tRow)
            } while (cursor.moveToNext())
            cursor.close()
        }


        //inizializzo bottoni e listener
        val buttonTelefono = findViewById<Button>(R.id.buttonTelefono)
        buttonTelefono.setOnClickListener{
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$telefonoRistorante"))
            startActivity(intent)
            //ipotizziamo che la chiamata sia andata a buon fine e sia stata effettuata la prenotazione
            Toast.makeText(this,"Prenotazione effettuata con successo, aggiornamento database in corso...",Toast.LENGTH_LONG).show()
            aggiungiPrenotazione(idRistorante,getGiornoString(LocalDate.now().dayOfWeek),db)
        }

        val buttonRecensioni = findViewById<Button>(R.id.buttonRecensioni)
        buttonRecensioni.setOnClickListener{

            val intent = Intent(this,Recensione::class.java)
            intent.putExtra("idRistorante",idRistorante)
            startActivity(intent)
        }
    }

    private fun aggiungiPrenotazione(idRistorante: String?, giornoString: String, db:SQLiteDatabase) {
        numPrenotazioni++

        //rimuovo vecchio ultimoGiorno se esiste (mantengo solo l'ultimo ristorante da cui ho ordinato, per ogni giorno della settimana)
        val cursor = db.query("Ristorante", arrayOf("_id"),"ultimoGiorno = ?",
            arrayOf(giornoString),null,null,null)

        if(cursor.moveToFirst()){//se la query non è null (c'è un ristorante con giornoString)
            val indexId = cursor.getColumnIndex("_id")
            val idVecchio = cursor.getString(indexId)
            db.execSQL("UPDATE Ristorante SET ultimoGiorno = NULL WHERE _id=?", arrayOf(idVecchio))
        }
        cursor.close()
        //inserisco nuovo ultimoGiorno
        db.execSQL("UPDATE Ristorante SET numPrenotazioni=?, ultimoGiorno = ? WHERE _id=?", arrayOf(numPrenotazioni, giornoString, idRistorante))
        //aggiorno scritta di benvenuto con il nuovo contatore
        val tvNomeRistorante = findViewById<TextView>(R.id.nomeRistorante)
        tvNomeRistorante.text = getString(R.string.benvenuto,nomeRistorante,numPrenotazioni)
    }

    private fun inizializzaTesto(nomeRistorante:String?,telefonoRistorante:String?){
        if ( (nomeRistorante==null) || (telefonoRistorante==null)){//controllo sulle query
            Log.e("stringheNull","Errore stringhe null nomeRistorante o TelefonoRistorante. Query non funzionante")
            return
        }
        val tvNomeRistorante = findViewById<TextView>(R.id.nomeRistorante)
        tvNomeRistorante.text = getString(R.string.benvenuto,nomeRistorante,numPrenotazioni)
        val buttonTelefono = findViewById<Button>(R.id.buttonTelefono)
        buttonTelefono.text = getString(R.string.chiamaTel, telefonoRistorante)//"Chiama +39$telefonoRistorante"
    }

    //per avere il giorno della settimana attuale -> LocalDate.now().dayOfWeek
    private fun getGiornoString(giorno:DayOfWeek): String {
        return when(giorno){
            DayOfWeek.MONDAY -> getString(R.string.lunedì)
            DayOfWeek.TUESDAY -> getString(R.string.martedì)
            DayOfWeek.WEDNESDAY -> getString(R.string.mercoledì)
            DayOfWeek.THURSDAY -> getString(R.string.giovedì)
            DayOfWeek.FRIDAY -> getString(R.string.venerdì)
            DayOfWeek.SATURDAY -> getString(R.string.sabato)
            DayOfWeek.SUNDAY -> getString(R.string.domenica)
        }
    }
}