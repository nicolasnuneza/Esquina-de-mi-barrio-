package com.tec.utb.esquinasdemiciudad.publicaciones;

import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
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
import com.tec.utb.esquinasdemiciudad.comentarios.comentario;
import com.tec.utb.esquinasdemiciudad.comentarios.comentarios;
import com.tec.utb.esquinasdemiciudad.likes.like;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


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
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
holder.imagen.setMinimumWidth(screenWidth);
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


        try {
            holder.fecha.setText(fecha( items.get(i).getFecha()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    showImage(v.getContext(),items.get(i).getImageUrl(),items.get(i).getDescripcion());

            }
        });




    }
    private final String APP_DIRECTORIO ="fotos/";
    private final String MEDIA_DIRECTORIO =APP_DIRECTORIO+"misfotos/";

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        Date date = new Date(); return dateFormat.format(date); }
    public void showImage(final Context context, String bitmap, String des) {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View layout = layoutInflater.inflate(R.layout.info_foto, null);
        ImageLoader mImageLoader;
        mImageLoader = MySingleton.getInstance(context).getImageLoader();

        final NetworkImageView smartImageView= (NetworkImageView) layout.findViewById(R.id.imagen_info);
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int screenHeight = context.getResources().getDisplayMetrics().heightPixels;
        smartImageView.setMinimumWidth(screenWidth);
        smartImageView.setMinimumHeight(screenHeight-105);
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

    }    String mpath="";

    private String guardarImagen (String nombre, Bitmap imagen) throws IOException {
        File file=new File(Environment.getExternalStorageDirectory(),MEDIA_DIRECTORIO);
        boolean directorio_exist=file.exists();
        if(!directorio_exist){
            directorio_exist=  file.mkdirs();
        }
        if(directorio_exist) {
            long timestamp = System.currentTimeMillis() / 1000;
            String nameimg = "" + nombre + ".jpg";

            mpath = Environment.getExternalStorageDirectory() + File.separator + MEDIA_DIRECTORIO + File.separator;
            File file1 = new File(mpath,nameimg);
            FileOutputStream fOut = null;

            try {
                fOut = new FileOutputStream(file1);

                imagen.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                fOut.flush();
                fOut.close();
                MakeSureFileWasCreatedThenMakeAvabile(file);
                Toast.makeText(context, "Imagen guardada en la galería.", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

            return file.getAbsolutePath();
    }
    private void MakeSureFileWasCreatedThenMakeAvabile(File file){
        MediaScannerConnection.scanFile(context,
                new String[] { file.toString() } , null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    public void onScanCompleted(String path, Uri uri) {
                    }
                });
    }


    private void UnableToSave() {
        Toast.makeText(context, "¡No se ha podido guardar la imagen!", Toast.LENGTH_SHORT).show();
    }

    private void AbleToSave() {
        Toast.makeText(context, "Imagen guardada en la galería.", Toast.LENGTH_SHORT).show();
    }
    private void opciones(final Context context, final String id_publicacion,String fecha) {
        final CharSequence[] option={"Guardar","Cancelar"};
        final AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("Elige una opcion");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:

                        break;

                    default:
                        dialog.dismiss();
                        break;

                }
            }


        });
        builder.show();
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
