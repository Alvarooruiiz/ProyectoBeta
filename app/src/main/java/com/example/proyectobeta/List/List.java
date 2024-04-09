package com.example.proyectobeta.List;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectobeta.Carousel.Carousel;
import com.example.proyectobeta.Login.Login;
import com.example.proyectobeta.R;
import com.example.proyectobeta.Usuario.UsuarioProvider;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

public class List extends AppCompatActivity implements OnAvatarClickListener, OnItemCardViewClickListener {

    private RecyclerAdapterList adapter;
    private RecyclerView rvList;
    private Usuario userLog;
    private ArrayList<Usuario> listUsers;

    private Toolbar toolbar;
    private BottomSheetDialog bottomSheetDialog;
    private static final int MnOp2 = 1;


    private SearchView searchView;
    private ArrayList<Usuario> usuariosMarcados;
    private static final int REQUEST_EDIT_USER = 102;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        Intent intent = getIntent();
        userLog = (Usuario) intent.getSerializableExtra("userLog");



        usuariosMarcados = new ArrayList<>(); // Inicializar la lista aquí

        adapter = new RecyclerAdapterList(this, new ArrayList<>(), userLog,usuariosMarcados); // Adaptador vacío inicialmente
        rvList = findViewById(R.id.rvList);


        rvList = findViewById(R.id.rvList);

        searchView = findViewById(R.id.search_bar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtrar(newText);
                return true;
            }
        });

        toolbar = findViewById(R.id.tbToolbar);
        setSupportActionBar(toolbar);

    }



    // Método para manejar el click en el checkbox


    @Override
    protected void onResume() {
        super.onResume();
        listar();
    }

    public void eliminarElementosSeleccionados() {

    }


    private void listar() {
        String[] columnas = new String[]{
                UsuarioProvider.Usuarios._ID,
                UsuarioProvider.Usuarios.COL_USER,
                UsuarioProvider.Usuarios.COL_EMAIL,
                UsuarioProvider.Usuarios.COL_PASSWORD,
                UsuarioProvider.Usuarios.COL_DATE,
                UsuarioProvider.Usuarios.COL_ACCTYPE,
                UsuarioProvider.Usuarios.COL_ICON,
                UsuarioProvider.Usuarios.COL_STATUS
        };
        Uri versionesUri = UsuarioProvider.CONTENT_URI_USUARIOS;
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(versionesUri,
                columnas,
                null,
                null,
                null);
        Usuario objetoDato;

        listUsers = new ArrayList<>();

        if (cur != null) {
            if (cur.moveToFirst()) {
                int colId = cur.getColumnIndex(UsuarioProvider.Usuarios._ID);
                int colUse = cur.getColumnIndex(UsuarioProvider.Usuarios.COL_USER);
                int colEma = cur.getColumnIndex(UsuarioProvider.Usuarios.COL_EMAIL);
                int colPas = cur.getColumnIndex(UsuarioProvider.Usuarios.COL_PASSWORD);
                int colDat = cur.getColumnIndex(UsuarioProvider.Usuarios.COL_DATE);
                int colAcc = cur.getColumnIndex(UsuarioProvider.Usuarios.COL_ACCTYPE);
                int colIcon = cur.getColumnIndex(UsuarioProvider.Usuarios.COL_ICON);
                int colStatus = cur.getColumnIndex(UsuarioProvider.Usuarios.COL_STATUS);

                do {
                    int id = cur.getInt(colId);
                    String user = cur.getString(colUse);
                    String mail = cur.getString(colEma);
                    String pass = cur.getString(colPas);
                    String date = cur.getString(colDat);
                    int accType = cur.getInt(colAcc);
                    byte[] icon = cur.getBlob(colIcon);
                    int status = cur.getInt(colStatus);
                    Log.d("Estado Usuario", "Estado: " + status);

                    objetoDato = new Usuario(id, user, mail, pass, date, accType, icon, status);
                    listUsers.add(objetoDato);
                } while (cur.moveToNext());
            }
            cur.close();
        }

        adapter = new RecyclerAdapterList(this, listUsers, userLog,usuariosMarcados);
        adapter.setOnAvatarClickListener(this);
        rvList.setAdapter(adapter);
        rvList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

    }

    private void filtrar(String texto) {
        ArrayList<Usuario> usuariosFiltrados = new ArrayList<>();
        if (listUsers != null) {
            if (texto.isEmpty()) {
                usuariosFiltrados.addAll(listUsers);
            } else {
                texto = texto.toLowerCase();
                for (Usuario usuario : listUsers) {
                    if (usuario.getUserName().toLowerCase().contains(texto) ||
                            usuario.getUserMail().toLowerCase().contains(texto) ||
                            usuario.getUserBirth().toLowerCase().contains(texto)) {
                        usuariosFiltrados.add(usuario);
                    }
                }
            }
        }
        adapter = new RecyclerAdapterList(this, usuariosFiltrados, userLog,usuariosMarcados);
        rvList.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_USER && resultCode == RESULT_OK) {
            listar();
        }
    }

    @Override
    public void onAvatarClick(Usuario usuario) {
        Intent intent = new Intent(List.this, Carousel.class);
        intent.putExtra("userId", usuario.getId());
        startActivity(intent);
    }


    @Override
    public void OnItemCardViewClick(Usuario usuario) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Intent intent = getIntent();
        userLog = (Usuario) intent.getSerializableExtra("userLog");
        if(userLog.getUserAcc()==0){
            getMenuInflater().inflate(R.menu.top_app_bar, menu);
            return super.onCreateOptionsMenu(menu);
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            if(!usuariosMarcados.isEmpty()){
                mostrarBottomSheet();
                return true;
            }else {
                Toast.makeText(this, "No ha seleccionado ningun usuario", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void mostrarBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        Button deleteButton = bottomSheetView.findViewById(R.id.btnDelete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarUsuariosMarcados();
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.show();
    }

    private void eliminarUsuariosMarcados() {
        for (Usuario usuario : usuariosMarcados) {
            String selection = UsuarioProvider.Usuarios._ID + "=?";
            String[] selectionArgs = {String.valueOf(usuario.getId())};

            int rowsDeleted = getContentResolver().delete(UsuarioProvider.CONTENT_URI_USUARIOS, selection, selectionArgs);

            if (rowsDeleted > 0) {
                Toast.makeText(this, "Usuarios eliminados exitosmente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al eliminar usuarios", Toast.LENGTH_SHORT).show();
            }
        }

        listUsers.removeAll(usuariosMarcados);
        adapter.notifyDataSetChanged();
        adapter.clearSelection();
    }





}