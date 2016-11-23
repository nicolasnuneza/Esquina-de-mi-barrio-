package com.tec.utb.esquinasdemiciudad.publicaciones;

import android.content.Intent;
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

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tec.utb.esquinasdemiciudad.R;
import com.tec.utb.esquinasdemiciudad.ajustes;
import com.tec.utb.esquinasdemiciudad.login.login;
import com.tec.utb.esquinasdemiciudad.publicar.subir_foto;

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
    final ArrayList<foto> items = new ArrayList();//array que contendra las publicaciones para despues mostrarlas en el recyclerveiw


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

            //metodo para verivicar si ya estamos registrado en la app


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


                        items.add(new foto("https://myservidor.000webhostapp.com/fotos_publicaciones/"+imagen, fecha, descripcion, "https://myservidor.000webhostapp.com/fotos_usuarios/"+id+".jpg",id,fecha, postSnapshot.child("id").getValue().toString()));


                }
                Collections.sort(items, Collections.<foto>reverseOrder());
                new Adapter_Main().wap(items);
                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    //hace la mima funcion del metodo de arriba



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
