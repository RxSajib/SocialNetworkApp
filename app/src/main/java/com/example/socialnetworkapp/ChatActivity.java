package com.example.socialnetworkapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class ChatActivity extends AppCompatActivity {

    private Toolbar chattoolbar;
    private EditText messegetext;
    private ImageView sendbutton;
    private CircleImageView currentimage;
    private TextView currentname;
    private DatabaseReference MuserDatabase;
    private FirebaseAuth Mauth;
    private String CurrentuserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Mauth = FirebaseAuth.getInstance();
        CurrentuserID = Mauth.getCurrentUser().getUid();
        chattoolbar = findViewById(R.id.ChatToolbarID);
        setSupportActionBar(chattoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        messegetext = findViewById(R.id.PickMessegeID);
        sendbutton = findViewById(R.id.SendID);
        currentimage = findViewById(R.id.CurrentImageID);
        currentname = findViewById(R.id.CurrentTextID);
        MuserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentuserID);


        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String messege = messegetext.getText().toString();

                if(messege.isEmpty()){
                    Toasty.error(ChatActivity.this, "Enter your message first", Toasty.LENGTH_LONG).show();
                }
            }
        });


        MuserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    if(dataSnapshot.hasChild("downloadurl")){
                        String imageuri = dataSnapshot.child("downloadurl").getValue().toString();
                        Glide.with(ChatActivity.this).load(imageuri).into(currentimage);
                    }
                    else {

                    }

                    if(dataSnapshot.hasChild("name")){

                        String nametext = dataSnapshot.child("name").getValue().toString();
                        currentname.setText(nametext);
                    }
                    else {

                    }

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
}
