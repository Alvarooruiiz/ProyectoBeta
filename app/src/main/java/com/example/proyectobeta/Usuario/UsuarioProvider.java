package com.example.proyectobeta.Usuario;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class UsuarioProvider extends ContentProvider {
    private static final String AUTHORITY = "com.example.proyectobeta.usuario";

    private static final String USUARIOS_PATH = "usuarios";
    private static final String IMAGENES_PATH = "imagenes";

    private static final String URI_USUARIOS = "content://" + AUTHORITY + "/" + USUARIOS_PATH;
    private static final String URI_IMAGENES = "content://" + AUTHORITY + "/" + IMAGENES_PATH;

    public static final Uri CONTENT_URI_USUARIOS = Uri.parse(URI_USUARIOS);
    public static final Uri CONTENT_URI_IMAGENES = Uri.parse(URI_IMAGENES);

    // Definimos el objeto UriMatcher
    private static final int USUARIOS = 1;
    private static final int USUARIOS_ID = 2;
    public static final UriMatcher URI_MATCHER;

    // Base de datos
    public UsuariosBBDD usuarioBBDD;
    public static final String BD_NOMBRE = "DBUSUARIOS_V2";
    public static final int BD_VERSION = 2;
    public static final String TABLA_USUARIOS = "Usuarios";
    public static final String TABLA_IMAGENES = "Imagenes";

    private static final int IMAGENES = 3;
    private static final int IMAGENES_ID = 4;



    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(AUTHORITY,"usuarios", USUARIOS);
        URI_MATCHER.addURI(AUTHORITY,"usuarios/#", USUARIOS_ID);
        URI_MATCHER.addURI(AUTHORITY,"imagenes", IMAGENES);
        URI_MATCHER.addURI(AUTHORITY,"imagenes/#", IMAGENES_ID);
    }

    @Override
    public boolean onCreate() {
        usuarioBBDD = new UsuariosBBDD(getContext(), BD_NOMBRE, null, BD_VERSION);
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = usuarioBBDD.getWritableDatabase();
        Cursor cursor;

        int match = URI_MATCHER.match(uri);
        switch (match) {
            case USUARIOS:
                cursor = db.query(TABLA_USUARIOS, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case USUARIOS_ID:
                String userId = uri.getLastPathSegment();
                String userSelection = "_id=?";
                String[] userSelectionArgs = new String[]{userId};
                cursor = db.query(TABLA_USUARIOS, projection, userSelection, userSelectionArgs, null, null, sortOrder);
                break;
            case IMAGENES:
                cursor = db.query(TABLA_IMAGENES, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case IMAGENES_ID:
                String imageId = uri.getLastPathSegment();
                String imageSelection = "_id=?";
                String[] imageSelectionArgs = new String[]{imageId};
                cursor = db.query(TABLA_IMAGENES, projection, imageSelection, imageSelectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("URI desconocido: " + uri);
        }

        return cursor;
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
            case IMAGENES:
                return "vnd.android.cursor.dir/vnd.com.example.proyectobeta.imagen";
            case IMAGENES_ID:
                return "vnd.android.cursor.item/vnd.com.example.proyectobeta.imagen";
            default:
                throw new IllegalArgumentException("URI desconocido: " + uri);
        }

    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = usuarioBBDD.getWritableDatabase();
        long regId = -1;
        switch (URI_MATCHER.match(uri)) {
            case USUARIOS:
                regId = db.insert(TABLA_USUARIOS, null, values);
                break;
            case IMAGENES:
                regId = db.insert(TABLA_IMAGENES, null, values);
                break;
            default:
                throw new IllegalArgumentException("URI desconocido: " + uri);
        }
        if (regId != -1) {
            Uri newUri = ContentUris.withAppendedId(uri, regId);
            return newUri;
        } else {
            throw new SQLException("Falla al insertar fila en: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = usuarioBBDD.getWritableDatabase();
        int cont;
        switch (URI_MATCHER.match(uri)) {
            case USUARIOS:
                cont = db.delete(TABLA_USUARIOS, selection, selectionArgs);
                break;
            case USUARIOS_ID:
                String userId = uri.getLastPathSegment();

                String whereImages = "user_id=?";
                String[] whereImagesArgs = new String[]{userId};
                db.delete(TABLA_IMAGENES, whereImages, whereImagesArgs);

                String whereUser = "_id=?";
                String[] whereUserArgs = new String[]{userId};
                cont = db.delete(TABLA_USUARIOS, whereUser, whereUserArgs);
                break;
            case IMAGENES_ID:
                String whereImagenes = "_id=" + uri.getLastPathSegment(); // Agrega el operador de igualdad
                cont = db.delete(TABLA_IMAGENES, whereImagenes, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("URI desconocido delete: " + uri);
        }
        return cont;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = usuarioBBDD.getWritableDatabase();
        int cont;
        switch (URI_MATCHER.match(uri)) {
            case USUARIOS_ID:
                String userId = uri.getLastPathSegment();
                String whereUser = Usuarios._ID + "=?";
                String[] whereUserArgs = new String[]{userId};
                cont = db.update(TABLA_USUARIOS, values, whereUser, whereUserArgs);
                break;
            case IMAGENES_ID:
                String imageId = uri.getLastPathSegment();
                String whereImagenes = Imagenes._ID + "=?";
                String[] whereImagenesArgs = new String[]{imageId};
                cont = db.update(TABLA_IMAGENES, values, whereImagenes, whereImagenesArgs);
                break;
            default:
                throw new IllegalArgumentException("URI desconocido en el update: " + uri);
        }
        return cont;
    }


    public UsuarioProvider(){}

    public static final class Usuarios implements BaseColumns {
        private Usuarios(){}

        public static final String COL_USER = "user_name";
        public static final String COL_PASSWORD = "user_pass";
        public static final String COL_EMAIL = "user_mail";
        public static final String COL_DATE = "user_birth";
        public static final String COL_ACCTYPE = "user_acc";
        public static final String COL_ICON = "user_image";
        public static final String COL_STATUS = "user_status";

    }

    public static final class Imagenes implements BaseColumns {
        private Imagenes(){}

        public static final String COL_USER_ID = "user_id";
        public static final String COL_IMAGE_URL = "image_url";
    }
}
