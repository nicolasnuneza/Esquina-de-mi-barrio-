package com.tec.utb.esquinasdemiciudad.comentarios;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tec.utb.esquinasdemiciudad.MySingleton;
import com.tec.utb.esquinasdemiciudad.R;
import com.tec.utb.esquinasdemiciudad.http.http;
import com.tec.utb.esquinasdemiciudad.login.Usuarios;
import com.tec.utb.esquinasdemiciudad.perfil.perfil;
import com.tec.utb.esquinasdemiciudad.publicaciones.Adapter_Main;
import com.tec.utb.esquinasdemiciudad.publicaciones.foto;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
        public View view;

        public ViewHolder(View v) {
            super(v);
            //le pasamos las vistas a los controles creados
            nombre = (TextView) v.findViewById(R.id.nombre_avatar);
            mensaje = (TextView) v.findViewById(R.id.comentario_mensaje);
            imagen_avatar= (CircleImageView) v.findViewById(R.id.imagen_avatar);
            fecha= (TextView) v.findViewById(R.id.comentario_tiempo);
            this.view=v;
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
    public Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
    private int lastPosition = -1;
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
    @Override
    public void onBindViewHolder(final Adapter_comentarios.ViewHolder holder, final int position) {
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();

        holder.nombre.setText("");
        holder.imagen_avatar.setImageBitmap(null);
        holder.nombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),perfil.class);
                intent.putExtra("id_usuario",items.get(position).getId_persona());
                v.getContext().startActivity(intent);
            }
        });
        holder.imagen_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),perfil.class);
                intent.putExtra("id_usuario",items.get(position).getId_persona());
                v.getContext().startActivity(intent);
            }
        });
        root.child("usuarios").child(items.get(position).getId_persona()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuarios usuarios=dataSnapshot.getValue(Usuarios.class);
                    ImageLoader imageLoader= MySingleton.getInstance(context).getImageLoader();

                    imageLoader.get(usuarios.getFoto(), ImageLoader.getImageListener(holder.imagen_avatar,
                            R.drawable.ic_cached_white_24dp, R.drawable.ic_close_black_24dp));


                SpannableString string = new SpannableString(usuarios.getNombre());
                string.setSpan(new UnderlineSpan(), 0, string.length(), 0);
                holder.nombre.setText(string);
                holder.mensaje.setText(items.get(position).getMensaje());
                try {
                    holder.fecha.setText(fecha( items.get(position).getFecha()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


setAnimation(holder.view,position);

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
