package com.example.socialnetworkapp;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import es.dmoral.toasty.Toasty;

public class ModiFyActivity extends AppCompatActivity {


    private ImageView Mpostimage;
    private TextView Mdetailstext;
    private TextView Mtime;
    private TextView Mdate;
    private String postUID;
    private TextView Mname;
    private DatabaseReference Mpostdatabase;
    private FirebaseAuth Mauth;
    private String CurrentuserID;

    private FloatingActionButton Edit;
    private Button Remove;
    private DatabaseReference MpostDatabase;
    private DatabaseReference MpostEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modi_fy);

        MpostEdit = FirebaseDatabase.getInstance().getReference().child("Posts");
        Mpostdatabase = FirebaseDatabase.getInstance().getReference().child("Posts");
        Mauth = FirebaseAuth.getInstance();
        CurrentuserID = Mauth.getCurrentUser().getUid();
        Mpostdatabase = FirebaseDatabase.getInstance().getReference().child("Posts");
        postUID = getIntent().getStringExtra("Key");
        Edit = findViewById(R.id.EditButtonID);
        Remove = findViewById(R.id.RemvoeButtonID);

        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.M){
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        Mpostimage = findViewById(R.id.ModifyImageID);
        Mdetailstext = findViewById(R.id.ModifyPostID);
        Mtime = findViewById(R.id.MtimeID);
        Mdate = findViewById(R.id.ModifydateID);
        Mname = findViewById(R.id.MnameID);


    }

    @Override
    protected void onStart() {
        super.onStart();

        Mpostdatabase.child(postUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    String name = dataSnapshot.child("fullname").getValue().toString();
                    String details = dataSnapshot.child("descptrion").getValue().toString();
                    String time = dataSnapshot.child("time").getValue().toString();
                    String date = dataSnapshot.child("date").getValue().toString();
                    String UID = dataSnapshot.child("uid").getValue().toString();

                    Mdetailstext.setText(details);
                    Mtime.setText(time);
                    Mdate.setText(date);
                    Mname.setText(name);

                    if(dataSnapshot.hasChild("postimage")){
                        String postimage = dataSnapshot.child("postimage").getValue().toString();
                        Glide.with(ModiFyActivity.this).load(postimage).placeholder(R.drawable.waiting).into(Mpostimage);
                    }

                    Remove.setVisibility(View.INVISIBLE);
                    Edit.setVisibility(View.INVISIBLE);

                    if(CurrentuserID.equals(UID)){
                        Remove.setVisibility(View.VISIBLE);
                        Edit.setVisibility(View.VISIBLE);

                        startingDeleting();
                        startEditing();
                    }


                }
                else {
                    Toasty.error(ModiFyActivity.this, "somethings is error", Toasty.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void startingDeleting(){

       Remove.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Mpostdatabase.child(postUID)
                       .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       Toasty.info(ModiFyActivity.this, "Data is remove", Toasty.LENGTH_LONG).show();
                       Intent intent = new Intent(ModiFyActivity.this, HomeActivity.class);
                       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                       startActivity(intent);
                       finish();
                   }
               });
           }
       });
    }


    public void startEditing(){

        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder Mdoilog = new AlertDialog.Builder(ModiFyActivity.this);
                View Mview = getLayoutInflater().inflate(R.layout.costomedittext_desian, null);

                final ImageView imageView = Mview.findViewById(R.id.ComtomEditID);
                final TextView nametext = Mview.findViewById(R.id.ComstionEditnameID);
                final EditText desedittext = Mview.findViewById(R.id.CostonPostID);
                final Button savebutton = Mview.findViewById(R.id.CostionPostSaveButtonID);

                MpostEdit.child(postUID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){

                            String name = dataSnapshot.child("fullname").getValue().toString();
                            String postdes = dataSnapshot.child("descptrion").getValue().toString();

                            if(dataSnapshot.hasChild("postimage")){
                                String postimage = dataSnapshot.child("postimage").getValue().toString();
                                Glide.with(ModiFyActivity.this).load(postimage).placeholder(R.drawable.waiting).into(imageView);
                            }
                            else {

                            }
                            nametext.setText(name);
                            desedittext.setText(postdes);


                            savebutton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    String destext = desedittext.getText().toString();
                                    if(destext.isEmpty()){
                                        Toasty.error(ModiFyActivity.this, "Details must needed", Toasty.LENGTH_LONG).show();
                                    }
                                    else {
                                        DatabaseReference mdatabase = MpostEdit.child(postUID);
                                        mdatabase.child("descptrion").setValue(destext).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if(task.isSuccessful()){
                                                    Toasty.success(ModiFyActivity.this, "Updated", Toasty.LENGTH_LONG).show();
                                                    finish();
                                                }
                                            }
                                        }) ;
                                    }


                                }
                            });
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                Mdoilog.setView(Mview);
                AlertDialog alertDialog = Mdoilog.create();
                alertDialog.show();
            }
        });
    }
}
