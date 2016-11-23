package com.tec.utb.esquinasdemiciudad.publicar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.snowdream.android.widget.SmartImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tec.utb.esquinasdemiciudad.MySingleton;
import com.tec.utb.esquinasdemiciudad.R;
import com.tec.utb.esquinasdemiciudad.ajustes;
import com.tec.utb.esquinasdemiciudad.login.login;
import com.tec.utb.esquinasdemiciudad.publicaciones.MainActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class subir_foto extends AppCompatActivity {
    //declaramos variables a utilizar

    private TextView text_mensaje;
    private Button galeria,foto,publicar;
    private SmartImageView imagen;
    TextInputEditText textInputEditText;
    Bitmap myBitmap_img=null;
    String mpath="";

    private final String APP_DIRECTORIO ="fotos/";
    private final String MEDIA_DIRECTORIO =APP_DIRECTORIO+"misfotos/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir_foto);
        String uuid = Settings.Secure.getString(this.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        text_mensaje= (TextView) findViewById(R.id.text_mensaje);
        galeria= (Button) findViewById(R.id.button_galeria);
        foto= (Button) findViewById(R.id.button_foto);
        publicar= (Button) findViewById(R.id.button_publicar);
        imagen= (SmartImageView) findViewById(R.id.imagen);
        textInputEditText= (TextInputEditText) findViewById(R.id.descripcion_text);
        //evento para abrir la galeria

        galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                i.setType("image/*");
                startActivityForResult(i.createChooser(i,"elige una opcion"), 300 );
                text_mensaje.setEnabled(false);
            }
        });
        //evento para abrir la camara

        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            opencamara();
                text_mensaje.setEnabled(false);
            }
        });
        //evento para publicar foto
        publicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    publicar_foto();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    //metodo para convertir un bitmap a base64 ya que el servidor interpreta las imagenes en este formato

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        if (image==null){
            return "";
        }
        else{


            ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
            image.compress(compressFormat, quality, byteArrayOS);
            Log.i("size",byteSizeOf(image)+"");
            return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
        }
    }

