package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import android.database.sqlite.*
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.widget.TableLayout
import android.widget.TableRow
import android.location.LocationManager
import android.view.Gravity
import android.widget.TextView
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.security.Provider
import java.util.Locale
import kotlin.properties.Delegates


class Mappa: AppCompatActivity(), OnMapReadyCallback, LocationListener {


    private val dbHelper = MyDbHelper(this)
    private lateinit var myMap: GoogleMap
    private val locationPermissionCode = 123
    private var permessi: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mappa)
        var mapFragment: SupportMapFragment? =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        this.gestioneEstrazioneDati()

        var statoPermission = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if(statoPermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),locationPermissionCode)

        }
        else{
            Toast.makeText(this,"Hai già i permessi",Toast.LENGTH_SHORT).show()
        }

        statoPermission = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
        if(statoPermission == PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,500f,this)
            permessi = true
        }


    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == locationPermissionCode){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Accesso Autorizzato",Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this,"Accesso Negato",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onProviderEnabled(provider: String) {
        super.onProviderEnabled(provider)

    }

    override fun onLocationChanged(p0: Location) {

        val currentLatLng = LatLng(p0.latitude, p0.longitude)
        myMap.addMarker(MarkerOptions().position(currentLatLng).title("La mia posizione"))
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
        Toast.makeText(this,"Aggiornamento ${p0.longitude},${p0.latitude}",Toast.LENGTH_SHORT).show()
        val cityName = getCityName(p0)
        val message = getString(R.string.seiA, cityName)
        val cityTextView: TextView = findViewById(R.id.textbox1)
        cityTextView.text = message
    }


    private fun getCityName(location: Location): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses: MutableList<Address>? = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val city = addresses?.get(0)?.locality
                    if (city != null && city.isNotEmpty()) {
                        return city
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return "Città sconosciuta"
    }
    fun gestioneEstrazioneDati(){
        var db: SQLiteDatabase = dbHelper.writableDatabase // apro il db
        val datoRicevuto = intent.getStringExtra("Chiave")

        val selection: String?
        val selectionArgs: Array<String?>

        if (datoRicevuto == "*") {
            // Query senza'tipo'
            selection = null
            selectionArgs = emptyArray()
        } else {
            selection = "tipo = ?"
            selectionArgs = arrayOf(datoRicevuto)
        }


        val queryBuilder = StringBuilder()
        queryBuilder.append("SELECT DISTINCT r.nome, m.tipo, r._id FROM Ristorante r, Menu m WHERE r._id = m.ristorante ")
        if (selection != null) {
            queryBuilder.append("AND $selection")
        }

        val cursor = db.rawQuery(queryBuilder.toString(), selectionArgs)


        val tableLayout = findViewById<TableLayout>(R.id.tables)

        while (cursor.moveToNext()) {

            // Retrieve data from the query
            val indexNome = cursor.getColumnIndex("nome")
            val nome = cursor.getString(indexNome)

            // Toast.makeText(this,nome,Toast.LENGTH_SHORT).show()
            val indexId = cursor.getColumnIndex("_id")
            val id = cursor.getString(indexId)

            val indexTipo = cursor.getColumnIndex("tipo")
            val tipo = cursor.getString(indexTipo)


            /* Set row width to match_parent
            row.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )
            row.setBackgroundColor(resources.getColor(android.R.color.darker_gray))

            */
            // Create cell TextViews and add them to the row
            val button = Button(this)


            button.text = "$nome , $tipo"
            val b = 200 // Dimensione del pulsante in dp
            val scale = resources.displayMetrics.density
            val buttonSize = (b*scale).toInt()
            val params = TableRow.LayoutParams(buttonSize, TableRow.LayoutParams.WRAP_CONTENT)
            button.layoutParams = params
            // Set button width to match_parent

            button.setOnClickListener {
                val intent = Intent(this, Ristorante::class.java)
                intent.putExtra("ChiaveId", id)
                intent.putExtra("ChiaveTipo", tipo)
                startActivity(intent)
            }
            val row = TableRow(this)
            row.addView(button)
            tableLayout.addView(row)
            // Add the row to the TableLayout

        }
        cursor.close()  // Close the Cursor after extracting the data
    }
    override fun onMapReady(p0: GoogleMap) {
        myMap = p0
        var db: SQLiteDatabase = dbHelper.writableDatabase // apro il db

        val datoRicevuto = intent.getStringExtra("Chiave")  // qui dentro ho pizza, panino, sushi o tutto*

        val selection: String?
        val selectionArgs: Array<String?>

        if (datoRicevuto == "*") {
            // Query senza'tipo'
            selection = null
            selectionArgs = emptyArray()
        } else {
            selection = "tipo = ?"
            selectionArgs = arrayOf(datoRicevuto)
        }


        val queryBuilder = StringBuilder()
        queryBuilder.append("SELECT DISTINCT r.longitudine, r.latitudine, r.nome FROM Ristorante r, Menu m WHERE r._id = m.ristorante ")
        if (selection != null) {
            queryBuilder.append("AND $selection")
        }

        val cursor = db.rawQuery(queryBuilder.toString(), selectionArgs)

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




