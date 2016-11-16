package com.tec.utb.esquinasdemiciudad;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
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
import com.tec.utb.esquinasdemiciudad.http.http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
    boolean todo=false;//sirve para determinar que actualizacion hacer
    String res="";
    final ArrayList<foto> items =new ArrayList();//array que contendra las publicaciones para despues mostrarlas en el recyclerveiw
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
              Intent intent=new Intent(MainActivity.this,subir_foto.class);
                startActivity(intent);
            }
        });
        swipeRefreshLayout= (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
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
                if(todo==false){
                    mostrar();
                }else {
                    mostrar_todo();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        mRecyclerView= (RecyclerView) findViewById(R.id.recyclerview_main);

        mRecyclerView.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        // Crear un nuevo adaptador
        mAdapter = new Adapter_Main(items,getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int maxScroll = recyclerView.computeVerticalScrollRange();
                int currentScroll = recyclerView.computeVerticalScrollOffset() + recyclerView.computeVerticalScrollExtent();
                if (currentScroll==maxScroll) {

                    Log.i("scroll","up, ");
                } else {


                }
            }
        });
        //cargar las publicaciones
        mostrar();
    }


        //metodo para obtener y cargar las publicaciones desde el servidor
    private void mostrar() {

       MySingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        String url ="http://comidasutb.gzpot.com/esquina/api/fotos.php";
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
                                    items.add(new foto("http://comidasutb.gzpot.com/esquina/fotos_publicaciones/"+jsonArray.getJSONObject(i).getString("imagen"),jsonArray.getJSONObject(i).getString("fecha"),jsonArray.getJSONObject(i).getString("descripcion"),"http://comidasutb.gzpot.com/esquina/fotos_usuarios/"+jsonArray.getJSONObject(i).getString("foto_perfil"),jsonArray.getJSONObject(i).getString("nombre")));

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
                Log.i("error","hay un error");
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
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);

    }
    //hace la mima funcion del metodo de arriba
    private void mostrar_todo(){
        final RequestQueue queue = MySingleton.getInstance(this.getApplicationContext()).
                getRequestQueue();
        String url ="http://comidasutb.gzpot.com/esquina/api/fotos.php";
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
                                    items.add(new foto("http://comidasutb.gzpot.com/esquina/fotos_publicaciones/"+jsonArray.getJSONObject(i).getString("imagen"),jsonArray.getJSONObject(i).getString("fecha"),jsonArray.getJSONObject(i).getString("descripcion"),"http://comidasutb.gzpot.com/esquina/fotos_usuarios/"+jsonArray.getJSONObject(i).getString("foto_perfil"),jsonArray.getJSONObject(i).getString("nombre")));

                                }
                                Toast.makeText(MainActivity.this, "Se cargaron todas las noticias", Toast.LENGTH_SHORT).show();

                                new Adapter_Main().wap(items);
                                mAdapter.notifyDataSetChanged();
                            }
                            else {                                Toast.makeText(MainActivity.this, "Ha ocurrido algun error", Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
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
                params.put("tipo_query","3");
                return params;
            }
        };
        stringRequest.setShouldCache(false);
// Add the request to the RequestQueue.
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

//metodo para verificar si ya el dispositivo android esta registrado en el servidor
    private void verificar() throws ExecutionException, InterruptedException {
        String uuid = Settings.Secure.getString(this.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        String []parms={"tipo_query","1","id",uuid};

        res= http.Post("http://comidasutb.gzpot.com/esquina/api/usuarios.php",parms);
        if(res.length()<2){
            Intent intent=new Intent(MainActivity.this,login.class);
            startActivity(intent);

        }
        else {

        }

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
Intent intent=new Intent(MainActivity.this,ajustes.class);
        startActivity(intent);
        }
        if(id==R.id.action_vertodo){

            if(todo==false){
                item.setTitle("Ver solo algunas");
                todo=true;
                mostrar_todo();

            }else {
                item.setTitle("Ver todas");
                todo=false;
                mostrar();

            }


        }
        return super.onOptionsItemSelected(item);
    }
}
