package com.example.proyectobeta.Imagen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectobeta.R;

import java.util.ArrayList;

public class RecyclerAdapterImages extends RecyclerView.Adapter<RecyclerAdapterImages.ImageViewHolder> {
    private Context context;
    private ArrayList<Bitmap> imagesList;
    private OnImageClickListener onImageClickListener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public RecyclerAdapterImages(Context context, ArrayList<Bitmap> imagesList) {
        this.context = context;
        this.imagesList = imagesList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.imagenes_usuario_recycled_view, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Bitmap image = imagesList.get(position);
        holder.imageView.setImageBitmap(image);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onImageClickListener != null) {
                    onImageClickListener.onImageClick(position);
                }
            }
        });

        // Cambiar el fondo de la vista si está seleccionada
        holder.itemView.setSelected(selectedPosition == position);
    }

    public void setOnImageClickListener(OnImageClickListener onImageClickListener) {
        this.onImageClickListener = onImageClickListener;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivImagesUser);
        }
    }
}