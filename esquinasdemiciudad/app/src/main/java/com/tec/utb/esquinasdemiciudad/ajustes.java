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
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tec.utb.esquinasdemiciudad.http.http;
import com.tec.utb.esquinasdemiciudad.login.Usuarios;
import com.tec.utb.esquinasdemiciudad.login.login;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ajustes extends AppCompatActivity {
    //declaramos variables a utilizar
    private Button galeria,foto,guardar;
    private EditText editText;
    private CircleImageView imagen,profile;
    Bitmap myBitmap_img=null;
    String mpath="";

    //direcctorio donde se guardaran las fotos tomadas por la app
    private final String APP_DIRECTORIO ="fotos/";
    private final String MEDIA_DIRECTORIO =APP_DIRECTORIO+"misfotos/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);
        //obtenemos las vistas
        galeria= (Button) findViewById(R.id.button_galeria_);
        foto= (Button) findViewById(R.id.button_foto_);
        guardar= (Button) findViewById(R.id.button_guardar_);
        editText= (EditText) findViewById(R.id.edittext_nombre);
        imagen= (CircleImageView) findViewById(R.id.circleImageView_cambiarfoto);
        profile= (CircleImageView) findViewById(R.id.profile_image);
        //cargamos datos del usuario
        try {
            cargar_datos();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //evento para abrir la galeria
        galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                i.setType("image/*");
                startActivityForResult(i.createChooser(i,"elige una opcion"), 300 );
            }
        });
        //evento para abrir la camara
        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opencamara();
            }
        });
        //evento para actualizar los datos del usuario
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    guardar();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
    //en este metodo cargamos los datos del usuario
    private void cargar_datos() throws ExecutionException, InterruptedException, JSONException, UnsupportedEncodingException {

        DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("usuarios");
        final String uuid = Settings.Secure.getString(this.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        root.child(uuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuarios usuario=dataSnapshot.getValue(Usuarios.class);
                editText.setText(usuario.getNombre());
                profile.setImageBitmap(decodeBase64(usuario.getFoto()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
            return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
        }
    }

    String res="";
    //metodo para hacer post al servidor y actualizar los datos
    private void guardar() throws ExecutionException, InterruptedException {

        if(myBitmap_img!=null){
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Creando usuario...");
            progressDialog.show();
            final String name = editText.getText().toString();
            final String uuid = Settings.Secure.getString(this.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            final DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("usuarios");
            final String myBase64Image = encodeToBase64(myBitmap_img, Bitmap.CompressFormat.JPEG, 100);


                 Usuarios usuarios = new Usuarios(name, myBase64Image, uuid);
                            root.child(usuarios.getId()).setValue(usuarios);
                            progressDialog.dismiss();
                            Toast.makeText(ajustes.this, "Actualizacion exitosa", Toast.LENGTH_SHORT).show();



                        }

        else {
            DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("usuarios");
            String uuid = Settings.Secure.getString(this.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            String name = editText.getText().toString();

            root.child(uuid).child("nombre").setValue(name);
            Toast.makeText(this, "Nombre actualizado", Toast.LENGTH_SHORT).show();
        }

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
                    myBitmap_img=resize(myBitmap_img,800,800);
                    imagen.setImageBitmap(myBitmap_img);

                    break;
                case 300:
                    Uri uri=data.getData();
                    try {
                        myBitmap_img= MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        myBitmap_img=resize(myBitmap_img,800,800);
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
        AlertDialog.Builder builder=new AlertDialog.Builder(ajustes.this);
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
