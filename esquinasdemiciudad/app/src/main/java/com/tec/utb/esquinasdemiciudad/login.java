package com.tec.utb.esquinasdemiciudad;

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
import android.os.Handler;
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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.snowdream.android.widget.SmartImageView;
import com.tec.utb.esquinasdemiciudad.http.http;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class login extends AppCompatActivity {

    TextInputEditText nombre;
    Button iniciar,foto,galeria;
    private SmartImageView imagen;
    Bitmap myBitmap_img=null;
    String mpath="";
    String res="";

    private final String APP_DIRECTORIO ="fotos/";
    private final String MEDIA_DIRECTORIO =APP_DIRECTORIO+"misfotos/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        String uuid = Settings.Secure.getString(this.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        nombre= (TextInputEditText) findViewById(R.id.edittext_nombre);
        iniciar= (Button) findViewById(R.id.boton_iniciar);
        foto= (Button) findViewById(R.id.boton_foto);
        galeria= (Button) findViewById(R.id.boton_galeria);
        imagen= (SmartImageView) findViewById(R.id.imagen);
        iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    registrar();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opencamara();
            }
        });
        galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                i.setType("image/*");
                startActivityForResult(i.createChooser(i,"elige una opcion"), 300 );
            }
        });
    }



    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        if (image==null){
            return "";
        }
        else{
            ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
            image.compress(compressFormat, quality, byteArrayOS);
            return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
        }
    }

        private void registrar() throws ExecutionException, InterruptedException {

            final String myBase64Image = encodeToBase64(myBitmap_img, Bitmap.CompressFormat.JPEG, 100);
            String name=nombre.getText().toString();
            final String uuid = Settings.Secure.getString(this.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

            String [] params={"tipo_query","2","id",uuid,"nombre",name,"foto",myBase64Image};
            String res=http.Post("http://comidasutb.gzpot.com/esquina/api/usuarios.php",params);
            if(res.length()>2) {
                Toast.makeText(login.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                finish();
            }
            else {
                Toast.makeText(login.this, "Ha ocurrido algun error", Toast.LENGTH_SHORT).show();
            }

          /*  RequestQueue queue = MySingleton.getInstance(this.getApplicationContext()).
                    getRequestQueue();
            String url ="http://comidasutb.gzpot.com/esquina/api/fotos.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.
                            Log.i("JSON",response);
                            if(response.length()>2) {
                                Toast.makeText(login.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else {
                                Toast.makeText(login.this, "Ha ocurrido algun error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("error","hay un error");
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("tipo_query","2");
                    params.put("id",uuid);
                    params.put("nombre",nombre.getText().toString());
                    params.put("imagen",myBase64Image);
                    return params;
                }
            };
// Add the request to the RequestQueue.
            MySingleton.getInstance(this).addToRequestQueue(stringRequest);*/

        }
    public Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            Log.i("imagen normal",width+" - "+height);
            int finalWidth = maxWidth;
            int finalHeight = maxHeight;


            if(width>maxWidth){
                 finalWidth = maxWidth;
            }
            else {
                finalWidth = width;
            }
            if(height>maxHeight){
                finalHeight=maxHeight;
            }else {finalHeight=height;}
            Log.i("imagen final",finalWidth+" - "+finalHeight);
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;

        } else {
            return image;
        }
    }
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
                    myBitmap_img= BitmapFactory.decodeFile(mpath);
                    myBitmap_img=resize(myBitmap_img,640,440);
                    imagen.setImageBitmap(myBitmap_img);

                    break;
                case 300:
                    Uri uri=data.getData();
                    try {
                        myBitmap_img= MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        myBitmap_img=resize(myBitmap_img,640,440);
                        imagen.setImageBitmap(myBitmap_img);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }
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



    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    private void showExplanation() {
        AlertDialog.Builder builder=new AlertDialog.Builder(login.this);
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
