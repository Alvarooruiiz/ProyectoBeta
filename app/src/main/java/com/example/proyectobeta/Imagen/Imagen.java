package com.example.proyectobeta.Imagen;

public class Imagen {
    private long id;
    private long userId;
    private String imageUrl;

    public Imagen() {
    }

    public Imagen(long userId, String imageUrl) {
        this.userId = userId;
        this.imageUrl = imageUrl;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}