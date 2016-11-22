package com.tec.utb.esquinasdemiciudad.comentarios;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tec.utb.esquinasdemiciudad.MySingleton;
import com.tec.utb.esquinasdemiciudad.R;
import com.tec.utb.esquinasdemiciudad.login.Usuarios;
import com.tec.utb.esquinasdemiciudad.publicaciones.Adapter_Main;
import com.tec.utb.esquinasdemiciudad.publicaciones.foto;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by luis mi on 22/11/2016.
 */

public class Adapter_comentarios extends RecyclerView.Adapter<Adapter_comentarios.ViewHolder> {

    public Adapter_comentarios(){}
    public Adapter_comentarios(List<comentario> items,Context context) {
        this.items=items;
        this.context = context;
    }
    List<comentario> items;
    Context context;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Campos respectivos de un item que se encuentran en cardview.xml
        public TextView nombre;
        public TextView mensaje;
        public CircleImageView imagen_avatar;
        public TextView fecha;

        public ViewHolder(View v) {
            super(v);
            //le pasamos las vistas a los controles creados
            nombre = (TextView) v.findViewById(R.id.nombre_avatar);
            mensaje = (TextView) v.findViewById(R.id.comentario_mensaje);
            imagen_avatar= (CircleImageView) v.findViewById(R.id.imagen_avatar);
            fecha= (TextView) v.findViewById(R.id.comentario_tiempo);

        }

    }
    @Override
    public Adapter_comentarios.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comentario, parent, false);
        return new ViewHolder(v);
    }
    public String fecha(String d) throws ParseException {
        String[] mese={"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = formatter.parse(d);
        Date date = new Date();
        dateFormat.format(date);
        long dias = date.getTime() - date1.getTime();



        if ((dias /(1000* 60)) < 1) {
            return "Ahora";
        } else if ((dias / (1000*60)) < 60 && (dias / (1000*60)) >=1) {
            return  (dias / (1000*60)) + "min";
        } else if ((dias / (1000*60*60) ) >= 1 &&(dias / (1000*60*60) ) < 24 ) {
            return (dias /(1000* 60 * 60)) + "h";
        }
        else if((dias / (1000*60*60*24)) >= 1&&(dias / (1000*60*60*24)) < 7){
            return (dias /(1000* 60 * 60*24)) + "d";
        }
        else if((dias / (1000*60*60*24*7)) >= 1){
            return (dias / (1000*60*60*24*7))+"sem";
        }
        else {return d;}


    }
    @Override
    public void onBindViewHolder(final Adapter_comentarios.ViewHolder holder, int position) {
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        root.child("usuarios").child(items.get(position).getId_persona()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuarios usuarios=dataSnapshot.getValue(Usuarios.class);
                holder.nombre.setText(usuarios.getNombre());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        holder.mensaje.setText(items.get(position).getMensaje());
        try {
            holder.fecha.setText(fecha( items.get(position).getFecha()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ImageRequest request = new ImageRequest("https://myservidor.000webhostapp.com/fotos_usuarios/"+items.get(position).getId_persona()+".jpg",
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        holder.imagen_avatar.setImageBitmap(bitmap);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                    }
                });
// Access the RequestQueue through your singleton class.
        request.setShouldCache(false);

        MySingleton.getInstance(context).addToRequestQueue(request);

    }
    public void wap(List<comentario> list){
        if (items != null) {
            items.clear();
            items.addAll(list);
        }
        else {
            items = list;
        }
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return items.size();
    }
}
