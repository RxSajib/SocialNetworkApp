package com.example.socialnetworkapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class AddPostActivity extends AppCompatActivity {

    private Toolbar AddToolbar;
    private ImageView postimage;
    private EditText postmessege;
    private Button postbutton;
    private Uri imageuri = null;
    private StorageReference Mpostimage;
    private  String Currenttime, CurrentDate;
    private String RandomID;
    private DatabaseReference Muserdatabase;
    private FirebaseAuth Mauth;
    private String CurrentUserID;
    private String DownloadpostImage;
    private DatabaseReference Mpostdatabase;
    private ProgressDialog Mprogress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        Mprogress = new ProgressDialog(AddPostActivity.this);
        Mpostdatabase = FirebaseDatabase.getInstance().getReference().child("Posts");
        Mauth = FirebaseAuth.getInstance();
        Muserdatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        CurrentUserID = Mauth.getCurrentUser().getUid();
        Mpostimage = FirebaseStorage.getInstance().getReference();
        AddToolbar = findViewById(R.id.AddPostToolbarID);
        setSupportActionBar(AddToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Post");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_icon);

        postbutton = findViewById(R.id.PostButtonID);
        postmessege = findViewById(R.id.PostMessegeID);
        postimage = findViewById(R.id.PostImageID);

        postimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(AddPostActivity.this);
            }
        });

        postbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String messegetext = postmessege.getText().toString();
                if(messegetext.isEmpty() || imageuri == null){
                    Alerter.create(AddPostActivity.this)
                            .enableSwipeToDismiss()
                            .setTitle("Follow me !")
                            .setText("Please select post image and input your message")
                            .setBackgroundColorRes(R.color.alarter_colour)
                            .show();
                }
                else{
                    Mprogress.setTitle("Post is uploading ...");
                    Mprogress.setMessage("Please wait we are updating your info");
                    Mprogress.setCanceledOnTouchOutside(false);
                    Mprogress.show();
                    ///date
                    Calendar timecalender = Calendar.getInstance();
                    SimpleDateFormat simpletimeformat = new SimpleDateFormat("dd-MMMM-yyyy");
                    CurrentDate = simpletimeformat.format(timecalender.getTime());

                    ///time
                    Calendar datecalnder = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm a");
                    Currenttime = simpleDateFormat.format(datecalnder.getTime());

                    RandomID = CurrentDate+Currenttime;

                   StorageReference mpath = Mpostimage.child("post_image").child(imageuri.getLastPathSegment()+RandomID+".jpg");
                   mpath.putFile(imageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                       @Override
                       public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {

                           if(task.isSuccessful()){

                               DownloadpostImage = task.getResult().getDownloadUrl().toString();
                                Muserdatabase.child(CurrentUserID)
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                if(dataSnapshot.exists()){

                                                    String fullname = dataSnapshot.child("fullname").getValue().toString();
                                                    String profileimage = dataSnapshot.child("downloadurl").getValue().toString();

                                                    HashMap<String, String> postmap = new HashMap<>();
                                                    postmap.put("uid", CurrentUserID);
                                                    postmap.put("date", CurrentDate);
                                                    postmap.put("time", Currenttime);
                                                    postmap.put("descptrion", messegetext);
                                                    postmap.put("postimage", DownloadpostImage);
                                                    postmap.put("profileimage", profileimage);
                                                    postmap.put("fullname", fullname);


                                                    Mpostdatabase.child(RandomID+CurrentUserID)
                                                            .setValue(postmap)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if(task.isSuccessful()){
                                                                        Mprogress.dismiss();
                                                                        Toasty.success(AddPostActivity.this, "Post is updated", Toasty.LENGTH_LONG).show();
                                                                    }
                                                                    else {
                                                                        Mprogress.dismiss();
                                                                        String errormessege= task.getException().getMessage().toString();
                                                                        Toasty.error(AddPostActivity.this, errormessege, Toasty.LENGTH_LONG).show();
                                                                        Intent intent = new Intent(AddPostActivity.this, HomeActivity.class);
                                                                        startActivity(intent);
                                                                    }
                                                                }
                                                            });
                                                }
                                                else {
                                                    Mprogress.dismiss();
                                                    String errormessege = task.getException().getMessage();
                                                    Toasty.error(AddPostActivity.this, "Please setup your account", Toasty.LENGTH_LONG).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                           }
                           else {
                               String errormessege = task.getException().getMessage().toString();
                               Toasty.error(AddPostActivity.this, errormessege, Toasty.LENGTH_LONG).show();
                           }
                       }
                   });
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
               imageuri = result.getUri();
               postimage.setImageURI(imageuri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
