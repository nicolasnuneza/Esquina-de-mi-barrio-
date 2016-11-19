package com.tec.utb.esquinasdemiciudad;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tec.utb.esquinasdemiciudad.http.http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    //declaramos variables para el recyclerview
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static int firstVisibleInListview;
    boolean todo = false;//sirve para determinar que actualizacion hacer
    String res = "";
    final ArrayList<foto> items = new ArrayList();//array que contendra las publicaciones para despues mostrarlas en el recyclerveiw
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //optenemos el id unico del telefono
        String uuid = Settings.Secure.getString(this.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        try {
            //metodo para verivicar si ya estamos registrado en la app
            verificar();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, subir_foto.class);
                startActivity(intent);
            }
        });
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.s1,
                R.color.s2,
                R.color.s3,
                R.color.s4
        );
        //sirve para dar scroll arriba y actualizar dependiendo el tipo de actualizacion que se desea
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mostrar();

                swipeRefreshLayout.setRefreshing(false);
            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_main);

        mRecyclerView.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        // Crear un nuevo adaptador
        mAdapter = new Adapter_Main(items, getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);


        //cargar las publicaciones
        mostrar();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    String nombre = "";
    String avatar = "";
    String id = "";
    String urlimagen = "";
    String urlavatar = "";
    DatabaseReference root = FirebaseDatabase.getInstance().getReference();

    //metodo para obtener y cargar las publicaciones desde el servidor
    public String fecha(String d) throws ParseException {
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

        if (dias_ < 1) {
            if ((dias /(1000* 60)) < 3) {
                return "hace un momento";
            } else if ((dias / (1000*60)) < 60 && (dias / (1000*60)) >= 3) {
                return "hace " + (dias / (1000*60)) + " min";
            } else if ((dias / (1000*60*60) ) >= 1) {
                return "hace " + (dias /(1000* 60 * 60)) + " h";
            }
            else {return d;}
        } else {
            return date1.getDay() + " del " + date1.getMonth() + "a las: " + date1.getHours() + ":" + date1.getMinutes();
        }


    }

    private void mostrar() {
        root.child("fotos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                items.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String imagen = postSnapshot.child("imagen").getValue(String.class).toString();
                    id = postSnapshot.child("id_persona").getValue(String.class).toString();

                    String fecha = postSnapshot.child("fecha").getValue(String.class).toString();

                    String descripcion = postSnapshot.child("descripcion").getValue(String.class).toString();
                    Log.i("informacion", " " + imagen + urlimagen + id + nombre + avatar);

                    try {
                        items.add(new foto("https://myservidor.000webhostapp.com/fotos_publicaciones/"+imagen, fecha(fecha), descripcion, "https://myservidor.000webhostapp.com/fotos_usuarios/"+id+".jpg",id,fecha));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
                Collections.sort(items, Collections.<foto>reverseOrder());
                new Adapter_Main().wap(items);
                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

     /*  MySingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        String url ="https://myservidor.000webhostapp.com/api/fotos.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("JSON",response);
                        try{
                            if(response.length()>2){
                                items.clear();
                                JSONObject jsonObject=new JSONObject(response);
                                JSONArray jsonArray=jsonObject.getJSONArray("fotos");
                                for (int i =0;i<jsonArray.length();i++){
                                    //insertamos los datos en el arrego items para luego poder mostrarlos en el recyclerview
                                    items.add(new foto("https://myservidor.000webhostapp.com/fotos_publicaciones/"+jsonArray.getJSONObject(i).getString("imagen"),jsonArray.getJSONObject(i).getString("fecha"),jsonArray.getJSONObject(i).getString("descripcion"),"https://myservidor.000webhostapp.com/fotos_usuarios/"+jsonArray.getJSONObject(i).getString("foto_perfil"),jsonArray.getJSONObject(i).getString("nombre")));

                                }
                                 //actualizamos el adaptador del recyclerview
                                new Adapter_Main().wap(items);
                                mAdapter.notifyDataSetChanged();
                                Toast.makeText(MainActivity.this, "Se cargaron las tres ultimas noticias", Toast.LENGTH_SHORT).show();

                            }
                            else {
                                Toast.makeText(MainActivity.this, "No hay publicaciones", Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("error","hay un error"+error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("tipo_query","1");
                return params;
            }
        };
        stringRequest.setShouldCache(false);
// Add the request to the RequestQueue.
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);*/

    }
    //hace la mima funcion del metodo de arriba

    private DatabaseReference root1 = FirebaseDatabase.getInstance().getReference().child("usuarios");

    //metodo para verificar si ya el dispositivo android esta registrado en el servidor
    private void verificar() throws ExecutionException, InterruptedException {
        final String uuid = Settings.Secure.getString(this.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        root1.child(uuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                } else {
                    Intent intent = new Intent(MainActivity.this, login.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
       /* MySingleton.getInstance(this.getApplicationContext()).
                getRequestQueue();
        String url ="https://myservidor.000webhostapp.com/api/usuarios.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("JSON_login",response);

                            if(response.length()>2) {



                            }

                            else {
                                Intent intent = new Intent(MainActivity.this, login.class);
                                startActivity(intent);
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
                params.put("tipo_query","1");
                params.put("id",uuid);
                return params;
            }
        };
        stringRequest.setShouldCache(false);
// Add the request to the RequestQueue.
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);*/



      /*  String []parms={"tipo_query","1","id",uuid};

        res= http.Post("https://myservidor.000webhostapp.com/api/usuarios.php",parms);
        Log.i("log_login",res);
        Toast.makeText(this, res, Toast.LENGTH_LONG).show();
        if(res.length()<2){
            Intent intent=new Intent(MainActivity.this,login.class);
            startActivity(intent);

        }
        else {

        }*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //opciones del menu superios
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, ajustes.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
