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
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import com.tec.utb.esquinasdemiciudad.comentarios.comentario;
import com.tec.utb.esquinasdemiciudad.comentarios.comentarios;
import com.tec.utb.esquinasdemiciudad.likes.like;

import java.util.List;

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
        public NetworkImageView imagen;
        public TextView nombre;
        public TextView descripcion;
        public CircleImageView imagen_avatar;
        public TextView fecha;
        public Button comentar;
        public TextView num_comentarios;
        public Button like_boton;

        public ViewHolder(View v) {
            super(v);
            //le pasamos las vistas a los controles creados
            imagen = (NetworkImageView) v.findViewById(R.id.imagen_main);
            nombre = (TextView) v.findViewById(R.id.nombre_avatar);
            descripcion = (TextView) v.findViewById(R.id.descripcion);
            imagen_avatar= (CircleImageView) v.findViewById(R.id.imagen_avatar);
            fecha= (TextView) v.findViewById(R.id.fecha_publicacion);
            comentar= (Button) v.findViewById(R.id.boton_comentar);
            like_boton= (Button) v.findViewById(R.id.boton_like);
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
    final DatabaseReference root = FirebaseDatabase.getInstance().getReference();

    public void boton_color (final Button button, final String id_publicacion, final String id_usuario){
        root.child("likes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                boolean estado=false;
                if(dataSnapshot.exists()){
                    for(DataSnapshot n:dataSnapshot.getChildren()){
                        like like1=n.getValue(like.class);
                        if(like1.getId_publicacion().equals(id_publicacion)&&like1.getId_usuario().equals(id_usuario)){
                            estado=true;

                        }

                    }
                    if(estado==true){
                        Drawable img = context.getResources().getDrawable( R.drawable.ic_favorite_black_24dp );
                        img.setBounds( 0, 0, 50, 50 );
                        button.setCompoundDrawables( img, null, null, null );
                    }else {
                        Drawable img = context.getResources().getDrawable( R.drawable.ic_favorite_border_black_24dp );
                        img.setBounds( 0, 0, 50, 50 );
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
    public void verificar_like(final String id_publicacion, final String id_usuario){


    }
    @Override
    public void onBindViewHolder(final Adapter_Main.ViewHolder holder, final int i) {
        final String uuid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
      holder.nombre.setText("");
        holder.imagen.setImageBitmap(null);
        holder.imagen_avatar.setImageBitmap(null);

        root.child("usuarios").child(items.get(i).getAvatar_nombre()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                holder.nombre.setText(dataSnapshot.child("nombre").getValue().toString());
                holder.imagen_avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showImage(v.getContext(),items.get(i).getAvatar_imagen(),dataSnapshot.child("nombre").getValue().toString());

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
        holder.comentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),comentarios.class);
                intent.putExtra("id",items.get(i).getId());
                v.getContext().startActivity(intent);
            }
        });
        ImageLoader mImageLoader;
        mImageLoader = MySingleton.getInstance(context).getImageLoader();
        holder.imagen.setImageUrl(items.get(i).getImageUrl(),mImageLoader);
        holder.descripcion.setText(String.valueOf(items.get(i).getDescripcion()));
        ImageRequest request = new ImageRequest(items.get(i).getAvatar_imagen(),
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
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        holder.imagen.setMinimumWidth(screenWidth);


        holder.fecha.setText(items.get(i).getFecha());
        holder.imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    showImage(v.getContext(),items.get(i).getImageUrl(),items.get(i).getDescripcion());

            }
        });




    }
    public void showImage(Context context,String bitmap,String des) {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.info_foto, null);
        ImageLoader mImageLoader;
        mImageLoader = MySingleton.getInstance(context).getImageLoader();

        NetworkImageView smartImageView= (NetworkImageView) layout.findViewById(R.id.imagen_info);
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int screenHeight=context.getResources().getDisplayMetrics().heightPixels;
        smartImageView.setMinimumHeight(screenHeight-105);
        smartImageView.setMinimumWidth(screenWidth);
        smartImageView.setImageUrl(bitmap,mImageLoader);
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
