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


        //panini

        p0.execSQL("INSERT INTO Ristorante (_id,nome,telefono,numPrenotazioni, ultimoGiorno, longitudine,latitudine) VALUES " +
                "(1,\"Mi 'ndujo\",\"3517591007\",5,\"martedì\",39.35363861160489, 16.23595023170938)," +
                "(2,\"McDonald's\",\"0984839473\",1,\"mercoledì\",39.35343241088686, 16.23448278179753)," +
                "(3,\"Kmzero\",\"3423154437\",3,\"giovedì\",39.352934385155166, 16.237561451199994)," +
                "(4,\"Burgeria\",\"3517168626\",2,\"venerdì\",39.353526195099874, 16.2367441926086);")

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

        p0.execSQL("INSERT INTO Menu (ristorante,nome,tipo,prezzo) VALUES" +
                "(4,\"Doppio Hamburger\",\"panino\",9.99)," +
                "(4,\"Vegetariano\",\"panino\",8.99)," +
                "(4,\"Smash Burger\",\"panino\",10.99)," +
                "(4,\"The King\",\"panino\",14.99)," +
                "(3,\"Marinaro\",\"panino\",9.99)," +
                "(3,\"Bufalino\",\"panino\",10.99)," +
                "(3,\"Italiano\",\"panino\",6.99)," +
                "(3,\"Casareccio\",\"panino\",12.99)," +
                "(2,\"Vegano\",\"panino\",7.99)," +
                "(2,\"Salamino\",\"panino\",9.99)," +
                "(2,\"Boscaiolo\",\"panino\",9.99)," +
                "(2,\"Formaggioso\",\"panino\",11.99);")

        //pizza

        p0.execSQL("INSERT INTO Ristorante (_id,nome,telefono,numPrenotazioni, ultimoGiorno, longitudine,latitudine) VALUES " +
                "(5,\"Mammarè\",\"3495639913\",7,\"null\",39.3443109232684, 16.244396367539355)," +
                "(6,\"Pizza Rosso\",\"3515733575\",3,\"lunedì\",39.34585845258052, 16.242164880529803)," +
                "(7,\"Doppiozero\",\"0984456419\",2,\"null\",39.352578669588496, 16.238989145111344)," +
                "(8,\"Stop Pizza\",\"0984838375\",2,\"null\",39.35146150573715, 16.244095244086427);")

        p0.execSQL("INSERT INTO Menu (ristorante,nome,tipo,prezzo) VALUES" +
                "(5,\"Margherita\",\"pizza\",5.99)," +
                "(5,\"Americana\",\"pizza\",6.99)," +
                "(5,\"4 Formaggi\",\"pizza\",6.99)," +
                "(5,\"Tonno e cipolla\",\"pizza\",7.99)," +
                "(6,\"4 Stagioni\",\"pizza\",8.99)," +
                "(6,\"Margherita\",\"pizza\",4.99)," +
                "(6,\"Olive\",\"pizza\",6.99)," +
                "(6,\"Napoletana\",\"pizza\",7.99)," +
                "(7,\"Bufala\",\"pizza\",8.99)," +
                "(7,\"Specialità della Casa\",\"pizza\",9.99)," +
                "(7,\"4 Formaggi\",\"pizza\",8.99)," +
                "(7,\"'Nduja e Ricotta\",\"pizza\",6.99)," +
                "(8,\"Americana\",\"pizza\",6.99)," +
                "(8,\"Zucca\",\"pizza\",7.99)," +
                "(8,\"Tonno e Cipolle\",\"pizza\",5.99)," +
                "(8,\"4 Stagioni\",\"pizza\",7.99);")

        //sushi

        p0.execSQL("INSERT INTO Ristorante (_id,nome,telefono,numPrenotazioni, ultimoGiorno, longitudine,latitudine) VALUES " +
                "(9,\"Sushi Kokusai\",\"09841803721\",4,\"sabato\",39.35004872447723, 16.242741655210985)," +
                "(10,\"888 Sushi\",\"09841453766\",1,\"null\",39.354224213458075, 16.24089178100956)," +
                "(11,\"Boa Sorte\",\"09841455067\",2,\"null\",39.33507153590957, 16.24279755359297)," +
                "(12,\"Sushi Garden\",\"0984306182\",3,\"domenica\",39.32632723167703, 16.248763574935246);")

        p0.execSQL("INSERT INTO Menu (ristorante,nome,tipo,prezzo) VALUES" +
                "(9,\"All you can eat\",\"sushi\",15.99)," +
                "(9,\"Pokè\",\"sushi\",13.99)," +
                "(9,\"Piatti Speciali\",\"sushi\",9.99)," +
                "(10,\"All you can eat\",\"sushi\",14.99)," +
                "(10,\"Piatti Speciali\",\"sushi\",8.99)," +
                "(11,\"All you can eat\",\"sushi\",14.99)," +
                "(11,\"Piatti Speciali\",\"sushi\",8.99)," +
                "(12,\"All you can eat\",\"sushi\",17.99)," +
                "(12,\"Pokè\",\"sushi\",11.99)," +
                "(12,\"Piatti Speciali\",\"sushi\",9.99);")



        p0.execSQL("INSERT INTO Recensione (ristorante, nomeRecensore, votoPrezzo, votoCibo) VALUES" +
                "(1, \"Mario\", 2.5, 4.0)," +
                "(1, \"Luca\", 3.0, 3.5)," +
                "(1, \"Giovanna\", 4.5, 2.0)," +
                "(2, \"Giulia\", 4.0, 4.5)," +
                "(2, \"Alessio\", 2.0, 3.0)," +
                "(2, \"Valentina\", 3.5, 4.0)," +
                "(3, \"Francesco\", 4.5, 2.5)," +
                "(3, \"Sofia\", 3.0, 3.5)," +
                "(3, \"Marco\", 2.0, 4.0)," +
                "(4, \"Lorenzo\", 3.5, 4.5)," +
                "(4, \"Elena\", 4.0, 3.0)," +
                "(4, \"Matteo\", 2.5, 4.0)," +
                "(5, \"Rosa\", 4.0, 3.5)," +
                "(5, \"Fabio\", 2.0, 4.5)," +
                "(5, \"Paola\", 3.5, 2.5)," +
                "(6, \"Simone\", 3.0, 4.0)," +
                "(6, \"Giorgio\", 4.5, 2.0)," +
                "(6, \"Federica\", 2.5, 3.5)," +
                "(7, \"Roberto\", 4.0, 4.0)," +
                "(7, \"Caterina\", 3.5, 2.5)," +
                "(7, \"Antonio\", 2.0, 3.0)," +
                "(8, \"Alessandra\", 4.5, 3.0)," +
                "(8, \"Davide\", 3.0, 4.5)," +
                "(8, \"Chiara\", 2.5, 2.0)," +
                "(9, \"Michele\", 3.5, 3.5)," +
                "(9, \"Elisa\", 2.0, 4.0)," +
                "(9, \"Nicola\", 4.0, 2.5)," +
                "(10, \"Laura\", 4.0, 3.0)," +
                "(10, \"Gianluca\", 3.5, 4.5)," +
                "(10, \"Serena\", 2.0, 2.5)," +
                "(11, \"Andrea\", 4.5, 4.5)," +
                "(11, \"Roberta\", 3.0, 3.0)," +
                "(11, \"Enrico\", 2.5, 2.0)," +
                "(12, \"Gabriele\", 4.0, 3.5)," +
                "(12, \"Stefania\", 2.0, 4.0)," +
                "(12, \"Il QG\", 3.5, 3.0);");


    }
    /*

        INSERT INTO Menu (ristorante,nome,tipo,prezzo)
        VALUES
        (2,"Mi sballo", "Panino",12.50)

     */

    override fun onUpgrade(p0: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun deleteDatabase(context: Context){//funzione di debug
        context.getDatabasePath("mydatabase.db").delete()
    }

    fun getRecensioniByRistoranteId(ristoranteId: String): Cursor {
        val db = readableDatabase
        val query = "SELECT nomeRecensore, votoPrezzo, votoCibo FROM Recensione WHERE ristorante = ?"
        return db.rawQuery(query, arrayOf(ristoranteId))
    }
}