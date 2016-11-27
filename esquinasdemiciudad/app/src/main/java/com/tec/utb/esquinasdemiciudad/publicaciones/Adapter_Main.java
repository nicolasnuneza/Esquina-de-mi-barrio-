package com.tec.utb.esquinasdemiciudad.publicaciones;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.tec.utb.esquinasdemiciudad.Save;
import com.tec.utb.esquinasdemiciudad.comentarios.comentario;
import com.tec.utb.esquinasdemiciudad.comentarios.comentarios;
import com.tec.utb.esquinasdemiciudad.http.http;
import com.tec.utb.esquinasdemiciudad.likes.Adapter_megustas;
import com.tec.utb.esquinasdemiciudad.likes.like;
import com.tec.utb.esquinasdemiciudad.likes.megustas;
import com.tec.utb.esquinasdemiciudad.login.Usuarios;
import com.tec.utb.esquinasdemiciudad.perfil.perfil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by luis mi on 24/10/2016.
 */

public class Adapter_Main  extends RecyclerView.Adapter<Adapter_Main.ViewHolder> {

    private List<foto> items;//lista de las publicaciones
    public Context context;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Campos respectivos de un item que se encuentran en cardview.xml
        public ImageView imagen;
        public TextView nombre;
        public TextView descripcion;
        public CircleImageView imagen_avatar;
        public TextView fecha;
        public Button comentar;
        public TextView num_comentarios;
        public Button opciones;
        public ImageButton like_boton;
        public TextView like_num;
        public View view;

        public ViewHolder(View v) {
            super(v);
            //le pasamos las vistas a los controles creados
            imagen = (ImageView) v.findViewById(R.id.imagen_main);
            nombre = (TextView) v.findViewById(R.id.nombre_avatar);
            descripcion = (TextView) v.findViewById(R.id.descripcion);
            imagen_avatar= (CircleImageView) v.findViewById(R.id.imagen_avatar);
            fecha= (TextView) v.findViewById(R.id.fecha_publicacion);
            comentar= (Button) v.findViewById(R.id.boton_comentar);
            like_boton= (ImageButton) v.findViewById(R.id.boton_like);
            like_num= (TextView) v.findViewById(R.id.textView_like);
            opciones= (Button) v.findViewById(R.id.boton_opciones);
            this.view=v;
        }

    }
