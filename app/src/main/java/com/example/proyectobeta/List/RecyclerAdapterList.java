package com.example.proyectobeta.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectobeta.R;
import com.example.proyectobeta.EditRegis.SharedEditRegister;

import java.util.ArrayList;

public class RecyclerAdapterList extends RecyclerView.Adapter<RecyclerAdapterList.ViewHolder> {

    private ArrayList<Usuario> usuarios;
    private ArrayList<Usuario> usuariosMarcados;
    private Context context;
    private int position;
    private Usuario userLog;

    private OnAvatarClickListener onAvatarClickListener;
    private OnItemCardViewClickListener onItemCardViewClickListener;


    private static final int REQUEST_EDIT_USER = 123;

    public RecyclerAdapterList(Context context, ArrayList<Usuario> usuarios, Usuario userLog, ArrayList<Usuario> usuariosMarcados) {
        this.context = context;
        this.usuarios = usuarios;
        this.userLog = userLog;
        this.usuariosMarcados = usuariosMarcados;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_style_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Usuario userSelected = usuarios.get(position);
        holder.username.setText(userSelected.getUserName());
        holder.correo.setText(userSelected.getUserMail());
        holder.fechaNacimiento.setText(userSelected.getUserBirth());
        if(userSelected.getUserAcc()==1){
            holder.tipoCuenta.setText("Normal");
        }else{
            holder.tipoCuenta.setText("Admin");
        }

        if(userLog.getUserAcc()==0){
            holder.checkBox.setVisibility(View.VISIBLE);
        }else{
            holder.checkBox.setVisibility(View.INVISIBLE);
        }

        byte[] imageData = userSelected.getUserImage();
        if (imageData != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            holder.avatar.setImageBitmap(bitmap);
        } else {
            holder.avatar.setImageResource(R.drawable.user_icon);
        }

        int backgroundColor = userSelected.getUserStatus() == 1 ? Color.parseColor("#FFBABABA") : Color.parseColor("#D8F4B6");
        holder.constraintLayout.setBackgroundColor(backgroundColor);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userLog != null && userLog.getUserAcc() == 0 || (userLog.getUserAcc() == 1 && userSelected.getUserName().equals(userLog.getUserName()))) {
                    Intent intent = new Intent(context, SharedEditRegister.class);
                    intent.putExtra("isRegistering",false);
                    intent.putExtra("userSelected", userSelected);
                    intent.putExtra("userLog",userLog);
                    ((List) context).startActivityForResult(intent, REQUEST_EDIT_USER);
                } else {
                    Toast.makeText(context, "No tienes permiso para editar este perfil", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAvatarClickListener != null) {
                    onAvatarClickListener.onAvatarClick(userSelected);
                }
            }
        });

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                usuarios.get(position).setChecked(isChecked);
                if (isChecked) {
                    usuariosMarcados.add(usuarios.get(position));
                } else {
                    usuariosMarcados.remove(usuarios.get(position));
                }
            }
        });

        if(holder.checkBox.isChecked()){
            userSelected.setChecked(true);
        }else userSelected.setChecked(false);
    }

    public void toggleCheckbox(int position) {
        Usuario usuario = usuarios.get(position);
        usuario.setChecked(!usuario.isChecked());
        notifyItemChanged(position);
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

    public void setOnAvatarClickListener(OnAvatarClickListener listener) {
        this.onAvatarClickListener = listener;
    }

    public void setOnItemCardViewClickListener(OnItemCardViewClickListener listener){
        this.onItemCardViewClickListener = listener;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView avatar;
        TextView username;
        TextView correo;
        TextView fechaNacimiento;
        TextView tipoCuenta;
        CheckBox checkBox;
        ConstraintLayout constraintLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.ivAvatarList);
            username = itemView.findViewById(R.id.tvUserList);
            correo = itemView.findViewById(R.id.tvMailList);
            fechaNacimiento = itemView.findViewById(R.id.tvDateList);
            tipoCuenta = itemView.findViewById(R.id.tvAccList);
            constraintLayout = itemView.findViewById(R.id.constraintColor);
            checkBox = itemView.findViewById(R.id.cbCheck);
        }
    }
}
