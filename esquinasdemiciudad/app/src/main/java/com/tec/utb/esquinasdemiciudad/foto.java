package com.tec.utb.esquinasdemiciudad;

/**
 * Created by luis mi on 24/10/2016.
 */
//clase para manejar las fotos del servidor
public class foto {
    public String getAvatar_nombre() {
        return avatar_nombre;
    }

    public void setAvatar_nombre(String avatar_nombre) {
        this.avatar_nombre = avatar_nombre;
    }

    public String getAvatar_imagen() {
        return avatar_imagen;
    }

    public void setAvatar_imagen(String avatar_imagen) {
        this.avatar_imagen = avatar_imagen;
    }

    String imageUrl;

    public foto(String imageUrl, String fecha, String descripcion, String avatar_imagen, String avatar_nombre) {
        this.imageUrl = imageUrl;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.avatar_imagen = avatar_imagen;
        this.avatar_nombre = avatar_nombre;
    }

    String avatar_nombre;



    String avatar_imagen;

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    String descripcion;
    String fecha;


}
