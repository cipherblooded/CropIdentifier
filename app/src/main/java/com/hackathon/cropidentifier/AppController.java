package com.hackathon.cropidentifier;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.hackathon.cropidentifier.activities.UserProfile;
import com.hackathon.cropidentifier.pojo.User;

public class AppController extends Application {

    public static FirebaseAuth mAuth;
    public static FirebaseUser currentUser;
    public static FirebaseDatabase database;
    public static StorageReference mStorageRef;
    public static User user;
    public static SharedPreferences sp;
    public static Gson gson;

    public static void signUp(Activity context, String email, String password, String name, String mobileNo, ProgressDialog dialog) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Sign up successful", Toast.LENGTH_SHORT).show();
                    currentUser = mAuth.getCurrentUser();
                    User user = new User(currentUser.getUid(), name, email, mobileNo);

                    uploadUser(context, user);
                } else {
                    Toast.makeText(context, "Sign up failed!!!\n" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Failed", task.getException().getMessage());
                }
                dialog.dismiss();
            }
        });

    }

    private static void uploadUser(Activity activity, User user) {

        database.getReference().child("Users").child(user.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    AppController.user = user;
                    sp.edit().putString("cuser", gson.toJson(user)).apply();
                    activity.startActivity(new Intent(activity, UserProfile.class));
                    activity.finishAffinity();
                }
            }
        });

    }

    public static void showAlertDialog(Context context, String mssg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(mssg)
                .setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog alert11 = builder.create();
        alert11.show();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);

        sp = getSharedPreferences("data", 0);
        gson = new Gson();
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        currentUser = mAuth.getCurrentUser();
        user = gson.fromJson(sp.getString("cuser", null), User.class);

    }
}
