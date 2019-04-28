package com.example.socialnetworkapp;

import android.os.Build;
import android.service.autofill.SaveRequest;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class FriendRequestActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CircleImageView Rimage;
    private Button request, cancel;
    private TextView countryname, Gender, birth, relanship;
    private TextView idname, statas, fullname;
    private String friendsID;
    private DatabaseReference Muserdatabase;
    private String Current_state;
    private String CurrentUserID;
    private FirebaseAuth Mauth;
    private DatabaseReference FriendRequestRef;
    private DatabaseReference Friends;
    private String CurrentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);



        Friends = FirebaseDatabase.getInstance().getReference().child("Friends");
        FriendRequestRef = FirebaseDatabase.getInstance().getReference().child("FriendResqest");
        Mauth = FirebaseAuth.getInstance();
        CurrentUserID = Mauth.getCurrentUser().getUid();
        Rimage = findViewById(R.id.RequestImageID);
      toolbar = findViewById(R.id.RequestToolbarID);
      setSupportActionBar(toolbar);
      getSupportActionBar().setTitle("Friend");
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_icon);
      friendsID = getIntent().getStringExtra("friendskey");
        Current_state = "not_friends";

      Muserdatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(friendsID);
      cancel = findViewById(R.id.ReguestRemoveButtonID);
      request = findViewById(R.id.ReguestAccepectButtonID);
      countryname = findViewById(R.id.CountryTextID);
      Gender = findViewById(R.id.GenderID);
      birth = findViewById(R.id.DateofburthID);
      relanship = findViewById(R.id.relanshipID);
      idname = findViewById(R.id.IDnametextID);
      statas = findViewById(R.id.statasID);
      fullname = findViewById(R.id.ReguestnameID);

      maintainRequestButton();

      Muserdatabase.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {

              if(dataSnapshot.exists()){

                  String fullnametext = dataSnapshot.child("fullname").getValue().toString();
                  fullname.setText(fullnametext);

                  if(dataSnapshot.hasChild("downloadurl")){
                      String imageuri = dataSnapshot.child("downloadurl").getValue().toString();
                      Glide.with(FriendRequestActivity.this).load(imageuri).placeholder(R.drawable.default_image).into(Rimage);
                  }
                  else {

                  }

                  String country = dataSnapshot.child("address").getValue().toString();
                  countryname.setText(country);

                  String gendertex = dataSnapshot.child("gender").getValue().toString();
                  Gender.setText(gendertex);

                  String datetetx = dataSnapshot.child("dob").getValue().toString();
                  birth.setText(datetetx);

                  String relantext = dataSnapshot.child("relanship").getValue().toString();
                  relanship.setText(relantext);

                  String id = dataSnapshot.child("name").getValue().toString();
                  idname.setText(id);

                  String statastext = dataSnapshot.child("statas").getValue().toString();
                  statas.setText(statastext);

                  }
              else {
                  Toasty.info(FriendRequestActivity.this, "No data found", Toasty.LENGTH_LONG).show();
              }
          }

          @Override
          public void onCancelled(DatabaseError databaseError) {

          }
      });


      cancel.setVisibility(View.INVISIBLE);
      cancel.setEnabled(false);


        if (!CurrentUserID.equals(friendsID)) {

            request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    request.setEnabled(false);

                    if(Current_state.equals("not_friends")){

                        sendFriendrequest();
                    }

                    if(Current_state.equals("request_send")){

                        CancelRequest();
                    }

                    if(Current_state.equals("request_recived")){

                        accepectfrind_request();
                    }

                    if(Current_state.equals("friends")){
                        unfriend_friends();
                    }
                }
            });
        }
        else {
            request.setVisibility(View.INVISIBLE);
            cancel.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    ///Accepect Friend request
    public void accepectfrind_request(){

        Calendar calendardate = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
        CurrentDate = simpleDateFormat.format(calendardate.getTime());

        Friends.child(CurrentUserID).child(friendsID).child("date").setValue(CurrentDate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            Friends.child(friendsID).child(CurrentUserID).child("data").setValue(CurrentDate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){


                                                FriendRequestRef.child(CurrentUserID).child(friendsID)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if(task.isSuccessful()){

                                                                    FriendRequestRef.child(friendsID).child(CurrentUserID)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                                    request.setEnabled(true);
                                                                                    Current_state = "friends";
                                                                                    request.setText("Unfriend");

                                                                                    cancel.setVisibility(View.INVISIBLE);
                                                                                    cancel.setEnabled(false);
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });

                                                ///end of code
                                            }
                                        }
                                    });
                        }
                    }
                });
    }


    ///send friend request
    public void sendFriendrequest(){

        FriendRequestRef.child(CurrentUserID).child(friendsID)
                .child("request_type").setValue("send")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            FriendRequestRef.child(friendsID).child(CurrentUserID)
                                    .child("request_type").setValue("recived")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){
                                                request.setEnabled(true);
                                                Current_state = "request_send";
                                                request.setText("Cancel");
                                                request.setBackgroundResource(R.drawable.cancel_request_desian);

                                                cancel.setVisibility(View.INVISIBLE);
                                                cancel.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    ///maintain requestbuton
    public void maintainRequestButton(){
        FriendRequestRef.child(CurrentUserID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(friendsID)){
                            String datatype = dataSnapshot.child(friendsID).child("request_type").getValue().toString();

                            if(datatype.equals("send")){
                                Current_state = "request_send";
                                request.setText("Cancel");
                                request.setBackgroundResource(R.drawable.cancel_request_desian);
                                cancel.setVisibility(View.INVISIBLE);
                                cancel.setEnabled(false);
                            }

                            else if(datatype.equals("recived")){

                                Current_state = "request_recived";
                                request.setText("Accepect");
                                cancel.setVisibility(View.VISIBLE);
                                cancel.setEnabled(true);

                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        CancelRequest();
                                    }
                                });
                            }
                        }

                        else {

                            Friends.child(friendsID)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            if(dataSnapshot.hasChild(CurrentUserID)){
                                                Current_state = "friends";
                                                request.setText("Unfriend");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    ///unfriend request
    public void unfriend_friends(){

        Friends.child(friendsID).child(CurrentUserID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            Friends.child(CurrentUserID).child(friendsID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){

                                                request.setEnabled(true);
                                                Current_state = "not_friends";
                                                request.setText("Reguest");

                                                cancel.setVisibility(View.INVISIBLE);
                                                cancel.setEnabled(false);

                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    ///cancel request
    public void CancelRequest(){

        FriendRequestRef.child(CurrentUserID).child(friendsID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            FriendRequestRef.child(friendsID).child(CurrentUserID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            request.setEnabled(true);
                                            Current_state = "not_friends";
                                            request.setText("Reguest");

                                            cancel.setVisibility(View.INVISIBLE);
                                            cancel.setEnabled(false);
                                        }
                                    });
                        }
                    }
                });
    }
}
