package com.example.proyectobeta.Usuario;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class UsuarioProvider extends ContentProvider {
    private static final String AUTHORITY = "com.example.proyectobeta.usuario";
    private static final String URI = "content://" + AUTHORITY + "/usuarios";
    public static final Uri CONTENT_URI = Uri.parse(URI);

    // Definimos el objeto UriMatcher
    private static final int USUARIOS = 1; // Acceso generico a tabla
    private static final int USUARIOS_ID = 2; // Acceso a una fila (acceso a usuarios por ID)
    public static final UriMatcher URI_MATCHER; // Objeto UriMatcher

    // Base de datos
    public UsuariosBBDD usuarioBBDD;
    public static final String BD_NOMBRE = "DBUSUARIOS";
    public static final int BD_VERSION = 1;
    public static final String TABLA_USUARIOS = "Usuarios";

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(AUTHORITY,"usuarios", USUARIOS);
        URI_MATCHER.addURI(AUTHORITY,"usuarios/#", USUARIOS_ID);
    }

    @Override
    public boolean onCreate() {
        usuarioBBDD = new UsuariosBBDD(getContext(), BD_NOMBRE, null, BD_VERSION);
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // Si es una consulta a un ID concreto construimos el WHERE
        String where = selection;
        if(URI_MATCHER.match(uri) == USUARIOS_ID){
            where = "_id=" + uri.getLastPathSegment();
        }
        SQLiteDatabase db = usuarioBBDD.getWritableDatabase();

        Cursor c = db.query(TABLA_USUARIOS,projection,where,selectionArgs,null,null,sortOrder);

        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = URI_MATCHER.match(uri);
        switch (match){
            case USUARIOS:
                return "vnd.android.cursor.dir/vnd.com.example.proyectobeta.usuario";

            case USUARIOS_ID:
                return "vnd.android.cursor.item/vnd.example.proyectobeta.usuario";
        }

        return null;

    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long regId;
        SQLiteDatabase db = usuarioBBDD.getWritableDatabase();

        regId = db.insert(TABLA_USUARIOS,null,values);

        Uri newUri = ContentUris.withAppendedId(CONTENT_URI,regId);

        return newUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int cont;

        String where = selection;
        if(URI_MATCHER.match(uri) == USUARIOS_ID) {
            where = "_id" + uri.getLastPathSegment();
        }
        SQLiteDatabase db = usuarioBBDD.getWritableDatabase();

        cont = db.delete(TABLA_USUARIOS,where,selectionArgs);

        return cont;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int cont;

        String where = selection;
        if(URI_MATCHER.match(uri) == USUARIOS_ID) {
            where = "_id" + uri.getLastPathSegment();
        }
        SQLiteDatabase db = usuarioBBDD.getWritableDatabase();

        cont = db.update(TABLA_USUARIOS,values,where,selectionArgs);

        return cont;
    }

    public UsuarioProvider(){}

    // Clase interna para declarar las constantes de columna
    public static final class Usuarios implements BaseColumns {
        private Usuarios(){}

        // Nombre de columnas
        public static final String COL_USER = "user_name";
        public static final String COL_PASSWORD = "user_pass";
        public static final String COL_EMAIL = "user_mail";
        public static final String COL_DATE = "user_birth";
        public static final String COL_ACCTYPE = "user_acc";
        public static final String COL_ICON = "user_image";


    }
}
