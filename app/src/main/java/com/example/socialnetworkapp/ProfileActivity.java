package com.example.socialnetworkapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tapadoo.alerter.Alerter;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView Simage;
    private EditText Sname, Scountryname, Sgender;
    private TextView   Scalander;
    private EditText SRelanship, SIDname;
    private EditText Spost;
    private FloatingActionButton save;
    private DatabaseReference mpost;
    private FirebaseAuth Mauth;
    private String CurrentUserID;
    private DatePickerDialog datePickerDialog;
    private DatabaseReference Msavedatabase;
    private ProgressDialog Mprogress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        Mprogress = new ProgressDialog(ProfileActivity.this);
        Msavedatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        Mauth = FirebaseAuth.getInstance();
        mpost = FirebaseDatabase.getInstance().getReference().child("Users");
        CurrentUserID = Mauth.getCurrentUser().getUid();
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.M){
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        Simage = findViewById(R.id.SettingImageID);
        Sname = findViewById(R.id.nameID);
        Scountryname = findViewById(R.id.CountryID);
        Sgender = findViewById(R.id.GenderTextID);
        Scalander = findViewById(R.id.CalenderID);
        SRelanship = findViewById(R.id.RelationshipID);
        SIDname = findViewById(R.id.IDNameID);
        Spost = findViewById(R.id.PostDetailsID);
        save = findViewById(R.id.SsaveID);


        Scalander.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePicker datePicker =new DatePicker(ProfileActivity.this);
                int currentday = datePicker.getDayOfMonth();
                int currentmonth = (datePicker.getMonth())+1;
               int currentYear = datePicker.getYear();

                datePickerDialog = new DatePickerDialog(ProfileActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                                Scalander.setText(dayOfMonth+"-"+month+"-"+year);
                            }
                        }, currentYear, currentmonth, currentday);

                datePickerDialog.show();
            }
        });

        mpost.child(CurrentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){

                            if(dataSnapshot.hasChild("downloadurl")){
                                String imagestring = dataSnapshot.child("downloadurl").getValue().toString();
                                Glide.with(ProfileActivity.this).load(imagestring).placeholder(R.drawable.default_image).into(Simage);
                            }
                            else {

                            }

                            String nametext = dataSnapshot.child("fullname").getValue().toString();
                            Sname.setText(nametext);

                            if(dataSnapshot.hasChild("address")){
                                Scountryname.setVisibility(View.VISIBLE);
                                String addresstext = dataSnapshot.child("address").getValue().toString();
                                Scountryname.setText(addresstext);
                            }
                            else {

                            }


                            String gendertext = dataSnapshot.child("gender").getValue().toString();
                            Sgender.setText(gendertext);

                            String birth = dataSnapshot.child("dob").getValue().toString();
                            Scalander.setText(birth);

                            String relinship = dataSnapshot.child("relanship").getValue().toString();
                            SRelanship.setText(relinship);

                            String idname = dataSnapshot.child("name").getValue().toString();
                            SIDname.setText(idname);

                            String statas = dataSnapshot.child("statas").getValue().toString();
                            Spost.setText(statas);
                        }
                        else {
                            Toasty.error(ProfileActivity.this, "Users doesn't exists", Toasty.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fullnametext = Sname.getText().toString();
                String cuntrynametext = Scountryname.getText().toString();
                String gendertext = Sgender.getText().toString();
                String birthtext = Scalander.getText().toString();
                String relinshiptext = SRelanship.getText().toString();
                String IDnametext = SIDname.getText().toString();
                String posttext = Spost.getText().toString();

                if(fullnametext.isEmpty() || cuntrynametext.isEmpty() || gendertext.isEmpty() || birthtext.isEmpty() || relinshiptext.isEmpty() || IDnametext.isEmpty() || posttext.isEmpty()){

                    Alerter.create(ProfileActivity.this)
                            .setBackgroundColorRes(R.color.infoColor)
                            .setTitle("Info !!!")
                            .setText("Please input your all bio data")
                            .enableSwipeToDismiss()
                            .show();
                }
                else {

                    DatabaseReference Msabase = Msavedatabase.child(CurrentUserID);
                    Msabase.child("fullname").setValue(fullnametext);
                    Msabase.child("address").setValue(cuntrynametext);
                    Msabase.child("dob").setValue(birthtext);
                    Msabase.child("gender").setValue(gendertext);
                    Msabase.child("name").setValue(IDnametext);
                    Msabase.child("relanship").setValue(relinshiptext);
                    Msabase.child("statas").setValue(posttext);
                    Toasty.success(ProfileActivity.this, "profile info set success", Toasty.LENGTH_LONG).show();
                }


            }
        });
    }
}
