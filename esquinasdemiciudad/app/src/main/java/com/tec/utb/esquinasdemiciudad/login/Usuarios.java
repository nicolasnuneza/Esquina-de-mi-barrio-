package com.tec.utb.esquinasdemiciudad.login;

import android.graphics.Bitmap;

/**
 * Created by luis mi on 19/11/2016.
 */

public class Usuarios {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    private String id;
    private String foto;
    private String nombre;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    Bitmap  bitmap;

    public Usuarios(){}
    public Usuarios(String nombre, String foto, String id) {
        this.nombre = nombre;
        this.foto = foto;
        this.id = id;
    }



}
