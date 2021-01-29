package com.hackathon.cropidentifier.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hackathon.cropidentifier.AppController;
import com.hackathon.cropidentifier.R;
import com.hackathon.cropidentifier.pojo.Crop;
import com.hackathon.cropidentifier.utils.SingleShotLocationProvider;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.util.Timer;
import java.util.TimerTask;

import static com.hackathon.cropidentifier.AppController.currentUser;
import static com.hackathon.cropidentifier.AppController.database;
import static com.hackathon.cropidentifier.AppController.mAuth;
import static com.hackathon.cropidentifier.AppController.sp;
import static com.hackathon.cropidentifier.AppController.user;

public class QueryImage extends AppCompatActivity implements IPickResult {

    private static final int PERMISSION_REQUEST_CODE = 1023;
    private TextView latitudeField;
    private TextView longitudeField;
    Toolbar toolbar;

    ImageView iv_image;
    ImageButton iv_pick;

    Timer timer;
    int count = 0;

    Uri image;
    SingleShotLocationProvider.GPSCoordinates coordinates;

    ProgressDialog dialog;

    Crop pendingCrop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_image);

        latitudeField = findViewById(R.id.tv_lat);
        longitudeField = findViewById(R.id.tv_long);

        iv_image = findViewById(R.id.iv_image);
        iv_pick = findViewById(R.id.pick_image);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (!checkPermission()) {
            //Toast.makeText(this, "Ask Permission", Toast.LENGTH_SHORT).show();
            requestPermission();
        }

        count = 0;

        database.getReference().child("Crops").child(user.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Crop crop;
                crop = snapshot.getValue(Crop.class);
                if (crop == null || pendingCrop == null || !crop.getId().equals(pendingCrop.getId()))
                    return;

                if (dialog != null)
                    dialog.dismiss();
                if (timer != null)
                    timer.cancel();

                Toast.makeText(QueryImage.this, "Result Fetched", Toast.LENGTH_SHORT).show();

                showCropResult(crop);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showCropResult(Crop crop) {
        AlertDialog.Builder builder = new AlertDialog.Builder(QueryImage.this);
        builder.setTitle("Result");
        if (crop.getConfidence() > 65) {
            builder.setMessage("Crop in image is identified as " + crop.getCrop() + " with chances as " + crop.getConfidence() + "%");

            builder.setNegativeButton("Similar Images", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Uri uri = Uri.parse("https://www.google.com/search?tbm=isch&q=" + crop.getCrop()); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    showCropResult(crop);
                }
            });

            builder.setNeutralButton("Read More", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Uri uri = Uri.parse(crop.getDesc());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    showCropResult(crop);
                }
            });
        } else
            builder.setMessage("Sorry we cannot identify the crop in this picture accurately");

        builder.setCancelable(true);

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                startActivity(new Intent(QueryImage.this, UserProfile.class));
                finishAffinity();
            }
        });

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(QueryImage.this, UserProfile.class));
                finishAffinity();
            }
        });

        /*builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                startActivity(new Intent(QueryImage.this, UserProfile.class));
                finishAffinity();
            }
        });*/

        builder.show();
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

        if (item.getItemId() == R.id.menu_about) {
            Toast.makeText(this, "Sub Item 1 clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else if (item.getItemId() == R.id.menu_signOut) {

            mAuth.signOut();
            currentUser = null;
            user = null;
            sp.edit().clear().apply();
            startActivity(new Intent(this, Login.class));
            finishAffinity();

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (canGetLocation())
            SingleShotLocationProvider.requestSingleUpdate(this, new SingleShotLocationProvider.LocationCallback() {
                @Override
                public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                    latitudeField.setText("Latitude - " + location.latitude);
                    longitudeField.setText("Longitude - " + location.longitude);
                    coordinates = location;
                }
            });
        else {
            showSettingsAlert();
        }
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        AlertDialog dialog;

        // Setting Dialog Title
        alertDialog.setTitle("Attention!");

        // Setting Dialog Message
        alertDialog.setMessage("Please Enable Location Services");

        // On pressing Settings button
        alertDialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });

        dialog = alertDialog.create();
        dialog.show();
    }

    public boolean canGetLocation() {
        boolean result = true;
        LocationManager lm;
        boolean gpsEnabled = false;
        boolean networkEnabled = false;

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // exceptions will be thrown if provider is not permitted.
        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            networkEnabled = lm
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        return gpsEnabled && networkEnabled;
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean isGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    isGranted = false;
                    break;
                }
            }

            if (isGranted) {

                Toast.makeText(this, "Proceed", Toast.LENGTH_SHORT).show();
                // main logic
            } else {
                Toast.makeText(this, "App can't work without Permissions", Toast.LENGTH_SHORT).show();
                finishAffinity();
            }
        }
    }

    public void pickImage(View view) {

//        PickImageDialog.build(new PickSetup()).show(this);

        PickSetup setup = new PickSetup()
                .setIconGravity(Gravity.LEFT)
                .setButtonOrientation(LinearLayout.HORIZONTAL);
        PickImageDialog.build(setup).show(this);

    }

    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null) {

            iv_image.setImageBitmap(r.getBitmap());
            iv_image.setVisibility(View.VISIBLE);
            iv_pick.setVisibility(View.GONE);
            image = r.getUri();

        } else {
            //Handle possible errors
            //TODO: do what you have to do with r.getError();
            Toast.makeText(this, r.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void uploadQuery(View view) {

        if (image == null) {
            Toast.makeText(this, "Click image first", Toast.LENGTH_SHORT).show();
            return;
        } else if (coordinates == null) {
            Toast.makeText(this, "Enable location services", Toast.LENGTH_SHORT).show();
            return;
        }
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Uploading Data");
        dialog.show();
        Crop crop = new Crop("", "Demo", coordinates.latitude, coordinates.longitude, "Not Available");

        StorageReference child = AppController.mStorageRef.child("Crops").child(AppController.user.getUid()).child(crop.getId() + ".jpg");

        child.putFile(image)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        child.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadPhotoUrl) {
                                //Now play with downloadPhotoUrl
                                //Store data into Firebase Realtime Database
                                crop.setImage(downloadPhotoUrl.toString());
                                database.getReference().child("Requests").child(user.getUid()).child(String.valueOf(crop.getId())).setValue(crop).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            pendingCrop = crop;
                                            Toast.makeText(QueryImage.this, "Upload Success...Waiting for results", Toast.LENGTH_SHORT).show();
                                            dialog.setMessage("Analysing Image");
                                            /*startActivity(new Intent(QueryImage.this, UserProfile.class));
                                            finishAffinity();*/
                                            timer = new Timer();
                                            timer.schedule(new TimerTask() {
                                                @Override
                                                public void run() {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(QueryImage.this, "Server is offline!!!! Try Again Later", Toast.LENGTH_SHORT).show();
                                                            dialog.dismiss();
                                                            startActivity(new Intent(QueryImage.this, UserProfile.class));
                                                            finishAffinity();
                                                            // database.getReference().child("Requests").child(user.getUid()).child(String.valueOf(crop.getId())).removeValue();
                                                        }
                                                    });
                                                }
                                            }, 10000);
                                        } else {
                                            Toast.makeText(QueryImage.this, "Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        dialog.dismiss();
                    }
                });

    }
}