//Contructores
   public  Adapter_Main(){}
    public Adapter_Main(List<foto> items,Context context) {
        this.items = items;
        this.context=context;

    }
    @Override
    public Adapter_Main.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview, parent, false);
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

        long dias_ = dias / (1000 * 60 * 60 * 24);
        Log.i("dias",""+dias_);
        Log.i("segundos",""+dias/1000);
        Log.i("minutos",""+(dias / (1000 * 60)));
        Log.i("horas",""+(dias / (1000 * 60*60)));

        if ((dias /(1000* 60)) < 1) {
            return "hace unos segundos";
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
    final DatabaseReference root = FirebaseDatabase.getInstance().getReference();

    public void boton_color (final ImageButton button, final String id_publicacion, final String id_usuario){
        root.child("likes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                boolean estado=false;
                if(dataSnapshot.getValue()!=null){
                    for(DataSnapshot n:dataSnapshot.getChildren()){
                        like like1=n.getValue(like.class);
                        if(like1.getId_publicacion().equals(id_publicacion)&&like1.getId_usuario().equals(id_usuario)){
                            estado=true;

                        }

                    }
                    if(estado==true){
                        Drawable img = context.getResources().getDrawable( R.drawable.ic_favorite_black_24dp );
                        button.setImageDrawable(img);
                    }else {
                        Drawable img = context.getResources().getDrawable( R.drawable.ic_favorite_border_black_24dp );
                        button.setImageDrawable(img);
                    }
                }else {
                    Drawable img = context.getResources().getDrawable( R.drawable.ic_favorite_border_black_24dp );
                    button.setImageDrawable(img);

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    public void boton_color2 (final ImageButton button, final String id_publicacion, final String id_usuario){
        root.child("likes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                boolean estado=false;
                if(dataSnapshot.getValue()!=null){
                    for(DataSnapshot n:dataSnapshot.getChildren()){
                        like like1=n.getValue(like.class);
                        if(like1.getId_publicacion().equals(id_publicacion)&&like1.getId_usuario().equals(id_usuario)){
                            estado=true;

                        }

                    }
                    if(estado==true){
                        Drawable img = context.getResources().getDrawable( R.drawable.ic_favorite_white_24dp );
                        button.setImageDrawable(img);
                    }else {
                        Drawable img = context.getResources().getDrawable( R.drawable.ic_favorite_border_white_24dp );
                        button.setImageDrawable(img);
                    }
                }else {
                    Drawable img = context.getResources().getDrawable( R.drawable.ic_favorite_border_white_24dp );
                    button.setImageDrawable(img);

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    public void verificar_like(final String id_publicacion, final String id_usuario){


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
    public void onBindViewHolder(final Adapter_Main.ViewHolder holder, final int i) {
        final String uuid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if(uuid.equals(items.get(i).getId_persona())){
            holder.opciones.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   opciones(v.getContext(),items.get(i).getId(),items.get(i).getFecha(),items.get(i).getImagen());
                }
            });
        }else {
            holder.opciones.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    opciones2(v.getContext(),items.get(i).getImagen());
                }
            });
        }
      holder.nombre.setText("");
        holder.imagen.setImageBitmap(null);
        holder.imagen_avatar.setImageBitmap(null);
        ImageLoader imageLoader= MySingleton.getInstance(context).getImageLoader();
// Petición
        imageLoader.get(items.get(i).getImagen(), ImageLoader.getImageListener(holder.imagen,
                R.drawable.ic_cached_black_24dp, R.drawable.ic_close_white_256dp_1x));

        holder.descripcion.setText(String.valueOf(items.get(i).getDescripcion()));

        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        holder.imagen.setMinimumWidth(screenWidth);


        try {
            holder.fecha.setText(fecha( items.get(i).getFecha()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.comentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),comentarios.class);
                intent.putExtra("id",items.get(i).getId());
                v.getContext().startActivity(intent);
            }
        });
        root.child("usuarios").child(items.get(i).getId_persona()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                final Usuarios usuario=dataSnapshot.getValue(Usuarios.class);
                ImageLoader imageLoader= MySingleton.getInstance(context).getImageLoader();

                imageLoader.get(usuario.getFoto(), ImageLoader.getImageListener(holder.imagen_avatar,
                        R.drawable.ic_cached_white_24dp, R.drawable.ic_close_black_24dp));
                holder.imagen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        showImage(v.getContext(),items.get(i).getImagen(),items.get(i).getDescripcion(),items.get(i).getId(),items.get(i).getFecha(),items.get(i).getId_persona(),usuario.getNombre());

                    }
                });
                SpannableString string = new SpannableString(usuario.getNombre());
                string.setSpan(new UnderlineSpan(), 0, string.length(), 0);
                holder.nombre.setText(string);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        holder.nombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),perfil.class);
                intent.putExtra("id_usuario",items.get(i).getId_persona());
                v.getContext().startActivity(intent);
            }
        });
        root.child("comentarios").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int num=0;

                for(DataSnapshot n : dataSnapshot.getChildren()){
                    comentario comentario1=n.getValue(comentario.class);
                    if(comentario1.getId_publicacion().equals(items.get(i).getId())){
                        num=num+1;
                    }
                }
                if (num<=0){
                    holder.comentar.setText("");
                }else {
                holder.comentar.setText(" "+num);}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        root.child("likes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            int num=0;

                if(dataSnapshot.exists()){
                boton_color(holder.like_boton,items.get(i).getId(),uuid);
                for(DataSnapshot n : dataSnapshot.getChildren()){
                    like like=n.getValue(like.class);
                    if(like.getId_publicacion().equals(items.get(i).getId())){
                        num=num+1;
                    }
                }
                    if (num<=0){
                        holder.like_num.setText("");
                    }
                    else {
                        holder.like_num.setText(" " + num);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        holder.like_boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("verificar",items.get(i).getId()+" "+uuid);
                root.child("likes").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean estado=false;
                        String id="";
                            for(DataSnapshot n:dataSnapshot.getChildren()){
                                like like1=n.getValue(like.class);
                                if(like1.getId_publicacion().equals(items.get(i).getId())&&like1.getId_usuario().equals(uuid)){
                                    id=like1.getId();
                                    estado=true;


                                }


                            }
                            if (estado==true){ root.child("likes").child(id).removeValue();}
                            else {
                                String key=root.child("like").push().getKey();
                                like like2=new like(key,uuid,items.get(i).getId());
                                root.child("likes").child(key).setValue(like2);
                            }



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });
        setAnimation(holder.view,i);

    }
    private void opciones(final Context context, final String id_publicacion, final String fecha, final String imagen) {

        final CharSequence[] option={"Eliminar","Guardar","Cancelar"};
        final AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("Elige una opcion");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        eliminar_publicacion(id_publicacion,fecha);
                        break;
                    case 1:
                        Save save=new Save();
                        try {
                            save.SaveImage(context,http.Download_Image(imagen));
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        break;
                    default:
                        dialog.dismiss();
                        break;

                }
            }


        });
        builder.show();
    }

    private void eliminar_publicacion(final String id_publicacion, String fecha) {
        root.child("pubicaciones").child(fecha+"-"+id_publicacion).removeValue();
        root.child("likes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    like like1=dataSnapshot1.getValue(like.class);
                    if(like1.getId_publicacion().equals(id_publicacion)){
                        root.child("likes").child(like1.getId()).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        root.child("comentarios").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    comentario com=dataSnapshot1.getValue(comentario.class);
                    if(com.getId_publicacion().equals(id_publicacion)){
                        root.child("comentarios").child(com.getId()).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void opciones2(final Context context, final String imagen) {

        final CharSequence[] option={"Guardar","Cancelar"};
        final AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("Que deseas hacer");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                            //Toast.makeText(context,saveImage(context,imagen).getAbsolutePath(), Toast.LENGTH_SHORT).show();
                        Save save=new Save();
                        try {
                            save.SaveImage(context,http.Download_Image(imagen));
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        break;

                    default:
                        dialog.dismiss();
                        break;

                }
            }


        });
        builder.show();
    }




    public AlphaAnimation Fadein(){
        AlphaAnimation alphaAnimation=new AlphaAnimation(0.0f,1.0f);
        alphaAnimation.setDuration(500);
        alphaAnimation.setFillAfter(true);
return alphaAnimation;
    }
    public AlphaAnimation Fadeout(){

        AlphaAnimation alphaAnimation=new AlphaAnimation(1.0f,0.0f);
        alphaAnimation.setDuration(500);
        alphaAnimation.setFillAfter(true);
        return alphaAnimation;
    }
    public void showImage(Context context, final String bitmap, String des, final String id, final String fecha,String id_persona,String nombre) {
        Log.i("id_publicacion",id);
        final String uuid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.info_foto, null);
       ImageView smartImageView= (ImageView) layout.findViewById(R.id.imagen_info);
        final LinearLayout linearLayout= (LinearLayout) layout.findViewById(R.id.contenedor_texto);
        final ImageButton megusta= (ImageButton) layout.findViewById(R.id.boton_like);
        final ImageButton cerrar= (ImageButton) layout.findViewById(R.id.boton_cerrar);
        final ImageButton opciones= (ImageButton) layout.findViewById(R.id.boton_opciones);
        final TextView textView= (TextView) layout.findViewById(R.id.textview_nombre_usuario);
        textView.setText(nombre);
        RelativeLayout relativeLayout= (RelativeLayout) layout.findViewById(R.id.relative);
        ImageButton comentarios= (ImageButton) layout.findViewById(R.id.boton_comentar);
        final TextView num_megusta= (TextView) layout.findViewById(R.id.textview_megusta);
        final TextView num_comen= (TextView) layout.findViewById(R.id.textView_comentarios);
        opciones.startAnimation(Fadein());
        linearLayout.startAnimation(Fadein());
        cerrar.startAnimation(Fadein());
        textView.setAnimation(Fadein());
       if(uuid.equals(id_persona)){
            opciones.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    opciones(v.getContext(),id,fecha,bitmap);
                }
            });
        }else {
            opciones.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    opciones2(v.getContext(),bitmap);
                }
            });
        }

        num_megusta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),megustas.class);
                intent.putExtra("id",id);
                v.getContext().startActivity(intent);
            }
        });
        smartImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(linearLayout.getVisibility()==View.VISIBLE){
                    linearLayout.startAnimation(Fadeout());
                    cerrar.startAnimation(Fadeout());
                    opciones.startAnimation(Fadeout());
                    textView.startAnimation(Fadeout());
                   linearLayout.setVisibility(View.INVISIBLE);
                    cerrar.setVisibility(View.INVISIBLE);
                    opciones.setVisibility(View.INVISIBLE);
                    textView.setVisibility(View.INVISIBLE);
                }else {

                    linearLayout.startAnimation(Fadein());
                    cerrar.startAnimation(Fadein());
                    opciones.startAnimation(Fadein());
                    textView.startAnimation(Fadein());

                    linearLayout.setVisibility(View.VISIBLE);
                    cerrar.setVisibility(View.VISIBLE);
                    opciones.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.VISIBLE);

                }
            }
        });
        num_comen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),comentarios.class);
                intent.putExtra("id",id);
                v.getContext().startActivity(intent);
            }
        });

        comentarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),comentarios.class);
                intent.putExtra("id",id);
                v.getContext().startActivity(intent);
            }
        });
        megusta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                root.child("likes").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean estado=false;
                        String id1="";
                        for(DataSnapshot n:dataSnapshot.getChildren()){
                            like like1=n.getValue(like.class);
                            if(like1.getId_publicacion().equals(id)&&like1.getId_usuario().equals(uuid)){
                                id1=like1.getId();
                                estado=true;


                            }


                        }
                        if (estado==true){ root.child("likes").child(id1).removeValue();}
                        else {
                            String key=root.child("like").push().getKey();
                            like like2=new like(key,uuid,id);
                            root.child("likes").child(key).setValue(like2);
                        }



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int screenHeight=context.getResources().getDisplayMetrics().heightPixels;
        smartImageView.getLayoutParams().height=screenHeight-((screenHeight/26));
        //smartImageView.setMinimumHeight(screenHeight-(screenHeight/25)+2);
        smartImageView.setMinimumWidth(screenWidth);
        relativeLayout.getLayoutParams().width=screenWidth;

        ImageLoader imageLoader= MySingleton.getInstance(context).getImageLoader();

        imageLoader.get(bitmap, ImageLoader.getImageListener(smartImageView,
                R.drawable.ic_cached_black_24dp, R.drawable.ic_close_white_24dp));

        TextView textView1 = (TextView) layout.findViewById(R.id.descripcion_info);
        textView1.setText(des);
        textView1.setMovementMethod(new ScrollingMovementMethod());

        root.child("comentarios").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int num=0;

                for(DataSnapshot n : dataSnapshot.getChildren()){
                    comentario comentario1=n.getValue(comentario.class);
                    if(comentario1.getId_publicacion().equals(id)){
                        num=num+1;
                    }
                }
                if (num<=0){
                    num_comen.setText("0 Comentarios");
                }else {
                    num_comen.setText(num+" Comentarios");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        root.child("likes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int num=0;

                if(dataSnapshot.exists()){
                    boton_color2(megusta,id,uuid);
                    for(DataSnapshot n : dataSnapshot.getChildren()){
                        like like=n.getValue(like.class);
                        if(like.getId_publicacion().equals(id)){
                            num=num+1;
                        }
                    }
                    if (num<=0){
                        num_megusta.setText("0 Me gusta");
                    }
                    else {
                        num_megusta.setText(num+" Me gusta");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
       final Dialog dialog=new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        dialog.onBackPressed();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //nothing;
            }
        });
        dialog.setContentView(layout);

        dialog.show();

      cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    public void wap(List<foto> list){
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
