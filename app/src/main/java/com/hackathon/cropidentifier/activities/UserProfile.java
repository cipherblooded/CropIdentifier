package com.hackathon.cropidentifier.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hackathon.cropidentifier.R;

import static com.hackathon.cropidentifier.AppController.currentUser;
import static com.hackathon.cropidentifier.AppController.mAuth;
import static com.hackathon.cropidentifier.AppController.sp;
import static com.hackathon.cropidentifier.AppController.user;

public class UserProfile extends AppCompatActivity {

    Button bt_add_data;
    Button bt_view_data;
    TextView tv_userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        bt_add_data=findViewById(R.id.add_data);
        bt_view_data=findViewById(R.id.view_data);
        tv_userName=findViewById(R.id.userName);

        tv_userName.setText(user.getName());

        bt_add_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(UserProfile.this, AddQuery.class);
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

    public void signOut(View view) {

        mAuth.signOut();
        currentUser=null;
        user=null;
        sp.edit().clear().apply();
        startActivity(new Intent(this,Login.class));
        finishAffinity();

    }
}