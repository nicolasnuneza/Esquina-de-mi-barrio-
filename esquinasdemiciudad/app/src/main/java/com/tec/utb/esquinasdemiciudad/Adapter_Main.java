package com.tec.utb.esquinasdemiciudad;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.github.snowdream.android.widget.SmartImageView;
import com.tec.utb.esquinasdemiciudad.http.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
        public NetworkImageView imagen;
        public TextView nombre;
        public TextView descripcion;
        public CircleImageView imagen_avatar;
        public TextView fecha;

        public ViewHolder(View v) {
            super(v);
            //le pasamos las vistas a los controles creados
            imagen = (NetworkImageView) v.findViewById(R.id.imagen_main);
            nombre = (TextView) v.findViewById(R.id.nombre_avatar);
            descripcion = (TextView) v.findViewById(R.id.descripcion);
            imagen_avatar= (CircleImageView) v.findViewById(R.id.imagen_avatar);
            fecha= (TextView) v.findViewById(R.id.fecha_publicacion);
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
    public Bitmap decode(String base64){
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }
    @Override
    public void onBindViewHolder(final Adapter_Main.ViewHolder holder, final int i) {
        Rect rect=new Rect(holder.imagen.getLeft(),holder.imagen.getTop(),holder.imagen.getRight(),holder.imagen.getBottom());
        Rect rect1=new Rect(holder.imagen_avatar.getLeft(),holder.imagen_avatar.getTop(),holder.imagen_avatar.getRight(),holder.imagen_avatar.getBottom());
        ImageLoader mImageLoader;
        mImageLoader = MySingleton.getInstance(context).getImageLoader();
        holder.imagen.setImageUrl(items.get(i).getImageUrl(),mImageLoader);
        holder.nombre.setText(items.get(i).getAvatar_nombre());
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



        holder.fecha.setText(items.get(i).getFecha());
        holder.imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    showImage(v.getContext(),items.get(i).getImageUrl(),items.get(i).getDescripcion());

            }
        });
        holder.imagen_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                    showImage(v.getContext(),items.get(i).getAvatar_imagen(),items.get(i).getAvatar_nombre());

            }
        });



    }
    public void showImage(Context context,String bitmap,String des) {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.info_foto, null);
        ImageLoader mImageLoader;
        mImageLoader = MySingleton.getInstance(context).getImageLoader();

        NetworkImageView smartImageView= (NetworkImageView) layout.findViewById(R.id.imagen_info);
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
