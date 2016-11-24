package com.tec.utb.esquinasdemiciudad.perfil;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
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
import com.tec.utb.esquinasdemiciudad.likes.like;
import com.tec.utb.esquinasdemiciudad.login.Usuarios;
import com.tec.utb.esquinasdemiciudad.publicaciones.foto;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by luis mi on 24/10/2016.
 */

public class Adapter_Main extends RecyclerView.Adapter<Adapter_Main.ViewHolder> {

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
        public Button like_boton;
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
            like_boton= (Button) v.findViewById(R.id.boton_like);
            opciones= (Button) v.findViewById(R.id.boton_opciones);
            this.view=v;
        }

    }
//Contructores
   public Adapter_Main(){}
    public Adapter_Main(List<foto> items, Context context) {
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

    public void boton_color (final Button button, final String id_publicacion, final String id_usuario){
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
                        img.setBounds( 0, 0, 45, 45 );
                        button.setCompoundDrawables( img, null, null, null );
                    }else {
                        Drawable img = context.getResources().getDrawable( R.drawable.ic_favorite_border_black_24dp );
                        img.setBounds( 0, 0, 45, 45 );
                        button.setCompoundDrawables( img, null, null, null );
                    }
                }else {
                    Drawable img = context.getResources().getDrawable( R.drawable.ic_favorite_border_black_24dp );
                    img.setBounds( 0, 0, 50, 50 );
                    button.setCompoundDrawables( img, null, null, null );
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


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
                    opciones(v.getContext(),items.get(i).getId(),items.get(i).getFecha(),decodeBase64(items.get(i).getImagen()));
                }
            });
        }else {
            holder.opciones.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    opciones2(v.getContext(),decodeBase64(items.get(i).getImagen()));
                }
            });
        }
      holder.nombre.setText("");
        holder.imagen.setImageBitmap(null);
        holder.imagen_avatar.setImageBitmap(null);
        holder.imagen.setImageBitmap(decodeBase64(items.get(i).getImagen()));

        holder.descripcion.setText(String.valueOf(items.get(i).getDescripcion()));

        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        holder.imagen.setMinimumWidth(screenWidth);


        try {
            holder.fecha.setText(fecha( items.get(i).getFecha()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showImage(v.getContext(),items.get(i).getImagen(),items.get(i).getDescripcion());

            }
        });
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
                 holder.nombre.setText(usuario.getNombre());
                holder.imagen_avatar.setImageBitmap(decodeBase64(usuario.getFoto()));
                holder.imagen_avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showImage(v.getContext(),usuario.getFoto(),usuario.getNombre());


                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
                holder.comentar.setText(" "+num);
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
                holder.like_boton.setText(" "+num);
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
    private void opciones(final Context context, final String id_publicacion, final String fecha, final Bitmap imagen) {

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
                        save.SaveImage(context,imagen);

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
        root.child("fotos").child(fecha+"-"+id_publicacion).removeValue();
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

    private void opciones2(final Context context, final Bitmap imagen) {

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
                        save.SaveImage(context,imagen);
                        break;

                    default:
                        dialog.dismiss();
                        break;

                }
            }


        });
        builder.show();
    }


    public Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
    public void showImage(Context context,String bitmap,String des) {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.info_foto, null);
        ImageLoader mImageLoader;
        mImageLoader = MySingleton.getInstance(context).getImageLoader();

        ImageView smartImageView= (ImageView) layout.findViewById(R.id.imagen_info);
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int screenHeight=context.getResources().getDisplayMetrics().heightPixels;
        smartImageView.setMinimumHeight(screenHeight-((screenHeight/12))+2);
        smartImageView.setMinimumWidth(screenWidth);
        smartImageView.setImageBitmap(decodeBase64(bitmap));
        TextView textView = (TextView) layout.findViewById(R.id.descripcion_info);
        textView.setText(des);
        Dialog dialog=new Dialog(context);
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
