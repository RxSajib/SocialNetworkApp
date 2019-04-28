package com.example.socialnetworkapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.Executor;

import javax.xml.validation.ValidatorHandler;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class FindFriendActivity extends AppCompatActivity {

    private Toolbar Mtoolbar;
    private RecyclerView Mrecylearview;
    private DatabaseReference Muserdatabase;
    private FirebaseAuth Mauth;
    private String CUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);

        Mauth = FirebaseAuth.getInstance();
        CUID = Mauth.getCurrentUser().getUid();
        Mtoolbar = findViewById(R.id.FToolbarID);
        Mrecylearview = findViewById(R.id.FRecylearID);
        Muserdatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        setSupportActionBar(Mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Find Friend");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_icon);

        Mrecylearview.setHasFixedSize(true);
        Mrecylearview.setLayoutManager(new LinearLayoutManager(FindFriendActivity.this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {


        FirebaseRecyclerAdapter<Fgetset, FindFriendHolver> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Fgetset, FindFriendHolver>(
                Fgetset.class,
                R.layout.find_friend_layout,
                FindFriendHolver.class,
                Muserdatabase
        ) {
            @Override
            protected void populateViewHolder(final FindFriendHolver viewHolder, Fgetset model, int position) {


               viewHolder.setnamefil(model.getFullname());
                viewHolder.setstatasfil(model.getStatas());
                viewHolder.setimagefil(model.getDownloadurl());

                final String UID = getRef(position).getKey();
                viewHolder.Mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(FindFriendActivity.this, FriendRequestActivity.class);
                        intent.putExtra("friendskey", UID);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        };

        Mrecylearview.setAdapter(firebaseRecyclerAdapter);
        super.onStart();
    }

    public static class FindFriendHolver extends RecyclerView.ViewHolder{

        private View Mview;
        private CircleImageView Fimage;
        private TextView Fname;
        private TextView Fstatas;
        private Context context;

        public FindFriendHolver(@NonNull View itemView) {
            super(itemView);

            Mview = itemView;
            Fimage = Mview.findViewById(R.id.FImageID);
            Fname = Mview.findViewById(R.id.FnameID);
            Fstatas = Mview.findViewById(R.id.FstatasID);
            context = itemView.getContext();
        }

        public void setimagefil(String img){
            Glide.with(context).load(img).into(Fimage);
        }

        public void setnamefil(String nam){
            Fname.setText(nam);
        }

        public void setstatasfil(String stat){
            Fstatas.setText(stat);
        }
    }
}


