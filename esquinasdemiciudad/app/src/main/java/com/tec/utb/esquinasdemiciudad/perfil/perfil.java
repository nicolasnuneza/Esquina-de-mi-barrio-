package com.tec.utb.esquinasdemiciudad.perfil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tec.utb.esquinasdemiciudad.MySingleton;
import com.tec.utb.esquinasdemiciudad.R;
import com.tec.utb.esquinasdemiciudad.login.Usuarios;
import com.tec.utb.esquinasdemiciudad.publicaciones.foto;


import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;

public class perfil extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView num;
    private ProgressBar progressBar;
    private ImageButton imageButton;
    private LinearLayout linearLayout;
    TextView nombre;
    CircleImageView circleImageView;
    String id_usuario;
    final ArrayList<foto> items = new ArrayList();//array que contendra las publicaciones para despues mostrarlas en el recyclerveiw
    DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    int vista=100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        circleImageView= (CircleImageView) findViewById(R.id.profile_image);
        nombre= (TextView) findViewById(R.id.edittext_nombre);
        num=(TextView) findViewById(R.id.edittext_num_publicaciones);
        progressBar= (ProgressBar) findViewById(R.id.progress_bar);
        linearLayout= (LinearLayout) findViewById(R.id.linearLayout);
        id_usuario=getIntent().getStringExtra("id_usuario");
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_main);
imageButton= (ImageButton) findViewById(R.id.boton_back);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mRecyclerView.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Crear un nuevo adaptador
        mAdapter = new Adapter_Main(items, getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);

        cargar_datos();
        mostrar();

    }
    private void mostrar() {
         root.child("pubicaciones").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int nume=0;
                items.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    foto foto1=postSnapshot.getValue(foto.class);
if(foto1.getId_persona().equals(id_usuario)){
    nume=nume+1;
    items.add(foto1);

}
                }
                num.setText(""+nume);
                Collections.sort(items, Collections.<foto>reverseOrder());
                new Adapter_Main().wap(items);
                mAdapter.notifyDataSetChanged();
                linearLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
    //en este metodo cargamos los datos del usuario
    Context context;
    private void cargar_datos(){
context=this;
        root.child("usuarios").child(id_usuario).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuarios usuario=dataSnapshot.getValue(Usuarios.class);
                ImageLoader imageLoader= MySingleton.getInstance(context).getImageLoader();

                imageLoader.get(usuario.getFoto(), ImageLoader.getImageListener(circleImageView,
                        R.drawable.ic_cached_black_24dp, R.drawable.ic_close_black_24dp));

                nombre.setText(usuario.getNombre());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }


}
