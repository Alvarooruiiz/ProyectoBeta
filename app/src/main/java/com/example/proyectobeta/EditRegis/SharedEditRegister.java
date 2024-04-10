package com.example.proyectobeta.EditRegis;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectobeta.Imagen.OnImageClickListener;
import com.example.proyectobeta.Imagen.RecyclerAdapterImages;
import com.example.proyectobeta.List.List;
import com.example.proyectobeta.List.Usuario;
import com.example.proyectobeta.Login.Login;
import com.example.proyectobeta.DatePicker.DatePickerFragment;
import com.example.proyectobeta.R;
import com.example.proyectobeta.Usuario.UsuarioProvider;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class SharedEditRegister extends AppCompatActivity implements OnImageClickListener {

    private ConstraintLayout constraintStatus;
    private TextView tvTittle;
    private TextView tvRegis;

    private TextInputLayout tilUser;
    private TextInputLayout tilPass;
    private TextInputLayout tilPassRep;
    private TextInputLayout tilMail;


    private Button btnDelete;
    private Button btnEdit;
    private Button btnRegister;
    private Button btnLoginLayout;
    private MaterialSwitch switchBaja;


    private AutoCompleteTextView spAcc;
    private TextInputLayout spAcc2;


    private Intent loginLayout;
    private EditText etDate;
    private ImageView ivIconImage;
    private String birthdate;
    private int typeAccount;
    private boolean imageOn = false;


    private Usuario userLog;
    private int typeAcc;
    private int userId;


    private static final int REQUEST_SELECT_IMAGE = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int REQUEST_LOGIN = 102;
    private static final int REQUEST_EDIT_USER = 123;


    private ArrayList<String> imageUrls = new ArrayList<>();

    private RecyclerView rvImagesUser;
    private ArrayList<Bitmap> imagesList;
    private RecyclerAdapterImages recyclerAdapterImages;

    private PendingIntent pendingIntent;
    private final static String CHANNEL_ID = "NOTIFICACION";
    private final static int NOTIFICACION_ID = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shared_layout);


        btnLoginLayout = findViewById(R.id.btnLoginLayout);
        btnRegister = findViewById(R.id.btnRegister);
        btnDelete = findViewById(R.id.btnDelete);
        btnEdit = findViewById(R.id.btnEdit);
        tvRegis = findViewById(R.id.tvYaTeHasReg);
        tvTittle = findViewById(R.id.tvTittle);
        constraintStatus = findViewById(R.id.constraintStatus);
        spAcc = findViewById(R.id.spAcc);
        spAcc2 = findViewById(R.id.sp_typeAcc);
        ivIconImage = findViewById(R.id.ivIconImage);
        switchBaja = findViewById(R.id.switchBaja);


        tilUser = findViewById(R.id.txtUserReg);
        EditText etUser = tilUser.getEditText();
        String userText = etUser.getText().toString().trim();

        tilPass = findViewById(R.id.txtPasswordReg);
        EditText etPass = tilPass.getEditText();
        String passText = etPass.getText().toString().trim();

        tilPassRep = findViewById(R.id.txtPasswordRepeatReg);
        EditText etPassRep = tilPassRep.getEditText();
        String passRepText = etPassRep.getText().toString().trim();

        tilMail = findViewById(R.id.txtEmailReg);
        EditText etMail = tilMail.getEditText();
        String mailText = etMail.getText().toString().trim();

        etDate = findViewById(R.id.etDate);
        String dateText = etDate.getText().toString();


        rvImagesUser = findViewById(R.id.rvImagesUser);
        imagesList = new ArrayList<>();

        recyclerAdapterImages = new RecyclerAdapterImages(this, imagesList);
        recyclerAdapterImages.setOnImageClickListener(this);

        rvImagesUser.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvImagesUser.setAdapter(recyclerAdapterImages);

        boolean isRegistering = getIntent().getBooleanExtra("isRegistering", false);
        Intent intent = getIntent();
        if (intent != null) {
            Usuario userSelected = (Usuario) intent.getSerializableExtra("userSelected");
            userLog = (Usuario) intent.getSerializableExtra("userLog");
            if (userLog != null) {
                typeAcc = userLog.getUserAcc();
                if (typeAcc == 0) {
                    spAcc.setEnabled(true);
                    spAcc2.setClickable(true);
                    switchBaja.setEnabled(true);

                    constraintStatus.setEnabled(true);
                }
            }
            if (userSelected != null) {
                userId = userSelected.getId();
                byte[] imageData = userSelected.getUserImage();
                mostrarDetallesUsuario(userSelected);
                if (imageData != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                    ivIconImage.setImageBitmap(bitmap);
                } else {
                    ivIconImage.setImageResource(R.drawable.user_icon);
                }
            }
        }

        if (isRegistering) {
            btnDelete.setVisibility(View.GONE);
            btnEdit.setVisibility(View.GONE);
            constraintStatus.setVisibility(View.GONE);
            btnLoginLayout.setVisibility(View.VISIBLE);
            tvRegis.setVisibility(View.VISIBLE);
            tvTittle.setText("Registrarse");
        } else {
            btnDelete.setVisibility(View.VISIBLE);
            btnEdit.setVisibility(View.VISIBLE);
            constraintStatus.setVisibility(View.VISIBLE);
            btnLoginLayout.setVisibility(View.GONE);
            tvRegis.setVisibility(View.GONE);
            tvTittle.setText("Editar")
            ;
        }

        btnLoginLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginLayout = new Intent(SharedEditRegister.this, Login.class);
                startActivityForResult(loginLayout, REQUEST_LOGIN);
            }
        });

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();

            }
        });


        ivIconImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionsDialog();

            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.account_types, android.R.layout.simple_dropdown_item_1line);
        spAcc.setAdapter(adapter);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editarUsuario(v)) {
//                    Toast.makeText(SharedEditRegister.this, ""+userLog.getUserAcc(), Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("userLogUpdated", userLog);
                    setResult(RESULT_OK, resultIntent);
                }
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


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (registerUser()) {
                    loginLayout = new Intent(SharedEditRegister.this, Login.class);
                    startActivityForResult(loginLayout, REQUEST_LOGIN);
                    finish();
                }
            }
        });

    }

    private boolean registerUser() {
        if (areFieldsFilled()) {
            EditText etUser = tilUser.getEditText();
            String userText = etUser.getText().toString().trim();

            EditText etMail = tilMail.getEditText();
            String mailText = etMail.getText().toString().trim();

            EditText etPass = tilPass.getEditText();

            ContentValues registro = new ContentValues();
            registro.put(UsuarioProvider.Usuarios.COL_USER, etUser.getText().toString().trim());
            registro.put(UsuarioProvider.Usuarios.COL_PASSWORD, etPass.getText().toString().trim());
            registro.put(UsuarioProvider.Usuarios.COL_EMAIL, etMail.getText().toString().trim());
            registro.put(UsuarioProvider.Usuarios.COL_DATE, etDate.getText().toString());
            registro.put(UsuarioProvider.Usuarios.COL_ACCTYPE, typeAccount);

            Bitmap firstImage = null;

            // Verifica si hay al menos una imagen seleccionada
            if (!imagesList.isEmpty()) {
                // Obtiene la primera imagen de la lista
                firstImage = imagesList.get(0);
            } else {
                // Si no hay ninguna imagen seleccionada, puedes mostrar un mensaje de error o manejar la situación según sea necesario
                Toast.makeText(this, "No has seleccionado ninguna imagen.", Toast.LENGTH_SHORT).show();
                return false; // Retorna falso para indicar que el registro no se puede completar sin una imagen
            }

            if (firstImage != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                firstImage.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] imageData = baos.toByteArray();
                registro.put(UsuarioProvider.Usuarios.COL_ICON, imageData);
            }

            if (isUserExists(userText)) {
                Toast.makeText(SharedEditRegister.this, "El usuario ya existe", Toast.LENGTH_SHORT).show();
                return false;
            } else if (isEmailExists(mailText)) {
                Toast.makeText(SharedEditRegister.this, "El correo ya existe", Toast.LENGTH_SHORT).show();
                return false;
            }

            ContentResolver cr = getContentResolver();
            Uri newUri = cr.insert(UsuarioProvider.CONTENT_URI_USUARIOS, registro);

            if (newUri != null) {
                long userId = ContentUris.parseId(newUri);

                for (Bitmap bitmap : imagesList) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] imageData = baos.toByteArray();

                    ContentValues imageValues = new ContentValues();
                    imageValues.put(UsuarioProvider.Imagenes.COL_USER_ID, userId);
                    imageValues.put(UsuarioProvider.Imagenes.COL_IMAGE_URL, imageData);

                    Uri imagesUri = UsuarioProvider.CONTENT_URI_IMAGENES;
                    getContentResolver().insert(imagesUri, imageValues);
                }

                Toast.makeText(SharedEditRegister.this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show();
                generateNotification(userText);
                return true;
            } else {
                Toast.makeText(SharedEditRegister.this, "Error al guardar el usuario", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return false;
    }

    private void generateNotification(String nombre) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the notification channel group
            NotificationChannelGroup group = new NotificationChannelGroup("group_id", "Group Name");
            notificationManager.createNotificationChannelGroup(group);

            // Create the notification channel
            NotificationChannel notificationChannel = new NotificationChannel("NOTIFICATION_URGENT_ID", "My Notifications", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);

            // Associate the channel with the group
            notificationChannel.setGroup("group_id");

            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
        }

        // Build and display the notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "NOTIFICATION_URGENT_ID");
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setTicker("Notificaión")
                .setContentTitle("Registro")
                .setContentIntent(onClick())
                .setContentText("Se ha registrado " + nombre + " exitosamente")
                .setContentInfo("New");

        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        assert notificationManager != null;
        notificationManager.notify(m, notificationBuilder.build());
    }
    public PendingIntent onClick(){
        Intent notificationIntent = new Intent(this,
                Login.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }
    public boolean areFieldsFilled() {
        EditText etUser = tilUser.getEditText();
        String userText = etUser.getText().toString().trim();

        EditText etMail = tilMail.getEditText();
        String mailText = etMail.getText().toString().trim();

        EditText etPass = tilPass.getEditText();
        String passText = etPass.getText().toString().trim();

        EditText etPassRep = tilPassRep.getEditText();
        String passRepText = etPassRep.getText().toString().trim();

        String dateText = etDate.getText().toString();


        if (userText.isEmpty()) {
            Toast.makeText(SharedEditRegister.this, "El usuario no puede estar vacío", Toast.LENGTH_SHORT).show();
            return false;
        } else if (mailText.isEmpty()) {
            Toast.makeText(SharedEditRegister.this, "El correo electrónico no puede estar vacío", Toast.LENGTH_SHORT).show();
            return false;
        } else if (passText.isEmpty()) {
            Toast.makeText(SharedEditRegister.this, "La contraseña no puede estar vacía", Toast.LENGTH_SHORT).show();
            return false;
        } else if (passRepText.isEmpty()) {
            Toast.makeText(SharedEditRegister.this, "Compruebe que ha rellenado todos los valores", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!passText.equals(passRepText)) {
            Toast.makeText(SharedEditRegister.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return false;
        } else if (dateText.isEmpty()) {
            Toast.makeText(SharedEditRegister.this, "Introduzca la fecha de nacimiento", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!imageOn && ivIconImage.getDrawable() == null) {
            Toast.makeText(SharedEditRegister.this, "Introduzca la imagen de perfil", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!spinnerChecked()) {
            Toast.makeText(SharedEditRegister.this, "Seleccione el tipo de cuenta", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean isUserExists(String userName) {
        Cursor cursor = getContentResolver().query(
                UsuarioProvider.CONTENT_URI_USUARIOS,
                null,
                UsuarioProvider.Usuarios.COL_USER + " = ?",
                new String[]{userName},
                null
        );

        boolean exists = (cursor != null && cursor.getCount() > 0);

        if (cursor != null) {
            cursor.close();
        }

        return exists;
    }

    private boolean isEmailExists(String email) {
        Cursor cursor = getContentResolver().query(
                UsuarioProvider.CONTENT_URI_USUARIOS,
                null,
                UsuarioProvider.Usuarios.COL_EMAIL + " = ?",
                new String[]{email},
                null
        );

        boolean exists = (cursor != null && cursor.getCount() > 0);

        if (cursor != null) {
            cursor.close();
        }

        return exists;
    }

    public void eliminarUsuario(View view) {
        if (userLog.getId() == userId) {
            String selection = UsuarioProvider.Usuarios._ID + "=?";
            String[] selectionArgs = {String.valueOf(userId)};

            int rowsDeleted = getContentResolver().delete(UsuarioProvider.CONTENT_URI_USUARIOS, selection, selectionArgs);

            if (rowsDeleted > 0) {
                Toast.makeText(this, "Usuario eliminado correctamente", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, Login.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Error al eliminar usuario", Toast.LENGTH_SHORT).show();
            }
        } else {
            String selection = UsuarioProvider.Usuarios._ID + "=?";
            String[] selectionArgs = {String.valueOf(userId)};

            int rowsDeleted = getContentResolver().delete(UsuarioProvider.CONTENT_URI_USUARIOS, selection, selectionArgs);

            if (rowsDeleted > 0) {
                Toast.makeText(this, "Usuario eliminado correctamente", Toast.LENGTH_SHORT).show();
                onBackPressed();
            } else {
                Toast.makeText(this, "Error al eliminar usuario", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean editarUsuario(View view) {
        if (!areFieldsFilled()) {
            return false;
        }

        String newUser = tilUser.getEditText().getText().toString();
        String newEmail = tilMail.getEditText().getText().toString();
        String newPassword = tilPass.getEditText().getText().toString();
        String newDate = etDate.getText().toString();
        int newAccType = 1;

        String selectedOption = spAcc.getText().toString();
        if (selectedOption.equals("Admin")) {
            newAccType = 0;
        }
        int newStatus = 0;
        if (switchBaja.isChecked()) {
            newStatus = 1;
        }

        boolean isChangingOwnAccountType = (userId == userLog.getId() && userLog.getUserAcc() != newAccType);



        ContentValues values = new ContentValues();
        values.put(UsuarioProvider.Usuarios.COL_USER, newUser);
        values.put(UsuarioProvider.Usuarios.COL_EMAIL, newEmail);
        values.put(UsuarioProvider.Usuarios.COL_PASSWORD, newPassword);
        values.put(UsuarioProvider.Usuarios.COL_DATE, newDate);
        values.put(UsuarioProvider.Usuarios.COL_ACCTYPE, newAccType);

        Bitmap firstImage = null;

        // Verifica si hay al menos una imagen seleccionada
        if (imageOn && !imagesList.isEmpty()) {
            // Obtiene la primera imagen de la lista
            firstImage = imagesList.get(0);
        }

        if (firstImage != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            firstImage.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] imageData = baos.toByteArray();
            values.put(UsuarioProvider.Usuarios.COL_ICON, imageData);
        }
        values.put(UsuarioProvider.Usuarios.COL_STATUS, newStatus);

        Cursor userCursor = getContentResolver().query(
                UsuarioProvider.CONTENT_URI_USUARIOS,
                null,
                UsuarioProvider.Usuarios.COL_USER + " = ? AND " + UsuarioProvider.Usuarios._ID + " != ?",
                new String[]{newUser, String.valueOf(userId)},
                null
        );

        Cursor emailCursor = getContentResolver().query(
                UsuarioProvider.CONTENT_URI_USUARIOS,
                null,
                UsuarioProvider.Usuarios.COL_EMAIL + " = ? AND " + UsuarioProvider.Usuarios._ID + " != ?",
                new String[]{newEmail, String.valueOf(userId)},
                null
        );

        boolean userExists = (userCursor != null && userCursor.getCount() > 0);
        boolean emailExists = (emailCursor != null && emailCursor.getCount() > 0);

        if (userCursor != null) {
            userCursor.close();
        }

        if (emailCursor != null) {
            emailCursor.close();
        }

        if (userExists) {
            Toast.makeText(this, "El nombre de usuario ya existe", Toast.LENGTH_SHORT).show();
            return false;
        } else if (emailExists) {
            Toast.makeText(this, "El correo electrónico ya existe", Toast.LENGTH_SHORT).show();
            return false;
        }

        Uri uri = UsuarioProvider.CONTENT_URI_USUARIOS.buildUpon()
                .appendPath(String.valueOf(userId))
                .build();

        String selection = UsuarioProvider.Usuarios._ID + "=?";
        String[] selectionArgs = {String.valueOf(userId)};

        int rowsUpdated = getContentResolver().update(uri, values, selection, selectionArgs);
        actualizarImagenesUsuario(userId);


        if (isChangingOwnAccountType) {
            userLog.setUserAcc(newAccType);
        }


        if (rowsUpdated > 0) {
            Toast.makeText(this, "Usuario actualizado correctamente", Toast.LENGTH_SHORT).show();
            limpiarCampos();

        } else {
            Toast.makeText(this, "Error al actualizar usuario", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private void mostrarDetallesUsuario(Usuario usuario) {
        tilUser.getEditText().setText(usuario.getUserName());
        tilMail.getEditText().setText(usuario.getUserMail());
        tilPass.getEditText().setText(usuario.getUserPass());
        tilPassRep.getEditText().setText(usuario.getUserPass());

        etDate.setText(usuario.getUserBirth());
        if (usuario.getUserAcc() == 1) {
            spAcc.setText("Normal");
        } else if (usuario.getUserAcc() == 0) {
            spAcc.setText("Admin");
        } else {
            spAcc.setText("");
        }

        switchBaja.setEnabled(userLog.getUserAcc() != 1);
        spAcc.setEnabled(userLog.getUserAcc() != 1);
        spAcc2.setEnabled(userLog.getUserAcc() != 1);

        if (usuario.getUserImage() != null) {
            String imageUriString = new String(usuario.getUserImage());
            ivIconImage.setImageURI(Uri.parse(imageUriString));
        }


        switchBaja.setChecked(usuario.getUserStatus() == 1);
        cargarImagenesUsuario(usuario.getId());
    }

    private void actualizarImagenesUsuario(int userId) {
        // Obtener las imágenes almacenadas del usuario
        Cursor cursor = getContentResolver().query(
                UsuarioProvider.CONTENT_URI_IMAGENES,
                null,
                UsuarioProvider.Imagenes.COL_USER_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null
        );

        ArrayList<Integer> existingImageIds = new ArrayList<>();

        // Verificar si hay imágenes almacenadas para este usuario
        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") int imageId = cursor.getInt(cursor.getColumnIndex(UsuarioProvider.Imagenes._ID));
                existingImageIds.add(imageId);
            }
            cursor.close();
        }

        // Eliminar las imágenes que ya no están presentes en la lista de imágenes del usuario
        for (Integer existingImageId : existingImageIds) {
            if (!imagesList.contains(existingImageId)) {
                String selection = UsuarioProvider.Imagenes._ID + "=?";
                String[] selectionArgs = {String.valueOf(existingImageId)};
                getContentResolver().delete(UsuarioProvider.CONTENT_URI_IMAGENES, selection, selectionArgs);
            }
        }

        // Insertar las nuevas imágenes que se han agregado
        for (Bitmap bitmap : imagesList) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] imageData = baos.toByteArray();

            ContentValues imageValues = new ContentValues();
            imageValues.put(UsuarioProvider.Imagenes.COL_USER_ID, userId);
            imageValues.put(UsuarioProvider.Imagenes.COL_IMAGE_URL, imageData);

            getContentResolver().insert(UsuarioProvider.CONTENT_URI_IMAGENES, imageValues);
        }
    }

    private void cargarImagenesUsuario(int userId) {
        // Limpiar la lista de imágenes antes de cargar las nuevas
        imagesList.clear();

        // Obtener las imágenes del usuario de la base de datos
        Cursor cursor = getContentResolver().query(
                UsuarioProvider.CONTENT_URI_IMAGENES,
                null,
                UsuarioProvider.Imagenes.COL_USER_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null
        );

        // Verificar si hay imágenes almacenadas
        if (cursor != null && cursor.getCount() > 0) {
            // Iterar a través del cursor para obtener las imágenes
            while (cursor.moveToNext()) {
                @SuppressLint("Range") byte[] imageData = cursor.getBlob(cursor.getColumnIndex(UsuarioProvider.Imagenes.COL_IMAGE_URL));
                if (imageData != null) {
                    // Convertir los datos de imagen en un bitmap
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                    // Agregar el bitmap a la lista de imágenes
                    imagesList.add(bitmap);
                }
            }
            cursor.close();
            recyclerAdapterImages.notifyDataSetChanged();
        }
    }
    private void limpiarCampos() {
        tilUser.getEditText().setText("");
        tilMail.getEditText().setText("");
        tilPass.getEditText().setText("");
        etDate.setText("");
        spAcc.setText("");
    }

    private boolean spinnerChecked() {
        String selectedOption = spAcc.getText().toString().trim();

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
                final String selectedDate = day + " / " + (month + 1) + " / " + year;
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

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_SELECT_IMAGE && data != null) {
                Uri selectedImageUri = data.getData();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    // Agregar el bitmap a la lista de imágenes
                    imagesList.add(bitmap);
                    // Actualizar el adaptador del RecyclerView
                    recyclerAdapterImages.notifyDataSetChanged();
                    imageOn = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                Bundle extras = data.getExtras();
                if (extras != null && extras.containsKey("data")) {
                    Bitmap imgBitmap = (Bitmap) extras.get("data");
                    // Agregar el bitmap a la lista de imágenes
                    imagesList.add(imgBitmap);
                    // Actualizar el adaptador del RecyclerView
                    recyclerAdapterImages.notifyDataSetChanged();
                    imageOn = true;
                }
            }
        }
    }

    @Override
    public void onImageClick(final int position) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Mensaje")
                .setMessage("Que desea hacer con la imagen")
                .setNegativeButton("Eliminar la imagen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletePhoto(position);
                    }
                })
                .setPositiveButton("Utilizarla de avatar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        useAsAvatar(position);
                    }
                })
                .setNeutralButton("Cancelar", null)
                .show();
    }

    public void deletePhoto(int position) {
        if (position != RecyclerView.NO_POSITION) {
            Bitmap iconBitmap = ((BitmapDrawable) ivIconImage.getDrawable()).getBitmap();
            Bitmap imageToDeleteBitmap = imagesList.get(position);

            if ((imageToDeleteBitmap != null && imageToDeleteBitmap.sameAs(iconBitmap))) {
                ivIconImage.setImageDrawable(null);
            }
            recyclerAdapterImages.notifyItemRemoved(position);
            imagesList.remove(position);
            Toast.makeText(this, "La imagen ha sido eliminada", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No se ha seleccionado ninguna imagen", Toast.LENGTH_SHORT).show();
        }
    }

    public void useAsAvatar(int position) {
        if (position != RecyclerView.NO_POSITION) {
            ivIconImage.setImageBitmap(imagesList.get(position));
            Toast.makeText(this, "Se ha asignado una imagen como foto de perfil", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No se ha seleccionado ninguna imagen", Toast.LENGTH_SHORT).show();
        }
    }
}


