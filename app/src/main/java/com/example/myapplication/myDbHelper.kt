package com.example.myapplication

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class MyDbHelper(context: Context): SQLiteOpenHelper(context, "mydatabase.db", null, 1) {
    override fun onCreate(p0: SQLiteDatabase) {
        p0.execSQL("CREATE TABLE Ristorante (_id INTEGER PRIMARY KEY NOT NULL, nome TEXT NOT NULL, telefono TEXT NOT NULL, numPrenotazioni INTEGER NOT NULL, ultimoGiorno TEXT, longitudine REAL NOT NULL, latitudine REAL NOT NULL);")
        p0.execSQL("CREATE TABLE Menu (ristorante INTEGER NOT NULL,nome TEXT NOT NULL, tipo TEXT NOT NULL, prezzo REAL NOT NULL, PRIMARY KEY (ristorante,nome), FOREIGN KEY (ristorante) REFERENCES Ristorante(id));")
        p0.execSQL("CREATE TABLE Recensione (ristorante INTEGER NOT NULL,nomeRecensore TEXT, votoPrezzo REAL NOT NULL, votoCibo REAL NOT NULL, PRIMARY KEY (ristorante,nomeRecensore), FOREIGN KEY (ristorante) REFERENCES Ristorante(id));")

        p0.execSQL("INSERT INTO Menu (ristorante,nome,tipo,prezzo) VALUES" +
                "(1,\"Mi'Ndujo\",\"panino\",9.99)," +
                "(1,\"Acri'\",\"panino\",7.99)," +
                "(1,\"Bisignano\",\"panino\",9.99)," +
                "(1,\"Luzzi'\",\"panino\",6.99)," +
                "(1,\"Mi sballo\",\"panino\",11.99)," +
                "(1,\"Bisignano top\",\"panino\",12.99)," +
                "(1,\"Mi sbunnu\",\"panino\",13.99)," +
                "(1,\"Conzativicci\",\"panino\",9.99)," +
                "(1,\"Ton Pippo\",\"panino\",10.99)," +
                "(1,\"Ghiotto\",\"panino\",6.99)," +
                "(1,\"Vegetariano\",\"panino\",6.99);")
                //"(3,\"Caruso\",\"pizza\",12.50);")

        p0.execSQL("INSERT INTO Menu (ristorante,nome,tipo,prezzo) VALUES" +
                "(2,\"Doppio Hamburger\",\"panino\",9.99)," +
                "(2,\"Sussone'\",\"panino\",7.99);")

        p0.execSQL("INSERT INTO Ristorante (_id,nome,telefono,numPrenotazioni, ultimoGiorno, longitudine,latitudine) VALUES " +
                "(1,\"Mi 'ndujo\",\"3517591007\",5,\"martedì\",39.35363861160489, 16.23595023170938);")
        p0.execSQL("INSERT INTO Ristorante (_id,nome,telefono,numPrenotazioni, ultimoGiorno, longitudine,latitudine) VALUES " +
                "(2,\"Burgeria\",\"3517168626\",2,\"venerdì\",39.353526195099874, 16.2367441926086);")

        p0.execSQL("INSERT INTO Recensione (ristorante,nomeRecensore,votoPrezzo,votoCibo) VALUES" +
                "(1,\"Domenico\",3.0,4.5)," +
                "(1,\"Pasquale\",3.5,4.0)," +
                "(1,\"Andrea\",4.0,3.5)," +
                "(1,\"Giuseppe\",4.5,4.5)," +
                "(1,\"Il QG\",2.5,3.0);")

    }
    /*

        INSERT INTO Menu (ristorante,nome,tipo,prezzo)
        VALUES
        (2,"Mi sballo", "Panino",12.50)

     */

    override fun onUpgrade(p0: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun deleteDatabase(context: Context){
        context.getDatabasePath("mydatabase.db").delete()
    }

    fun getRecensioniByRistoranteId(ristoranteId: String): Cursor {
        val db = readableDatabase
        val query = "SELECT nomeRecensore, votoPrezzo, votoCibo FROM Recensione WHERE ristorante = ?"
        return db.rawQuery(query, arrayOf(ristoranteId))
    }
}