package com.hackathon.cropidentifier.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.hackathon.cropidentifier.R;
import com.hackathon.cropidentifier.adapters.QueriesAdapter;
import com.hackathon.cropidentifier.pojo.Crop;

import java.util.ArrayList;
import java.util.List;

import static com.hackathon.cropidentifier.AppController.currentUser;
import static com.hackathon.cropidentifier.AppController.database;
import static com.hackathon.cropidentifier.AppController.mAuth;
import static com.hackathon.cropidentifier.AppController.sp;
import static com.hackathon.cropidentifier.AppController.user;

public class ViewQueries extends AppCompatActivity {

    RecyclerView recyclerView;
    QueriesAdapter adapter;
    List<Crop> allCrops=new ArrayList<>();
    ProgressDialog dialog;
    Toolbar toolbar;
    View tv_noData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_queries);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tv_noData=findViewById(R.id.tv_noData);

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
                if(allCrops.size()==0){
                    recyclerView.setVisibility(View.GONE);
                    tv_noData.setVisibility(View.VISIBLE);
                }else {
                    recyclerView.setVisibility(View.VISIBLE);
                    tv_noData.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewQueries.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ViewQueries.this,UserProfile.class));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.options_menu_and_sub_items, menu);
        inflater.inflate(R.menu.menu_with_icons, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.menu_about){
            Toast.makeText(this, "Sub Item 1 clicked", Toast.LENGTH_SHORT).show();
            return true;
        }else if(item.getItemId()==R.id.menu_signOut){

            mAuth.signOut();
            currentUser=null;
            user=null;
            sp.edit().clear().apply();
            startActivity(new Intent(this,Login.class));
            finishAffinity();

            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }
}