package com.tec.utb.esquinasdemiciudad.comentarios;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tec.utb.esquinasdemiciudad.R;
import com.tec.utb.esquinasdemiciudad.login.login;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class comentarios extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ImageButton imageButton;
    TextView getTextView;
ProgressBar progressBar;
    final ArrayList<comentario> items = new ArrayList();//array que contendra los comentarios para despues mostrarlas en el recyclerveiw
    DatabaseReference root = FirebaseDatabase.getInstance().getReference();

    EditText editText;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        editText= (EditText) findViewById(R.id.edittext);
        textView= (TextView) findViewById(R.id.textview_publicar);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
verificar();
            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_main);
        progressBar= (ProgressBar) findViewById(R.id.progress_bar);
        imageButton= (ImageButton) findViewById(R.id.boton_back);
        getTextView= (TextView) findViewById(R.id.textView3);
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
        mAdapter = new Adapter_comentarios(items, getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);
        mostrar_comentarios(getIntent().getStringExtra("id"));
    }


    private void mostrar_comentarios(final String id){
        root.child("comentarios").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int num=0;
                items.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    comentario comentario1=postSnapshot.getValue(comentario.class);

                    if(comentario1.getId_publicacion().equals(id)){
                        items.add(comentario1);
                        num=num+1;
                    }



                }
                getTextView.setText(num+" Comentarios");
                new Adapter_comentarios().wap(items);
                mAdapter.notifyDataSetChanged();
                mRecyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(); return dateFormat.format(date);
    }

    private void comentar(String id_publicacion,String mensaje) {
                //
        if(!mensaje.trim().equals("")){
                 String fecha = getDateTime();
                                String uuid = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                                    String id=root.child("comentario").push().getKey();
                                            comentario comentario1=new comentario(id,mensaje,id_publicacion,uuid,fecha);


                                root.child("comentarios").child(id).setValue(comentario1);
            editText.setText("");}





    }
    private DatabaseReference root1 = FirebaseDatabase.getInstance().getReference().child("usuarios");

    //metodo para verificar si ya el dispositivo android esta registrado en el servidor
    private void verificar() {
        final String uuid = Settings.Secure.getString(this.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        root1.child(uuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    comentar(getIntent().getStringExtra("id"),editText.getText().toString());
                } else {
                    Intent intent = new Intent(comentarios.this, login.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

}
