package com.example.proyectobeta.List;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectobeta.R;
import com.example.proyectobeta.Usuario.UsuarioProvider;
import com.google.android.material.search.SearchBar;

import java.util.ArrayList;

public class List extends AppCompatActivity {

    private RecyclerAdapter adapter;
    private RecyclerView rvList;
    private Usuario userLog;

    private SearchView searchView;
    private static final int REQUEST_EDIT_USER = 102;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        Intent intent = getIntent();
        userLog = (Usuario) intent.getSerializableExtra("userLog");

        if (savedInstanceState == null) {
            listar();
        }

        searchView = findViewById(R.id.search_bar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filtrar(newText);
                listar();
                return true;
            }
        });
        
        rvList = findViewById(R.id.rvList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        listar();
    }

    private void listar() {
        rvList = findViewById(R.id.rvList);

        String[] columnas = new String[]{
                UsuarioProvider.Usuarios._ID,
                UsuarioProvider.Usuarios.COL_USER,
                UsuarioProvider.Usuarios.COL_EMAIL,
                UsuarioProvider.Usuarios.COL_PASSWORD,
                UsuarioProvider.Usuarios.COL_DATE,
                UsuarioProvider.Usuarios.COL_ACCTYPE,
                UsuarioProvider.Usuarios.COL_ICON
        };
        Uri versionesUri = UsuarioProvider.CONTENT_URI;
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(versionesUri,
                columnas,
                null,
                null,
                null);
        Usuario objetoDato;

        ArrayList<Usuario> datos = new ArrayList<>();
        adapter = new RecyclerAdapter(this, datos, userLog);

        datos.clear();

        if (cur != null) {
            if (cur.moveToFirst()) {
                int colId = cur.getColumnIndex(UsuarioProvider.Usuarios._ID);
                int colUse = cur.getColumnIndex(UsuarioProvider.Usuarios.COL_USER);
                int colEma = cur.getColumnIndex(UsuarioProvider.Usuarios.COL_EMAIL);
                int colPas = cur.getColumnIndex(UsuarioProvider.Usuarios.COL_PASSWORD);
                int colDat = cur.getColumnIndex(UsuarioProvider.Usuarios.COL_DATE);
                int colAcc = cur.getColumnIndex(UsuarioProvider.Usuarios.COL_ACCTYPE);
                int colIcon = cur.getColumnIndex(UsuarioProvider.Usuarios.COL_ICON);

                do {
                    int id = cur.getInt(colId);
                    String user = cur.getString(colUse);
                    String mail = cur.getString(colEma);
                    String pass = cur.getString(colPas);
                    String date = cur.getString(colDat);
                    int accType = cur.getInt(colAcc);
                    byte[] icon = cur.getBlob(colIcon);
                    objetoDato = new Usuario(id, user, mail, pass, date, accType, icon);
                    datos.add(objetoDato);
                } while (cur.moveToNext());
            }
            cur.close();
        }
        rvList.setAdapter(adapter);
        rvList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_USER && resultCode == RESULT_OK) {
            listar();
        }
    }
}
