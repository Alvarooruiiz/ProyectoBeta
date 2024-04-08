package com.example.proyectobeta.Usuario;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class UsuariosBBDD extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "UsuariosDB";


    private final String sqlCreateUsuarios ="CREATE TABLE Usuarios (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "user_name TEXT, " +
            "user_pass TEXT, " +
            "user_mail TEXT, " +
            "user_birth TEXT, " +
            "user_acc INTEGER, " +
            "user_image BLOB, " +
            "user_status INTEGER DEFAULT 0)";

    private final String sqlCreateImages ="CREATE TABLE Imagenes (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "user_id INTEGER, " +
            "image_url BLOB, " +
            "FOREIGN KEY(user_id) REFERENCES Usuarios(_id))";
    public UsuariosBBDD(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlCreateUsuarios);
        db.execSQL(sqlCreateImages);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Usuarios");
        db.execSQL("DROP TABLE IF EXISTS Imagenes");
        db.execSQL(sqlCreateUsuarios);
        db.execSQL(sqlCreateImages);
    }
}
