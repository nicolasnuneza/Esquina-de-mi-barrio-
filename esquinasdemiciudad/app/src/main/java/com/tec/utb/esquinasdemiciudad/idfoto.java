package com.tec.utb.esquinasdemiciudad;

/**
 * Created by luis mi on 25/11/2016.
 */

public class idfoto {
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
    public idfoto(){}
    public idfoto(String id_usuario, String id_publicacion) {
        this.id_usuario = id_usuario;
        this.id_publicacion = id_publicacion;
    }

    String id_usuario;
    String id_publicacion;
}
