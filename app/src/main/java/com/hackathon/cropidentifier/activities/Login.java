package com.hackathon.cropidentifier.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.hackathon.cropidentifier.AppController;
import com.hackathon.cropidentifier.R;
import com.hackathon.cropidentifier.pojo.User;

import static com.hackathon.cropidentifier.AppController.currentUser;
import static com.hackathon.cropidentifier.AppController.database;
import static com.hackathon.cropidentifier.AppController.gson;
import static com.hackathon.cropidentifier.AppController.mAuth;
import static com.hackathon.cropidentifier.AppController.sp;
import static com.hackathon.cropidentifier.AppController.user;

public class Login extends AppCompatActivity {

    ProgressDialog loading;
    String link,email,password;
    Button bt_signUpTemp, bt_logIn;
    EditText et_logInEmail, et_logInPassword;
    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(currentUser!=null){
            startActivity(new Intent(this, UserProfile.class));
            finishAffinity();
        }

        bt_signUpTemp=findViewById(R.id.signup_temp);
        et_logInEmail=findViewById(R.id.login_email);
        et_logInPassword=findViewById(R.id.login_password);
        bt_logIn=findViewById(R.id.login);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        bt_signUpTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(28);

                Intent intent=new Intent(Login.this, Signup.class);
                startActivity(intent);
                finish();
            }
        });

        bt_logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(28);

                email = et_logInEmail.getText().toString().trim();
                password = et_logInPassword.getText().toString().trim();

                ProgressDialog dialog=new ProgressDialog(Login.this);
                dialog.setMessage("Logging in");
                dialog.setCancelable(false);
                dialog.show();

                if(!(email.isEmpty()||password.isEmpty())){
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        currentUser= mAuth.getCurrentUser();
                                        database.getReference().child("Users").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                Log.e("user",snapshot.getValue().toString());
                                                user= snapshot.getValue(User.class);
                                                sp.edit().putString("cuser", gson.toJson(user)).apply();
                                                startActivity(new Intent(Login.this, UserProfile.class));
                                                finishAffinity();
                                                dialog.dismiss();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                dialog.dismiss();
                                            }
                                        });
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(Login.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                }
                            });
                }
                else {
                    if(email.isEmpty()){
                        AppController.showAlertDialog(Login.this,"Please Enter Your Registered Email");
                    }else {
                        AppController.showAlertDialog(Login.this,"Please Enter Your Password");
                    }
                }
            }
        });

    }
}