package com.example.socialnetworkapp;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserPostAcitvity extends AppCompatActivity {

    private Toolbar userpostToolbar;
    private DatabaseReference Muserdatabase;
    private FirebaseAuth Mauth;
    private String CurrentUser;
    private RecyclerView Userpost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_post_acitvity);

        userpostToolbar = findViewById(R.id.UserPostToolbarID);
        setSupportActionBar(userpostToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_icon);


        Muserdatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        Mauth = FirebaseAuth.getInstance();
        CurrentUser = Mauth.getCurrentUser().getUid();
        Userpost = findViewById(R.id.UserPosViewID);
        Userpost.setHasFixedSize(true);
        Userpost.setLayoutManager(new LinearLayoutManager(UserPostAcitvity.this));


        Muserdatabase.child(CurrentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    String profilename = dataSnapshot.child("fullname").getValue().toString();
                    getSupportActionBar().setTitle(profilename+" 's"+" Posts");
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
