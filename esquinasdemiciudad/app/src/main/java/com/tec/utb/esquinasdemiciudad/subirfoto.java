package com.tec.utb.esquinasdemiciudad;

/**
 * Created by luis mi on 18/11/2016.
 */

public class subirfoto {
    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getId_persona() {
        return id_persona;
    }

    public void setId_persona(String id_persona) {
        this.id_persona = id_persona;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public subirfoto(String id, String fecha, String descripcion, String id_persona, String imagen) {
        this.id = id;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.id_persona = id_persona;
        this.imagen = imagen;
    }

    private String id;
    private String imagen;
    private String id_persona;
    private String descripcion;
    private String fecha;
}
