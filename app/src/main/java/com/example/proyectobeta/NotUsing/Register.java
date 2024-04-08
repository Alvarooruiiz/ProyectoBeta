package com.example.proyectobeta.NotUsing;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.graphics.drawable.BitmapDrawable;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectobeta.Login.Login;
import com.example.proyectobeta.R;
import com.example.proyectobeta.DatePicker.DatePickerFragment;
import com.example.proyectobeta.Usuario.UsuarioProvider;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Register extends AppCompatActivity {

    private Button btnDelete;
    private Button btnRegister;
    private Button btnLoginLayout;
    private Intent loginLayout;
    private EditText etDate;
    private ImageView ivIconImage;
    private String birthdate; // String de la fecha de nacimiento
    private int typeAccount;
    private boolean imageOn = false; // Se ha introducido imagen o no

    private static final int REQUEST_SELECT_IMAGE = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 101;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        btnLoginLayout = findViewById(R.id.btnLoginLayout);
        btnLoginLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginLayout = new Intent(Register.this, Login.class);
                startActivity(loginLayout);
            }
        });

        etDate = findViewById(R.id.etDate);
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();

            }
        });

        ivIconImage = findViewById(R.id.ivIconImage);
        ivIconImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionsDialog();

            }
        });

        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.account_types, android.R.layout.simple_dropdown_item_1line);
        autoCompleteTextView.setAdapter(adapter);


        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextInputLayout tilUser = findViewById(R.id.txtUserReg);
                EditText etUser = tilUser.getEditText();
                String userText = etUser.getText().toString().trim();

                TextInputLayout tilPass = findViewById(R.id.txtPasswordReg);
                EditText etPass = tilPass.getEditText();
                String passText = etPass.getText().toString().trim();

                TextInputLayout tipPassRep = findViewById(R.id.txtPasswordRepeatReg);
                EditText etPassRep = tipPassRep.getEditText();
                String passRepText = etPassRep.getText().toString().trim();

                TextInputLayout tilMail = findViewById(R.id.txtEmailReg);
                EditText etMail = tilMail.getEditText();
                String mailText = etMail.getText().toString().trim();

                EditText etDate = findViewById(R.id.etDate);
                String dateText = etDate.getText().toString();


                if (userText.isEmpty()) {
                    Toast.makeText(Register.this, "El usuario no puede estar vacío", Toast.LENGTH_SHORT).show();
                } else if (mailText.isEmpty()) {
                    Toast.makeText(Register.this, "El correo electrónico no puede estar vacío", Toast.LENGTH_SHORT).show();
                } else if (passText.isEmpty()) {
                    Toast.makeText(Register.this, "La contraseña no puede estar vacía", Toast.LENGTH_SHORT).show();
                } else if (passRepText.isEmpty()) {
                    Toast.makeText(Register.this, "Compruebe que ha rellenado todos los valores", Toast.LENGTH_SHORT).show();
                } else if (!passText.equals(passRepText)) {
                    Toast.makeText(Register.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                } else if(dateText.isEmpty()) {
                    Toast.makeText(Register.this, "Introduzca la fecha de nacimiento", Toast.LENGTH_SHORT).show();
                }else if (!imageOn){
                    Toast.makeText(Register.this, "Introduzca la imagen de perfil", Toast.LENGTH_SHORT).show();
                }else if (!spinnerChecked()) {
                    Toast.makeText(Register.this, "Seleccione el tipo de cuenta", Toast.LENGTH_SHORT).show();
                }else {
                    ContentValues registro = new ContentValues();
                    registro.put(UsuarioProvider.Usuarios.COL_USER,userText);
                    registro.put(UsuarioProvider.Usuarios.COL_PASSWORD,passText);
                    registro.put(UsuarioProvider.Usuarios.COL_EMAIL,mailText);
                    registro.put(UsuarioProvider.Usuarios.COL_DATE,birthdate);
                    registro.put(UsuarioProvider.Usuarios.COL_ACCTYPE,typeAccount);


                    if (imageOn) {
                        Bitmap bitmap = ((BitmapDrawable) ivIconImage.getDrawable()).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        byte[] imageData = baos.toByteArray();
                        registro.put(UsuarioProvider.Usuarios.COL_ICON, imageData);
                    }

                    ContentResolver cr = getContentResolver();
                    Uri newUri = cr.insert(UsuarioProvider.CONTENT_URI, registro);
                    if (newUri != null) {
                        Toast.makeText(Register.this, "Usuario guardado", Toast.LENGTH_SHORT).show();
                        loginLayout = new Intent(Register.this, Login.class);
                        startActivity(loginLayout);

                    } else {
                        Toast.makeText(Register.this, "Error al guardar el usuario", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }



    public void cleanEditText(){

    }
    private boolean spinnerChecked() {
        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        String selectedOption = autoCompleteTextView.getText().toString().trim();

        if (!selectedOption.isEmpty()) {
            if (selectedOption.equals("Admin")) {
                typeAccount = 0;
            } else if (selectedOption.equals("Normal")) {
                typeAccount = 1;
            }
            return true;
        } else {
            return false;
        }
    }
    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because January is zero
                final String selectedDate = day + " / " + (month+1) + " / " + year;
                etDate.setText(selectedDate);
                birthdate = selectedDate;
            }
        });

        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void showOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar una opción")
                .setItems(new CharSequence[]{"Seleccionar desde la galeria", "Abrir camara"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case 0:
                                openGallery();
                                break;
                            case 1:
                                openCamera();
                                break;
                            default:
                                break;
                        }
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_SELECT_IMAGE);
    }

    private void openCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_SELECT_IMAGE && data != null) {
                Uri selectedImageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    ivIconImage.setImageBitmap(bitmap);
                    imageOn = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                Bundle extras = data.getExtras();
                if (extras != null && extras.containsKey("data")) {
                    Bitmap imgBitmap = (Bitmap) extras.get("data");
                    ivIconImage.setImageBitmap(imgBitmap);
                    imageOn = true;
                }
            }
        }
    }
}
