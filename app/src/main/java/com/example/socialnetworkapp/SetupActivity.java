package com.example.socialnetworkapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tapadoo.alerter.Alerter;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class SetupActivity extends AppCompatActivity {

    private CircleImageView Mimage;
    private EditText Username;
    private EditText Fullname;
    private EditText Address;
    private Button SaveButton;
    private Uri imageuri = null;
    private DatabaseReference Muserdatabase;
    private FirebaseAuth Mauth;
    private String CurrentUserID;
    private ProgressDialog Mprogress;
    private StorageReference Muserimagestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        Muserimagestore = FirebaseStorage.getInstance().getReference().child("User_image");
        Mprogress = new ProgressDialog(SetupActivity.this);
        Mauth = FirebaseAuth.getInstance();
        CurrentUserID = Mauth.getCurrentUser().getUid();


        Muserdatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentUserID);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        Mimage = findViewById(R.id.ProfileImageID);
        Username = findViewById(R.id.UserNameID);
        Fullname = findViewById(R.id.FullnameID);
        Address = findViewById(R.id.AddressID);
        SaveButton = findViewById(R.id.SaveID);

        SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String usernametext = Username.getText().toString();
                String fullnametext = Fullname.getText().toString();
                String addresstext = Address.getText().toString();

                if(usernametext.isEmpty() || fullnametext.isEmpty() || addresstext.isEmpty()){
                    Alerter.create(SetupActivity.this)
                            .setTitle("Follow me !")
                            .setText("Please update your all information")
                            .setBackgroundColorRes(R.color.alarter_colour)
                            .enableSwipeToDismiss()
                            .show();
                }
                else {

                    Mprogress.setTitle("Updating ...");
                    Mprogress.setMessage("Please wait your bio data is saving");
                    Mprogress.setCanceledOnTouchOutside(false);
                    Mprogress.show();
                    HashMap<String, String> usermap = new HashMap<>();
                    usermap.put("name", usernametext);
                    usermap.put("fullname", fullnametext);
                    usermap.put("address", addresstext);
                    usermap.put("dob", "null");
                    usermap.put("gender", "null");
                    usermap.put("statas", "null");
                    usermap.put("relanship", "null");

                    Muserdatabase.setValue(usermap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toasty.success(SetupActivity.this, "Information is update", Toasty.LENGTH_LONG).show();
                                        Mprogress.dismiss();
                                        Intent intent = new Intent(SetupActivity.this, HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else {
                                        String errormessege = task.getException().getMessage().toString();
                                        Toasty.success(SetupActivity.this, errormessege, Toasty.LENGTH_LONG).show();
                                        Mprogress.dismiss();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            String errormessege = e.getMessage().toString();
                            Toasty.error(SetupActivity.this, errormessege, Toasty.LENGTH_LONG).show();
                            Mprogress.dismiss();
                        }
                    });
                }
            }
        });

        Mimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if(ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                        ActivityCompat.requestPermissions(SetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    }
                    else {
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(SetupActivity.this);
                    }
                }
            }
        });

        Muserdatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    if(dataSnapshot.hasChild("downloadurl")){
                        String profileimage = dataSnapshot.child("downloadurl").getValue().toString();
                        Glide.with(SetupActivity.this).load(profileimage).placeholder(R.drawable.default_image).into(Mimage);
                    }
                    else {

                    }

                    if(dataSnapshot.hasChild("name")){
                        String username = dataSnapshot.child("name").getValue().toString();
                        Username.setText(username);
                    }
                    else {

                    }
                    if(dataSnapshot.hasChild("fullname")){
                        String fullname = dataSnapshot.child("fullname").getValue().toString();
                        Fullname.setText(fullname);
                    }
                    else {

                    }
                    if(dataSnapshot.hasChild("address")) {
                        String address = dataSnapshot.child("address").getValue().toString();
                        Address.setText(address);
                    }
                    else {

                    }
                }
                else {
                //   Toasty.info(SetupA, Toasty.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
               imageuri = result.getUri();
               Mimage.setImageURI(imageuri);

               StorageReference mpath = Muserimagestore.child(CurrentUserID+".jpg");
               mpath.putFile(imageuri)
                       .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                           @Override
                           public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                               if(task.isSuccessful()){

                                   String downloadurl = task.getResult().getDownloadUrl().toString();
                                   Muserdatabase.child("downloadurl").setValue(downloadurl)
                                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                                               @Override
                                               public void onComplete(@NonNull Task<Void> task) {
                                                   if(task.isSuccessful()){
                                                       Toasty.success(SetupActivity.this, "profile image has been set", Toasty.LENGTH_LONG).show();
                                                   }
                                                   else {
                                                       String errormessege = task.getException().getMessage().toString();
                                                       Toasty.error(SetupActivity.this, errormessege, Toasty.LENGTH_LONG).show();
                                                   }
                                               }
                                           });
                               }
                               else {
                                   String errormessege = task.getException().getMessage().toString();
                                   Toasty.error(SetupActivity.this, errormessege, Toasty.LENGTH_LONG).show();
                               }
                           }
                       });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