String res="";
    //metodo para hacer post al servidor y subir una foto

    String myBase64Image ;
    String des;

    public static int byteSizeOf(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        } else {
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
    }
    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(); return dateFormat.format(date); }
    DatabaseReference root = FirebaseDatabase.getInstance().getReference();

    private DatabaseReference root1 = FirebaseDatabase.getInstance().getReference().child("usuarios");

    //metodo para verificar si ya el dispositivo android esta registrado en el servidor
    private void verificar() {
        final String uuid = Settings.Secure.getString(this.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        root1.child(uuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                } else {
                    Intent intent = new Intent(subir_foto.this, login.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    private void publicar_foto() throws ExecutionException, InterruptedException {


            if(myBitmap_img!=null){
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Publicando...");
                progressDialog.show();

                des = textInputEditText.getText().toString();

                //
                final String myBase64Image = encodeToBase64(myBitmap_img, Bitmap.CompressFormat.JPEG, 100);

                final String fecha = getDateTime();
                MySingleton.getInstance(this.getApplicationContext()).getRequestQueue();
                String url ="https://myservidor.000webhostapp.com/api/subir_fotos.php";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                String uuid = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

                                subirfoto foto = new subirfoto(root.push().getKey(), "" + fecha, des, uuid, fecha + ".jpg");

                                root.child("fotos").child(foto.getFecha()+"-"+foto.getId()).setValue(foto);

                                progressDialog.dismiss();
                                Toast.makeText(subir_foto.this, "Publicacion exitosa", Toast.LENGTH_SHORT).show();
                                finish();

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("error","hay un error");
                        Toast.makeText(subir_foto.this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();

                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<String, String>();
                        params.put("tipo","1");
                        params.put("nombre_imagen",fecha);
                        params.put("imagen",myBase64Image);
                        return params;
                    }
                };
// Add the request to the RequestQueue.
                MySingleton.getInstance(this).addToRequestQueue(stringRequest);
                //
                //
                //

            }
            else {Toast.makeText(subir_foto.this, "Debes cargar alguna imagen para publicarla", Toast.LENGTH_SHORT).show();}




    }


    //metodo para abrir la camara
    private void opencamara() {
        File file=new File(Environment.getExternalStorageDirectory(),MEDIA_DIRECTORIO);
        boolean directorio_exist=file.exists();
        if(!directorio_exist){
            directorio_exist=  file.mkdirs();
        }
        if(directorio_exist){
            long timestamp= System.currentTimeMillis()/1000;
            String nameimg=""+timestamp+".jpg";

            mpath=Environment.getExternalStorageDirectory()+File.separator+MEDIA_DIRECTORIO+File.separator+nameimg;
            File file1=new File(mpath);
            Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file1));
            startActivityForResult(intent,200);
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("file_path",mpath);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mpath=savedInstanceState.getString("file_path");
    }

    public Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            Log.i("imagen normal",width+" - "+height);
            int finalWidth = maxWidth;
            int finalHeight = maxHeight;

            if(width>3500&&width<4500){
                finalWidth = width-((width/2)+(width/3));
                finalHeight=height-((height/2)+(height/3));
            }
            else if(width>2500&&width<=3499){
                finalWidth = width-((width/2)+(width/4));
                finalHeight=height-((height/2)+(height/4));
            }
            else if(width>1700&&width<=2499){
                finalWidth = width-((width/2)+(width/5));
                finalHeight=height-((height/2)+(height/5));
            }
            else if(width>1001&&width<1699){
                finalWidth = width-((width/2)+(width/6));
                finalHeight=height-((height/2)+(height/6));
            }
            else if(width>800 && width<1000){
                finalWidth = width-(width/3);
                finalHeight=height-(height/3);
            }
            else if(width<799){
                finalWidth = width;
                finalHeight=height;
            }

            Log.i("imagen final",finalWidth+" - "+finalHeight);
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;

        } else {
            return image;
        }
    }
    //resultado de la imagen seleccionada o tomada por el usuario
    Uri fotoasubir;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            switch (requestCode){
                case 200:
                    MediaScannerConnection.scanFile(this,new String[]{mpath},null, new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage","Scanned"+path+":");
                            Log.i("ExternalStorage","->Uri= "+uri);
                        }
                    });
                    myBitmap_img=BitmapFactory.decodeFile(mpath);
                    myBitmap_img=resize(myBitmap_img,800,800);
                    Log.i("size",byteSizeOf(myBitmap_img)+"");
                    imagen.setImageBitmap(myBitmap_img);

                    break;
                case 300:
                    Uri uri=data.getData();
                    fotoasubir=uri;
                    try {

                        myBitmap_img= MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        myBitmap_img=resize(myBitmap_img,800,600);
                        Log.i("size",byteSizeOf(myBitmap_img)+"");
                        imagen.setImageBitmap(myBitmap_img);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }
    //permisos para android 6 en adelante
    private boolean myRequesStoragePermission() {

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return true;
        }

        if((checkSelfPermission(WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)&&(checkSelfPermission(CAMERA)==PackageManager.PERMISSION_GRANTED)){
            return true;
        }
        if((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))||(shouldShowRequestPermissionRationale(CAMERA))){
            Toast.makeText(this, "Los permisos son necesarios para poder usar la aplicacion", Toast.LENGTH_SHORT).show();

        }else {    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
        }
        return false;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==100){
            if(grantResults.length==2&&grantResults[0]== PackageManager.PERMISSION_GRANTED&&grantResults[1]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permisos Aceptados", Toast.LENGTH_SHORT).show();
            }else {
                showExplanation();
            }
        }
    }

    private void showExplanation() {
        AlertDialog.Builder builder=new AlertDialog.Builder(subir_foto.this);
        builder.setTitle("Permisos denegados");
        builder.setMessage("Necesitas aceptar los permisos para usas esta app");
        builder.setPositiveButton("aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri=Uri.fromParts("package",getPackageName(),null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.show();
    }

}