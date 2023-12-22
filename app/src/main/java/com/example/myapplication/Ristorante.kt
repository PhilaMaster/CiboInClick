package com.example.myapplication

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Ristorante : AppCompatActivity() {
    //@SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //ricevo i valori dalle activity precedenti tramite intents
        val idRistorante = "1"//la ricevo da andrea
        val tipoRistorante = "panino"//ricevo da andrea
        var nomeRistorante: String? = null
        var telefonoRistorante: Int? = null

        //inizializzo database e prendo i valori del ristorante
        val dbHelper = MyDbHelper(this)
        val db: SQLiteDatabase = dbHelper.writableDatabase //apro il db
        var columns = arrayOf("nome","telefono")
        var where = "id = ?"
        var whereArgs = arrayOf(idRistorante)//deve prendere i valori da
        var cursor = db.query("Ristorante",columns,where,whereArgs,null,null,null)

        if(cursor.moveToFirst()){
            val indexNome = cursor.getColumnIndex("nome")
            nomeRistorante = cursor.getString(indexNome)

            val indexPrezzo = cursor.getColumnIndex("telefono")
            telefonoRistorante = cursor.getInt(indexPrezzo)
        }
        cursor.close()


        //inizializzo stringhe
        inizializzaTesto(nomeRistorante,telefonoRistorante)


        val tableLayout= findViewById<TableLayout>(R.id.tableLayout)

        //query per riempire il menù del ristorante
        columns = arrayOf("nome","prezzo")
        where = "tipo = ?"
        whereArgs = arrayOf("panino")//deve prendere i valori da
        cursor = db.query("Menu",columns,where,whereArgs,null,null,null)


        if(cursor.moveToFirst()){
            do{
                val indexNome = cursor.getColumnIndex("nome")
                val nome = cursor.getString(indexNome)

                val indexPrezzo = cursor.getColumnIndex("prezzo")
                val prezzo = cursor.getDouble(indexPrezzo)

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

                tableLayout.addView(tRow)
            } while (cursor.moveToNext())
            cursor.close()
        }


        //inizializzo bottoni e listener
        val buttonTelefono = findViewById<Button>(R.id.buttonTelefono)
        buttonTelefono.setOnClickListener{
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$telefonoRistorante"))
            startActivity(intent)
        }

        val buttonRecensioni = findViewById<Button>(R.id.buttonRecensioni)
        buttonRecensioni.setOnClickListener{
            val intent = Intent(this,Ristorante::class.java)
            intent.putExtra("idRistorante",idRistorante)
            intent.putExtra("tipoRistorante",tipoRistorante)
            startActivity(intent)
        }
    }



/*
PER INSERIRE IN RISTORANTE
INSERT INTO Ristorante (id,nome,telefono,longitudine,latitudine)
VALUES
(1,"Mammare",3495639913,39.34433996327592, 16.244342723497894)


 PER INSERIRE IN MENU'
 INSERT INTO Menu (ristorante,nome,tipo,prezzo)
VALUES
(2,"Mi sballo", "Panino",12.50)

 */


    private fun inizializzaTesto(nomeRistorante:String?,telefonoRistorante:Int?){
        val sourceString = "Benvenuto da <b> $nomeRistorante</b>,<br> cosa vuoi ordinare?"
        //mytextview.setText(Html.fromHtml(sourceString));
        val tvNomeRistorante = findViewById<TextView>(R.id.nomeRistorante)
        tvNomeRistorante.text = Html.fromHtml(sourceString,0)
        //tvNomeRistorante.text = "Benvenuto da $nomeRistorante, cosa vuoi ordinare?"
        val buttonTelefono = findViewById<Button>(R.id.buttonTelefono)
        buttonTelefono.text = getString(R.string.chiamaTel, telefonoRistorante)//"Chiama +39$telefonoRistorante"
    }
}