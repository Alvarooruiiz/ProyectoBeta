package com.example.proyectobeta.Login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectobeta.List.List;
import com.example.proyectobeta.List.Usuario;
import com.example.proyectobeta.R;
import com.example.proyectobeta.Register.Register;
import com.example.proyectobeta.Usuario.UsuarioProvider;
import com.google.android.material.textfield.TextInputLayout;

public class Login extends AppCompatActivity {

    private Button btnRegisterLayout;
    private Button btnLogin;
    private Intent intentRegister;
    private Intent intentList;
    private TextInputLayout tilUser;
    private TextInputLayout tilPass;
    private static final int REQUEST_REGISTER = 1;
    private static final int REQUEST_LOGIN = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_layout);

        btnLogin=findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Usuario usuarioLog = getAuthenticatedUser();

                if (usuarioLog != null) {
                    intentList = new Intent(Login.this, List.class);
                    intentList.putExtra("userLog", usuarioLog);
                    startActivityForResult(intentList, REQUEST_LOGIN);
                } else {
                    Toast.makeText(Login.this, "La cuenta no existe o la contraseña es incorrecta", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnRegisterLayout = findViewById(R.id.btnRegisterLayout);
        btnRegisterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentRegister = new Intent(Login.this, Register.class);
                startActivityForResult(intentRegister, REQUEST_REGISTER);
            }
        });
    }

    private Usuario getAuthenticatedUser() {
        tilUser = findViewById(R.id.txtUserLog);
        EditText etUser = tilUser.getEditText();
        String userText = etUser.getText().toString().trim();

        tilPass = findViewById(R.id.txtPassLog);
        EditText etPass = tilPass.getEditText();
        String passText = etPass.getText().toString().trim();

        String[] projection = {
                UsuarioProvider.Usuarios._ID,
                UsuarioProvider.Usuarios.COL_USER,
                UsuarioProvider.Usuarios.COL_PASSWORD,
                UsuarioProvider.Usuarios.COL_ACCTYPE,
                UsuarioProvider.Usuarios.COL_EMAIL,
                UsuarioProvider.Usuarios.COL_DATE,
                UsuarioProvider.Usuarios.COL_ICON
        };

        String selection = UsuarioProvider.Usuarios.COL_USER + " = ?";
        String[] selectionArgs = {userText};

        Cursor cursor = getContentResolver().query(UsuarioProvider.CONTENT_URI, projection, selection, selectionArgs, null);

        if (cursor != null && cursor.moveToFirst()) {
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(UsuarioProvider.Usuarios._ID));
            String user = cursor.getString(cursor.getColumnIndexOrThrow(UsuarioProvider.Usuarios.COL_USER));
            String password = cursor.getString(cursor.getColumnIndexOrThrow(UsuarioProvider.Usuarios.COL_PASSWORD));
            int accType = cursor.getInt(cursor.getColumnIndexOrThrow(UsuarioProvider.Usuarios.COL_ACCTYPE));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(UsuarioProvider.Usuarios.COL_EMAIL));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(UsuarioProvider.Usuarios.COL_DATE));
            byte[] icon = cursor.getBlob(cursor.getColumnIndexOrThrow(UsuarioProvider.Usuarios.COL_ICON));

            Usuario usuario = new Usuario(userId, user, email, password, date, accType, icon);
            cursor.close();
            if (passText.equals(password)) {
                return usuario;
            }
        }
        return null;
    }
}