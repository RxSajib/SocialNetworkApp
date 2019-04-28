package com.example.socialnetworkapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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

import javax.xml.validation.ValidatorHandler;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendActivity extends AppCompatActivity {

    private Toolbar friendstoolbar;
    private RecyclerView friendsrecylearvew;
    private DatabaseReference Mfriends;
    private String CurrentUserID;
    private FirebaseAuth Mauth;
    private DatabaseReference Muserdatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        Mauth = FirebaseAuth.getInstance();
        CurrentUserID = Mauth.getCurrentUser().getUid();
        friendstoolbar = findViewById(R.id.FriendToolbarID);
        friendsrecylearvew = findViewById(R.id.FriendsRecylerID);
        Mfriends = FirebaseDatabase.getInstance().getReference().child("Friends").child(CurrentUserID);


        Muserdatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        setSupportActionBar(friendstoolbar);
        getSupportActionBar().setTitle("Friends List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        friendsrecylearvew.setHasFixedSize(true);
        friendsrecylearvew.setLayoutManager(new LinearLayoutManager(FriendActivity.this));

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

        FirebaseRecyclerAdapter<FriendsgetSet, FriendsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<FriendsgetSet, FriendsViewHolder>(
                FriendsgetSet.class,
                R.layout.friend_list_layout,
                FriendsViewHolder.class,
                Mfriends
        ) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder viewHolder, FriendsgetSet model, int position) {

                final String uid = getRef(position).getKey();

                Muserdatabase.child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){

                            final String name = dataSnapshot.child("fullname").getValue().toString();
                            viewHolder.setnamefil(name);

                            String stat = dataSnapshot.child("statas").getValue().toString();
                            viewHolder.setstatasfil(stat);

                            if(dataSnapshot.hasChild("downloadurl")){

                                String image = dataSnapshot.child("downloadurl").getValue().toString();
                                viewHolder.setimagefil(image);
                            }
                            else {

                            }



                            viewHolder.Mview.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    CharSequence option[] = new CharSequence[]
                                            {

                                                    name+"'s profile",
                                                    "Send Messege"

                                            };

                                    AlertDialog.Builder mbuilder = new AlertDialog.Builder(FriendActivity.this);
                                    mbuilder.setTitle("Select Options");

                                    mbuilder.setItems(option, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            if(which == 0){


                                            }

                                            if(which == 1){
                                                Intent intent = new Intent(FriendActivity.this, ChatActivity.class);
                                                intent.putExtra("sendchatid", uid);
                                                startActivity(intent);
                                            }
                                        }
                                    });

                                    mbuilder.show();

                                }
                            });


                        }
                        else{

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

        friendsrecylearvew.setAdapter(firebaseRecyclerAdapter);
        super.onStart();
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        private View Mview;
        private CircleImageView imageView;
        private TextView nametext, statastext;
        private Context context;

        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);


            context = itemView.getContext();
            Mview = itemView;
            imageView = Mview.findViewById(R.id.FriendsImageID);
            nametext = Mview.findViewById(R.id.FriendNameID);
            statastext = Mview.findViewById(R.id.FriendStatasID);
        }

        public void setimagefil(String img){
            Glide.with(context).load(img).placeholder(R.drawable.default_image).into(imageView);
        }

        public void setnamefil(String nam){
            nametext.setText(nam);
        }

        public void setstatasfil (String stat){
            statastext.setText(stat);
        }
    }
}
