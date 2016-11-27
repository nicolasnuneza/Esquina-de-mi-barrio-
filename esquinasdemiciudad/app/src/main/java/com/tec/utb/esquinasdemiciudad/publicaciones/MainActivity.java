package com.tec.utb.esquinasdemiciudad.publicaciones;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appdatasearch.GetRecentContextCall;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tec.utb.esquinasdemiciudad.MySingleton;
import com.tec.utb.esquinasdemiciudad.R;
import com.tec.utb.esquinasdemiciudad.ajustes;
import com.tec.utb.esquinasdemiciudad.http.http;
import com.tec.utb.esquinasdemiciudad.login.login;
import com.tec.utb.esquinasdemiciudad.publicar.subir_foto;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    //declaramos variables para el recyclerview
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBar;
    int vista=20;
    final ArrayList<foto> items = new ArrayList();//array que contendra las publicaciones para despues mostrarlas en el recyclerveiw

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
progressBar= (ProgressBar) findViewById(R.id.progress_bar);
        //metodo para verivicar si ya estamos registrado en la app
            verificar();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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

                mRecyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
vista=20;

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

    }

    String nombre = "";
    String avatar = "";
    String id = "";
    String urlimagen = "";
    String urlavatar = "";
    DatabaseReference root = FirebaseDatabase.getInstance().getReference();

    //metodo para obtener y cargar las publicaciones desde el servidor

Context context;
    private void mostrar() {
        context=this;
        root.child("pubicaciones").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int num=0;
                items.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    final foto foto1=postSnapshot.getValue(foto.class);
// Petición


                    num+=1;
                        items.add(foto1);


                }
Log.i("numero",""+num);
                Collections.sort(items, Collections.<foto>reverseOrder());
                new Adapter_Main().wap(items);
                mAdapter.notifyDataSetChanged();

                mRecyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    //hace la mima funcion del metodo de arriba

    private DatabaseReference root1 = FirebaseDatabase.getInstance().getReference().child("usuarios");

    //metodo para verificar si ya el dispositivo android esta registrado en el servidor
    private void verificar() {
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

}
