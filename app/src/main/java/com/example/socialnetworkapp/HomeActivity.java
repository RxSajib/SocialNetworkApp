package com.example.socialnetworkapp;

import android.content.Context;
import android.content.Intent;
import android.provider.Contacts;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ConcurrentModificationException;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout Mdrawer;
    private NavigationView Mnaview;
    private Toolbar hometoolbar;
    private FirebaseAuth Mauth;
    private DatabaseReference MuserDatabase;
    private DatabaseReference MuserDatabasenavagation;
    private FirebaseAuth MuserAuth;
    private String CurrentUser;
    private DatabaseReference Mpostdes;
    private DatabaseReference Mlike;
    private Boolean likesval = false;

    //for navagation view
    private CircleImageView mimageview;
    private TextView mfullnametext;
    private RecyclerView MrecylerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        Mlike = FirebaseDatabase.getInstance().getReference().child("likes");
        Mpostdes = FirebaseDatabase.getInstance().getReference().child("Posts");
        MrecylerView = findViewById(R.id.HomeRecylearID);
        MrecylerView.setHasFixedSize(true);
        MrecylerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
        MuserAuth = FirebaseAuth.getInstance();
        MuserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        Mauth = FirebaseAuth.getInstance();
        Mdrawer = findViewById(R.id.HomeDrawerID);
        Mnaview = findViewById(R.id.HomeNavagactionID);
        hometoolbar = findViewById(R.id.HometoolbarID);
        setSupportActionBar(hometoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.nav_icon);
        getSupportActionBar().setTitle("Home");

        Mnaview.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if(menuItem.getItemId() == R.id.AddnewPostID){
                    Mdrawer.closeDrawer(Gravity.START);
                    Intent intent = new Intent(HomeActivity.this, AddPostActivity.class);
                    startActivity(intent);
                }

                if(menuItem.getItemId() == R.id.ProfileID){
                    menuItem.setChecked(true);
                    Mdrawer.closeDrawer(Gravity.START);
                    Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                    startActivity(intent);
                }
                if(menuItem.getItemId() == R.id.HomeID){
                    menuItem.setChecked(true);
                    Mdrawer.closeDrawer(Gravity.START);
                }

                if(menuItem.getItemId() == R.id.SettingsID){
                    menuItem.setCheckable(true);
                    Mdrawer.closeDrawer(Gravity.START);
                    Intent intent = new Intent(HomeActivity.this, SetupActivity.class);
                    startActivity(intent);
                }

                if(menuItem.getItemId() == R.id.LogoutID){
                    Mauth.signOut();
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }

                if(menuItem.getItemId() == R.id.FindFriendsID){
                    menuItem.setCheckable(true);
                    Mdrawer.closeDrawer(Gravity.START);
                    Intent intent = new Intent(HomeActivity.this, FindFriendActivity.class);
                    startActivity(intent);
                }

                if(menuItem.getItemId() == R.id.FriendsID){
                    menuItem.setCheckable(true);
                    Mdrawer.closeDrawer(Gravity.START);
                    Intent intent = new Intent(HomeActivity.this, FriendActivity.class);
                    startActivity(intent);
                }

                return false;
            }
        });

        View MnavView = Mnaview.inflateHeaderView(R.layout.header_layout);
         mimageview = MnavView.findViewById(R.id.NavImageID);
         mfullnametext = MnavView.findViewById(R.id.NavnameID);
        CurrentUser = MuserAuth.getCurrentUser().getUid();

       MuserDatabasenavagation = FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentUser);

        MuserDatabasenavagation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists()) {

                    if(dataSnapshot.hasChild("downloadurl")){
                        String profileimage = dataSnapshot.child("downloadurl").getValue().toString();String fullname = dataSnapshot.child("fullname").getValue().toString();
                        Glide.with(HomeActivity.this).load(profileimage).placeholder(R.drawable.default_image).into(mimageview);
                    }
                    else {

                    }

                    if(dataSnapshot.hasChild("fullname")) {
                        String fullnametext = dataSnapshot.child("fullname").getValue().toString();
                        mfullnametext.setText(fullnametext);
                    }
                    else {

                    }

                }
                else {

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
            Mdrawer.openDrawer(Gravity.START);
        }

        if(item.getItemId() == R.id.AddID){
            Intent intent = new Intent(HomeActivity.this, AddPostActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {

        FirebaseUser Muser = Mauth.getCurrentUser();
        if(Muser == null){
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        else {

           final String currentuserID = Mauth.getCurrentUser().getUid().toString();
            MuserDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(!dataSnapshot.hasChild(currentuserID)){

                        Intent intent = new Intent(HomeActivity.this, SetupActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



        }

       FirebaseRecyclerAdapter<PostGetset, HomeVieHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<PostGetset, HomeVieHolder>(
               PostGetset.class,
               R.layout.post_sample_layout,
               HomeVieHolder.class,
               Mpostdes
       ) {
           @Override
           protected void populateViewHolder(final HomeVieHolder viewHolder, PostGetset model, final int position) {

               final String UID = getRef(position).getKey();

               viewHolder.setlikebuttonstatas(UID);

               Mpostdes.child(UID).addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot) {
                       if(dataSnapshot.exists()){

                           String name = dataSnapshot.child("fullname").getValue().toString();
                           viewHolder.setfullnamefil(name);

                           String date = dataSnapshot.child("date").getValue().toString();
                           String time = dataSnapshot.child("time").getValue().toString();
                           String descptrion = dataSnapshot.child("descptrion").getValue().toString();

                           viewHolder.settimefill(time);
                           viewHolder.setdatefill(date);
                           viewHolder.setdescptrionfill(descptrion);

                           if(dataSnapshot.hasChild("profileimage")){
                               String proimage = dataSnapshot.child("profileimage").getValue().toString();
                               viewHolder.setProfileimagefill(proimage);
                           }
                           else {

                           }

                           if(dataSnapshot.hasChild("postimage")){
                               String postimage = dataSnapshot.child("postimage").getValue().toString();
                               viewHolder.setpostimagefil(postimage);
                           }
                           else {

                           }

                           final String PoID = getRef(position).getKey();
                           viewHolder.Mview.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {

                                   Intent intent = new Intent(HomeActivity.this, ModiFyActivity.class);
                                   intent.putExtra("Key", PoID);
                                   startActivity(intent);
                               }
                           });
                       }
                       else {

                       }
                   }



                   @Override
                   public void onCancelled(DatabaseError databaseError) {

                   }
               });

               viewHolder.like.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                        likesval = true;


                        Mlike.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if(likesval.equals(true)){
                                    if(dataSnapshot.child(UID).hasChild(CurrentUser)){

                                        Mlike.child(UID).child(CurrentUser).removeValue();
                                        likesval = false;
                                    }
                                    else {
                                        Mlike.child(UID).child(CurrentUser).setValue(true);
                                        likesval = false;
                                    }
                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {


                            }
                        });
                   }
               });
           }
       };

        MrecylerView.setAdapter(firebaseRecyclerAdapter);
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }


    public  static  class HomeVieHolder extends RecyclerView.ViewHolder {

        private View Mview;
        private CircleImageView profileimage;
        private TextView fullname;
        private TextView date;
        private TextView time;
        private TextView descptrion;
        private ImageView postimage;
        private ImageButton like;
        private Context context;
        private TextView likcount;
        private ImageView comment;

        int countlike;
        String currentuserID;
        DatabaseReference likedatabase;

        public HomeVieHolder(@NonNull View itemView) {
            super(itemView);

            Mview = itemView;
            profileimage = Mview.findViewById(R.id.SPostImageID);
            fullname = Mview.findViewById(R.id.SPostnameID);
            date = Mview.findViewById(R.id.CpostDateID);
            time = Mview.findViewById(R.id.CpostTimeID);
            descptrion = Mview.findViewById(R.id.CPostDetailsID);
            like = Mview.findViewById(R.id.LovePIC);
            postimage = Mview.findViewById(R.id.CPostImageID);
            context = itemView.getContext();
            likcount = Mview.findViewById(R.id.LikeCountTextID);
            comment = Mview.findViewById(R.id.CommentID);

            likedatabase = FirebaseDatabase.getInstance().getReference().child("likes");
            currentuserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        public void setlikebuttonstatas(final String UID){

            likedatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.child(UID).hasChild(currentuserID)){
                        countlike = (int) dataSnapshot.child(UID).getChildrenCount();
                      //  like.setImageResource(R.drawable.redlike);
                        likcount.setText(Integer.toString(countlike)+" Like");
                    }
                    else {
                        countlike = (int) dataSnapshot.child(UID).getChildrenCount();
                  //      like.setImageResource(R.drawable.like);
                        likcount.setText(Integer.toString(countlike)+" Like");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void setfullnamefil (String fullnametext){
            fullname.setText(fullnametext);
        }

        public void setdatefill(String datetext){
            date.setText(datetext);
        }

        public void settimefill (String timeetext){
            time.setText(timeetext);
        }

        public void setdescptrionfill(String desctext){
            descptrion.setText(desctext);
        }

        public void setProfileimagefill(String profileimagetext){
            Glide.with(context).load(profileimagetext).placeholder(R.drawable.default_image).into(profileimage);
        }

        public void setpostimagefil(String postimg){
            Glide.with(context).load(postimg).placeholder(R.drawable.waiting).into(postimage);
        }

    }
}
