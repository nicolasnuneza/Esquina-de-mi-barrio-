package com.tec.utb.esquinasdemiciudad.comentarios;

/**
 * Created by luis mi on 22/11/2016.
 */

public class comentario {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getId_publicacion() {
        return id_publicacion;
    }

    public void setId_publicacion(String id_publicacion) {
        this.id_publicacion = id_publicacion;
    }

    public String getId_persona() {
        return id_persona;
    }

    public void setId_persona(String id_persona) {
        this.id_persona = id_persona;
    }

    String id;
    String id_persona;
    String id_publicacion;

    public comentario(){}
    public comentario(String id, String mensaje, String id_publicacion, String id_persona, String fecha) {
        this.id = id;
        this.mensaje = mensaje;
        this.id_publicacion = id_publicacion;
        this.id_persona = id_persona;
        this.fecha = fecha;
    }

    String mensaje;
    String fecha;
}
