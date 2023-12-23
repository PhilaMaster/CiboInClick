package com.example.myapplication

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class MyDbHelper(context: Context): SQLiteOpenHelper(context, "mydatabase.db", null, 1) {
    override fun onCreate(p0: SQLiteDatabase) {
        p0.execSQL("CREATE TABLE Ristorante (id INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT, telefono TEXT, longitudine REAL, latitudine REAL);")
        p0.execSQL("CREATE TABLE Menu (ristorante INTEGER,nome TEXT, tipo TEXT, prezzo REAL, PRIMARY KEY (ristorante,nome), FOREIGN KEY (ristorante) REFERENCES Ristorante(id));")
        p0.execSQL("CREATE TABLE Recensione (ristorante INTEGER,nomeRecensore TEXT, votoPrezzo REAL, votoCibo REAL, PRIMARY KEY (ristorante,nomeRecensore), FOREIGN KEY (ristorante) REFERENCES Ristorante(id));")

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
                "(1,\"Vegetariano\",\"panino\",6.99)," +
                "(2,\"Caruso\",\"pizza\",12.50);")

        p0.execSQL("INSERT INTO Ristorante (id,nome,telefono,longitudine,latitudine) VALUES " +
                "(1,\"Mi 'ndujo\",\"3517591007\",39.35363861160489, 16.23595023170938);")
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
}