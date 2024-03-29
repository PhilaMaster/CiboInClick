package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import android.database.sqlite.*
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.widget.TableLayout
import android.widget.TableRow
import android.location.LocationManager
import android.widget.TextView
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.os.Build
import android.util.Log
import android.view.WindowManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.util.Locale


@Suppress("DEPRECATION")
class Mappa: AppCompatActivity(), OnMapReadyCallback, LocationListener {


    private val dbHelper = MyDbHelper(this)
    private lateinit var myMap: GoogleMap
    private val locationPermissionCode = 123
    private var permessi: Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mappa)
        val mapFragment: SupportMapFragment? =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        this.gestioneEstrazioneDati()
    }

    override fun onResume() {
        super.onResume()
        var statoPermission = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if(statoPermission != PackageManager.PERMISSION_GRANTED && permessi){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),locationPermissionCode)

        }
        else {
            //Toast.makeText(this,"Hai già i permessi o hai rifiutato",Toast.LENGTH_SHORT).show()
            Log.d("gps permessi","Hai già i permessi o hai rifiutato")
        }

        statoPermission = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
        if(statoPermission == PackageManager.PERMISSION_GRANTED && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000,5000f,this)

            Log.d("gps permessi","permessi ok, calcolo posizione")
        }
        else {
            permessi = false
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

    override fun onLocationChanged(p0: Location) {

        val currentLatLng = LatLng(p0.latitude, p0.longitude)
        myMap.addMarker(MarkerOptions().position(currentLatLng).title("La mia posizione"))
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
        Toast.makeText(this,"Aggiornamento posizione:${p0.longitude},${p0.latitude}",Toast.LENGTH_SHORT).show()
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
                    val city = addresses.get(0).locality
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
    @SuppressLint("SetTextI18n")
    private fun gestioneEstrazioneDati(){
        val db: SQLiteDatabase = dbHelper.writableDatabase // apro il db
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

            val button = Button(this)
            val tipoT= when(tipo){
                "panino" -> getString(R.string.tipoTPanino)
                "sushi" -> getString(R.string.tipoTSushi)
                "pizza" -> getString(R.string.tipoTPizza)
                else -> getString(R.string.tipoTTutto)
            }
            button.text = "$nome , $tipoT"


            val screenSize = ScreenUtils.getScreenSize(this)
            val screenWidth = screenSize.x
            val params = TableRow.LayoutParams(screenWidth, TableRow.LayoutParams.WRAP_CONTENT)
            button.layoutParams = params
            button.backgroundTintList = getColorStateList(R.color.coloreVioletto)
            button.setTextColor(Color.BLACK)


            button.setOnClickListener {
                val intent = Intent(this, Ristorante::class.java)
                intent.putExtra("ChiaveId", id)
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
        val db: SQLiteDatabase = dbHelper.writableDatabase // apro il db

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


@Suppress("DEPRECATION")
class ScreenUtils {
    companion object {
        fun getScreenSize(context: Context): Point {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                context.display
            } else {
                windowManager.defaultDisplay
            }

            val size = Point()
            display?.getSize(size)
            return size
        }
    }
}


