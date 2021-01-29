package com.hackathon.cropidentifier.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hackathon.cropidentifier.R;

import static com.hackathon.cropidentifier.AppController.currentUser;
import static com.hackathon.cropidentifier.AppController.mAuth;
import static com.hackathon.cropidentifier.AppController.sp;
import static com.hackathon.cropidentifier.AppController.user;

public class UserProfile extends AppCompatActivity {

    Button bt_add_data;
    Button bt_view_data;
    TextView tv_userName;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        bt_add_data=findViewById(R.id.add_data);
        bt_view_data=findViewById(R.id.view_data);
        tv_userName=findViewById(R.id.userName);

        tv_userName.setText(user.getName());

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bt_add_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(UserProfile.this, QueryImage.class);
                startActivity(intent);

            }
        });

        bt_view_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UserProfile.this, ViewQueries.class);
                startActivity(intent);
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