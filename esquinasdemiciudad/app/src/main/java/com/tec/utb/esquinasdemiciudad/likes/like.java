package com.tec.utb.esquinasdemiciudad.likes;

/**
 * Created by luis mi on 22/11/2016.
 */

public class like {
    String id;

    public like(){}
    public like(String id, String id_usuario, String id_publicacion) {
        this.id = id;
        this.id_usuario = id_usuario;
        this.id_publicacion = id_publicacion;
    }

    String id_publicacion;
    String id_usuario;
    public String getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(String id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getId_publicacion() {
        return id_publicacion;
    }

    public void setId_publicacion(String id_publicacion) {
        this.id_publicacion = id_publicacion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
