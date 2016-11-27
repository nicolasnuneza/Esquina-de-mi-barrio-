package com.tec.utb.esquinasdemiciudad.likes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tec.utb.esquinasdemiciudad.R;

import java.util.ArrayList;

public class megustas extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ImageButton imageButton;
    TextView textView;
    final ArrayList<like> items = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_megustas);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        imageButton= (ImageButton) findViewById(R.id.boton_back);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_main);
textView= (TextView) findViewById(R.id.textView3);
        mRecyclerView.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Crear un nuevo adaptador
        mAdapter = new Adapter_megustas(items, getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);
        Log.i("id-publicacion",getIntent().getStringExtra("id"));
        mostrar(getIntent().getStringExtra("id"));

    }
    DatabaseReference root = FirebaseDatabase.getInstance().getReference();

    private void mostrar(final String id){
        root.child("likes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                items.clear();
                int num=0;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    like like1=postSnapshot.getValue(like.class);

                    if(like1.getId_publicacion().equals(id)){
                        items.add(like1);
                        num=num+1;
                    }



                }
                textView.setText(num+" Me gusta");
                new Adapter_megustas().wap(items);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
