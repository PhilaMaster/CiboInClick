package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import android.database.sqlite.*
import android.widget.ArrayAdapter
import android.widget.ListView
import org.osmdroid.api.IMapController
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.location.*
import android.view.Gravity
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.MyDbHelper
import com.example.myapplication.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.osmdroid.config.Configuration
import org.w3c.dom.Text


class Mappa: AppCompatActivity(), OnMapReadyCallback{

    private val dbHelper = MyDbHelper(this)
    private lateinit var myMap: GoogleMap


    override fun onCreate(savedInstanceState: Bundle?)
        {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_mappa)
            var mapFragment: SupportMapFragment? = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            mapFragment?.getMapAsync(this)
            this.gestioneEstrazioneDati()

            }


    fun gestioneEstrazioneDati(){
        var db: SQLiteDatabase = dbHelper.writableDatabase //apro il db
        val datoRicevuto = intent.getStringExtra("Chiave")  //qui dentro ho pizza, panino, sushi o tutto

        val selectionArgs = arrayOf(datoRicevuto) //estraggo in base al tipo che mi arriva dal put extra
        val cursor = db.rawQuery("SELECT DISTINCT r.nome,m.tipo,r._id " +
                "                       FROM Ristorante r, Menu m " +
                "                       WHERE r._id=m.ristorante AND" +
                "                             tipo = ?",selectionArgs)

        val tableLayout = findViewById<TableLayout>(R.id.tables)

        while (cursor.moveToNext()) {
            // Crea una nuova riga
            val tableRow = TableRow(this)

            // Ottieni i dati dalla query
            val indexNome = cursor.getColumnIndex("nome")
            val nome = cursor.getString(indexNome)

            //  Toast.makeText(this,nome,Toast.LENGTH_SHORT).show()
            val indexId = cursor.getColumnIndex("_id")
            val id = cursor.getString(indexId)

            val indexTipo = cursor.getColumnIndex("tipo")
            val tipo = cursor.getString(indexTipo)

            Toast.makeText(this, "Nome: $nome, Tipo: $tipo", Toast.LENGTH_SHORT).show()

            // Crea celle TextView e aggiungile alla riga
            val button = Button(this)
            button.text = "$nome , $tipo"

            button.setOnClickListener {
                val intent = Intent(this, Ristorante::class.java)
                intent.putExtra("ChiaveId", id)
                intent.putExtra("ChiaveTipo", tipo)
                startActivity(intent)
            }

            tableRow.addView(button)



            // Aggiungi la riga al TableLayout
            tableLayout.addView(tableRow)
        }
        cursor.close()  // Chiudi il Cursor dopo aver estratto i dati
    }

    override fun onMapReady(p0: GoogleMap) {
        myMap = p0
        var db: SQLiteDatabase = dbHelper.writableDatabase // apro il db
        val datoRicevuto = intent.getStringExtra("Chiave")  // qui dentro ho pizza, panino, sushi o tutto*
        val selectionArgs = arrayOf(datoRicevuto) // estraggo in base al tipo che mi arriva dal put extra
        val cursor = db.rawQuery("SELECT DISTINCT r.longitudine,r.latitudine,r.nome " +
                "FROM Ristorante r, Menu m " +
                "WHERE r._id=m.ristorante AND" +
                " tipo = ?", selectionArgs)

        var firstLocation: LatLng? = null

        while (cursor.moveToNext()) {
            val indexNome = cursor.getColumnIndex("nome")
            val nome = cursor.getString(indexNome)

            val indexLat = cursor.getColumnIndex("latitudine")
            val latitudine = cursor.getDouble(indexLat)

            val indexLong = cursor.getColumnIndex("longitudine")
            val longitudine = cursor.getDouble(indexLong)

            val pos = LatLng(longitudine,latitudine)
            myMap.addMarker(MarkerOptions().position(pos).title(nome))

            if (firstLocation == null) {
                firstLocation = pos
            }
        }

        // Imposta la camera solo se ci sono risultati
        firstLocation?.let {
            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 10.0f))
        }

        cursor.close() // Chiudere il cursore quando non è più necessario
    }





}