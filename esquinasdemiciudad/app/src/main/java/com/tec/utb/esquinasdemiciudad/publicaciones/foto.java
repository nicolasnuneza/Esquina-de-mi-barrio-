package com.tec.utb.esquinasdemiciudad.publicaciones;

/**
 * Created by luis mi on 24/10/2016.
 */
//clase para manejar las fotos del servidor
public class foto implements Comparable<foto>{

   String id;

    public String getId_persona() {
        return id_persona;
    }

    public void setId_persona(String id_persona) {
        this.id_persona = id_persona;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
    public foto(){}
    public foto(String id, String imagen, String fecha, String id_persona, String descripcion) {
        this.id = id;
        this.imagen = imagen;
        this.fecha = fecha;
        this.id_persona = id_persona;
        this.descripcion = descripcion;
    }

    String id_persona;
    String descripcion;
    String fecha;
    String imagen;
    @Override
    public int compareTo(foto o) {
        return getFecha().compareTo(o.getFecha());
    }




}
