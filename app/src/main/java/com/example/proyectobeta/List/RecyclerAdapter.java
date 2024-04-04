package com.example.proyectobeta.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectobeta.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<Usuario> usuarios;
    private ArrayList<Usuario> usuariosFiltrados;
    private Context context;
    private int position;
    private Usuario userLog;

    private static final int REQUEST_EDIT_USER = 123;

    public RecyclerAdapter(Context context, ArrayList<Usuario> usuarios, Usuario userLog) {
        this.context = context;
        this.usuarios = usuarios;
        this.usuariosFiltrados = new ArrayList<>(usuarios);
        this.userLog = userLog;
    }

    public void filtrar(String texto) {
        usuariosFiltrados.clear();
        if (texto.isEmpty()) {
            usuariosFiltrados.addAll(usuarios);
        } else {
            texto = texto.toLowerCase();
            for (Usuario usuario : usuarios) {
                if (usuario.getUserName().toLowerCase().contains(texto) ||
                        usuario.getUserMail().toLowerCase().contains(texto) ||
                        usuario.getUserBirth().toLowerCase().contains(texto)) {
                    usuariosFiltrados.add(usuario);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_style_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Usuario userSelected = usuarios.get(position);
        holder.username.setText(userSelected.getUserName());
        holder.correo.setText(userSelected.getUserMail());
        holder.fechaNacimiento.setText(userSelected.getUserBirth());
        holder.tipoCuenta.setText(String.valueOf(userSelected.getUserAcc()));
        byte[] imageData = userSelected.getUserImage();
        if (imageData != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            holder.avatar.setImageBitmap(bitmap);
        } else {
            holder.avatar.setImageResource(R.drawable.user_icon);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userLog != null && userLog.getUserAcc() == 0 || (userLog.getUserAcc() == 1 && userSelected.getUserName().equals(userLog.getUserName()))) {
                    Intent intent = new Intent(context, EditUser.class);
                    intent.putExtra("userSelected", userSelected);
                    intent.putExtra("userLog",userLog);
                    ((List) context).startActivityForResult(intent, REQUEST_EDIT_USER);
                } else {
                    Toast.makeText(context, "No tienes permiso para editar este perfil", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Usuario getItem(int position) {
        if (usuarios != null && position >= 0 && position < usuarios.size()) {
            return usuarios.get(position);
        }
        return null;
    }



    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView avatar;
        TextView username;
        TextView correo;
        TextView fechaNacimiento;
        TextView tipoCuenta;

        public ViewHolder(View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.ivAvatarList);
            username = itemView.findViewById(R.id.tvUserList);
            correo = itemView.findViewById(R.id.tvMailList);
            fechaNacimiento = itemView.findViewById(R.id.tvDateList);
            tipoCuenta = itemView.findViewById(R.id.tvAccList);
        }
    }
}
