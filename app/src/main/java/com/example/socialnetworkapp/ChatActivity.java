package com.example.socialnetworkapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

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
    private String friendsid;
    private DatabaseReference FriendsDatabase;
    private DatabaseReference Roodref;
    private String CurrentDate, CurrentTime;
    private RecyclerView MessegeView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        Roodref = FirebaseDatabase.getInstance().getReference();
        Mauth = FirebaseAuth.getInstance();
        CurrentuserID = Mauth.getCurrentUser().getUid();
        chattoolbar = findViewById(R.id.ChatToolbarID);
        setSupportActionBar(chattoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        friendsid = getIntent().getStringExtra("sendchatid").toString();
        FriendsDatabase = FirebaseDatabase.getInstance().getReference().child(friendsid);

        messegetext = findViewById(R.id.PickMessegeID);
        sendbutton = findViewById(R.id.SendID);
        currentimage = findViewById(R.id.CurrentImageID);
        currentname = findViewById(R.id.CurrentTextID);
        MuserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        MessegeView = findViewById(R.id.MessegeViewID);
        MessegeView.setHasFixedSize(true);
        MessegeView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));


        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String messege = messegetext.getText().toString();

                if(messege.isEmpty()){
                    Toasty.error(ChatActivity.this, "Enter your message first", Toasty.LENGTH_LONG).show();
                }
                else {

                    messegetext.setText(null);
                    String messegesenderref = "Message/"+CurrentuserID+"/"+friendsid;
                    String messegereciverref = "Message/"+friendsid+"/"+CurrentuserID;

                    DatabaseReference user_messegekey = Roodref.child("Messege").child(messegesenderref).child(messegereciverref).push();
                    String messegepushID = user_messegekey.getKey();


                    ///current date
                    Calendar calendardate = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
                    CurrentDate = simpleDateFormat.format(calendardate.getTime());

                    ///current time
                    Calendar calendartime = Calendar.getInstance();
                    SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm a");
                    CurrentTime = simpleTimeFormat.format(calendartime.getTime());


                    Map messegetextbody = new HashMap();
                    messegetextbody.put("message", messege);
                    messegetextbody.put("date", CurrentDate);
                    messegetextbody.put("time", CurrentTime);
                    messegetextbody.put("from", CurrentuserID);
                    messegetextbody.put("type", "text");

                    Map messegebody_details = new HashMap();
                    messegebody_details.put(messegesenderref+"/"+messegepushID,messegetextbody);
                    messegebody_details.put(messegereciverref+"/"+messegepushID,messegetextbody);

                    Roodref.updateChildren(messegebody_details).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {

                            if(task.isSuccessful()){

                            }
                            else {
                                String errormessege = task.getException().getMessage();
                                Toasty.error(ChatActivity.this, errormessege, Toasty.LENGTH_LONG).show();
                            }
                        }
                    });

                }
            }
        });


        MuserDatabase.child(friendsid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    if(dataSnapshot.hasChild("downloadurl")){
                        String imagepath = dataSnapshot.child("downloadurl").getValue().toString();
                        Glide.with(ChatActivity.this).load(imagepath).placeholder(R.drawable.default_image).into(currentimage);
                    }
                    else {

                    }

                    if(dataSnapshot.hasChild("name")){
                        String namedata = dataSnapshot.child("name").getValue().toString();
                        currentname.setText(namedata);
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
