package com.example.proyectobeta.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectobeta.R;
import com.example.proyectobeta.Register.DatePickerFragment;
import com.example.proyectobeta.Usuario.UsuarioProvider;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class EditUser extends AppCompatActivity {
    private int typeAcc;

    private TextInputLayout tilUser;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private EditText etDate;
    private String birthdate;
    private AutoCompleteTextView spAcc;
    private TextInputLayout spAcc2;
    private int userId;

    private Button btnEdit;
    private Button btnDelete;

    private Intent intentList;
    private ImageView ivIcon;
    private boolean imageOn = false;
    private Usuario userLog;
    private static final int REQUEST_SELECT_IMAGE = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 101;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_user_layout);


        tilUser = findViewById(R.id.txtUserReg);
        tilEmail = findViewById(R.id.txtEmailReg);
        tilPassword = findViewById(R.id.txtPasswordReg);
        etDate = findViewById(R.id.etDate);
        spAcc = findViewById(R.id.spAcc);
        spAcc2 = findViewById(R.id.sp_typeAcc);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        ivIcon = findViewById(R.id.ivIconImage);

        Intent intent = getIntent();
        Usuario userSelected = (Usuario) intent.getSerializableExtra("userSelected");
        userLog = (Usuario) intent.getSerializableExtra("userLog");
        typeAcc = userLog.getUserAcc();
        if(typeAcc==0){
            spAcc.setEnabled(true);
            spAcc2.setClickable(true);
        }


        if (userSelected != null) {
            userId = userSelected.getId();
            byte[] imageData = userSelected.getUserImage();
            mostrarDetallesUsuario(userSelected);
            if (imageData != null) {

                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                ivIcon.setImageBitmap(bitmap);
            } else {
                ivIcon.setImageResource(R.drawable.user_icon);
            }
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.account_types, android.R.layout.simple_dropdown_item_1line);
        spAcc.setAdapter(adapter);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editarUsuario(v);
                setResult(RESULT_OK);
                finish();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarUsuario(v);
                setResult(RESULT_OK);
                finish();

            }
        });
        ivIcon = findViewById(R.id.ivIconImage);
        ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionsDialog();

            }
        });

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });



        spAcc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = parent.getItemAtPosition(position).toString();
                if (selectedOption.equals("Admin")) {
                    typeAcc = 0;
                } else if (selectedOption.equals("Normal")) {
                    typeAcc = 1;
                }
            }
        });
    }


    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                final String selectedDate = day + " / " + (month+1) + " / " + year;
                etDate.setText(selectedDate);
                birthdate = selectedDate;
            }
        });

        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void limpiarCampos() {
        tilUser.getEditText().setText("");
        tilEmail.getEditText().setText("");
        tilPassword.getEditText().setText("");
        etDate.setText("");
        spAcc.setText("");
    }



    private void mostrarDetallesUsuario(Usuario usuario) {
        tilUser.getEditText().setText(usuario.getUserName());
        tilEmail.getEditText().setText(usuario.getUserMail());
        tilPassword.getEditText().setText(usuario.getUserPass());
        etDate.setText(usuario.getUserBirth());
        if(usuario.getUserAcc()==1){
            spAcc.setText("Normal");
        }else if (usuario.getUserAcc()==0){
            spAcc.setText("Admin");
        }else{
            spAcc.setText("");
        }

        if (usuario.getUserImage() != null) {
            String imageUriString = new String(usuario.getUserImage());
            ivIcon.setImageURI(Uri.parse(imageUriString));
        }
    }
    public void editarUsuario(View view) {
        String newUser = tilUser.getEditText().getText().toString();
        String newEmail = tilEmail.getEditText().getText().toString();
        String newPassword = tilPassword.getEditText().getText().toString();
        String newDate = etDate.getText().toString();
        int newAccType = 1;

        String selectedOption = spAcc.getText().toString();
        if (selectedOption.equals("Admin")) {
            newAccType = 0;
        }

        ContentValues values = new ContentValues();
        values.put(UsuarioProvider.Usuarios.COL_USER, newUser);
        values.put(UsuarioProvider.Usuarios.COL_EMAIL, newEmail);
        values.put(UsuarioProvider.Usuarios.COL_PASSWORD, newPassword);
        values.put(UsuarioProvider.Usuarios.COL_DATE, newDate);
        values.put(UsuarioProvider.Usuarios.COL_ACCTYPE, newAccType);

        if (imageOn) {
            Bitmap bitmap = ((BitmapDrawable) ivIcon.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            values.put(UsuarioProvider.Usuarios.COL_ICON, imageBytes);
        }

        String selection = UsuarioProvider.Usuarios._ID + "=?";
        String[] selectionArgs = {String.valueOf(userId)};

        int rowsUpdated = getContentResolver().update(UsuarioProvider.CONTENT_URI, values, selection, selectionArgs);

        if (rowsUpdated > 0) {
            Toast.makeText(this, "Usuario actualizado correctamente", Toast.LENGTH_SHORT).show();
            limpiarCampos();
        } else {
            Toast.makeText(this, "Error al actualizar usuario", Toast.LENGTH_SHORT).show();
        }
    }

    public void eliminarUsuario(View view) {
        String selection = UsuarioProvider.Usuarios._ID + "=?";
        String[] selectionArgs = {String.valueOf(userId)};

        int rowsDeleted = getContentResolver().delete(UsuarioProvider.CONTENT_URI, selection, selectionArgs);

        if (rowsDeleted > 0) {
            Toast.makeText(this, "Usuario eliminado correctamente", Toast.LENGTH_SHORT).show();
            onBackPressed();
        } else {
            Toast.makeText(this, "Error al eliminar usuario", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_SELECT_IMAGE && data != null) {
                Uri selectedImageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    ivIcon.setImageBitmap(bitmap);
                    imageOn = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                Bundle extras = data.getExtras();
                if (extras != null && extras.containsKey("data")) {
                    Bitmap imgBitmap = (Bitmap) extras.get("data");
                    ivIcon.setImageBitmap(imgBitmap);
                    imageOn = true;
                }
            }
        }
    }

    public void showOptionsDialog() {
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
        ((EditUser) this).startActivityForResult(intent, REQUEST_SELECT_IMAGE);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ((EditUser) this).startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }
}