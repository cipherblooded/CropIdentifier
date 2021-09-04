package com.hackathon.cropidentifier.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hackathon.cropidentifier.AppController;
import com.hackathon.cropidentifier.R;
import com.hackathon.cropidentifier.pojo.User;

public class Signup extends AppCompatActivity {

    EditText et_signUpName,et_signUpMobileNo,et_signUpEmail,et_signUpPassword;
    Button bt_logInTemp,bt_signUp;
    Vibrator vibrator;
    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        loading=new ProgressDialog(this);

        bt_logInTemp=findViewById(R.id.login_temp);
        bt_signUp=findViewById(R.id.signup);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        et_signUpName=findViewById(R.id.signup_name);
        et_signUpMobileNo=findViewById(R.id.signup_mobileno);
        et_signUpEmail=findViewById(R.id.signup_email);
        et_signUpPassword=findViewById(R.id.signup_password);

        bt_logInTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(28);

                Intent intent=new Intent(Signup.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        bt_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrator.vibrate(28);

                String name = et_signUpName.getText().toString();
                String mobileNo = et_signUpMobileNo.getText().toString();
                String email = et_signUpEmail.getText().toString();
                String password = et_signUpPassword.getText().toString();

                if(!(name.isEmpty()||mobileNo.isEmpty()||email.isEmpty()||password.isEmpty())){

                    loading = ProgressDialog.show(view.getContext(),"Signing Up","Please Wait",false,false);

                    boolean validPassword = isValidPassword(password);
                    boolean validMobileNo = isValidMobileNo(mobileNo);

                    if(validPassword&&validMobileNo) {
                        AppController.signUp(Signup.this,email,password,name,mobileNo,loading);
                    }else {
                        if(!validMobileNo){
                            AppController.showAlertDialog(Signup.this,"Enter Valid Mobile Number");
                        } else {
                            showAlertDialog("Password is too week to set",
                                    "Password must be of at least 8 Character and\nshould contain a:\n\n"+
                                            "1. Uppercase Letter\n" +
                                            "2. Lowercase Letter\n" +
                                            "3. One Digit (0 to 9)\n"+
                                            "4. Special Character (@, #, %, $)\n");
                        }
                    }
                }else {
                    if(name.isEmpty()){
                        AppController.showAlertDialog(Signup.this,"Enter Your Name");
                    }else if (mobileNo.isEmpty()){
                        AppController.showAlertDialog(Signup.this,"Enter Your Mobile Number");
                    }else if(email.isEmpty()){
                        AppController.showAlertDialog(Signup.this,"Enter Your Email");
                    }else if(password.isEmpty()){
                        AppController.showAlertDialog(Signup.this,"Enter Your Password");
                    }
                }
            }
        });

    }

    private boolean isValidMobileNo(String mobileNo) {

        String tempMobileNumber = mobileNo;
        char ch = ' ';

        if (mobileNo.length()==10||mobileNo.length()==13){

            if(mobileNo.length()==13) {

                if(mobileNo.substring(0,3).equals("+91")){
                    tempMobileNumber = mobileNo.substring(3);
                }else {
                    return false;
                }

            }
            for (int i = 0; i < tempMobileNumber.length() ; i++) {
                ch = tempMobileNumber.charAt(i);
                if(!(ch>=48&&ch<=57)){
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }

    }

    private boolean isValidPassword(String password) {

        boolean flag1 = false, flag2 = false, flag3 = false, flag4 = false;
        char ch = ' ';

        if(password.length()>=8){

            for (int i = 0; i < password.length(); i++) {
                ch= password.charAt(i);
                if(ch>=65&&ch<=90){
                    flag1=true;
                }else if(ch>=97&&ch<=122){
                    flag2=true;
                }else if(ch>=48&&ch<=57){
                    flag3=true;
                }else if(ch>=35&&ch<=38||ch==63||ch==64){
                    flag4=true;
                }
            }
            if (flag1&&flag2&&flag3&&flag4){
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }

    private void showAlertDialog(String tittle, String stringToShow){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(tittle)
                .setMessage(stringToShow)
                .setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog alert11 = builder.create();
        alert11.show();
    }
}