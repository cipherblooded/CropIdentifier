package com.hackathon.cropidentifier.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.hackathon.cropidentifier.R;
import com.hackathon.cropidentifier.adapters.QueriesAdapter;
import com.hackathon.cropidentifier.pojo.Crop;

import java.util.ArrayList;
import java.util.List;

import static com.hackathon.cropidentifier.AppController.database;
import static com.hackathon.cropidentifier.AppController.user;

public class ViewQueries extends AppCompatActivity {

    RecyclerView recyclerView;
    QueriesAdapter adapter;
    List<Crop> allCrops=new ArrayList<>();
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_queries);

        dialog=new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Loading data");
        dialog.show();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter=new QueriesAdapter(this,allCrops);

        recyclerView.setAdapter(adapter);

        database.getReference().child("Crops").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allCrops.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Log.e("child",child.getValue().toString());
                    Crop crop=child.getValue(Crop.class);

                    allCrops.add(crop);
                }

                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewQueries.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ViewQueries.this,UserProfile.class));
            }
        });

    }
}