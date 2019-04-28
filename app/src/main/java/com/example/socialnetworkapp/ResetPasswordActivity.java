package com.example.socialnetworkapp;

import android.app.ProgressDialog;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import es.dmoral.toasty.Toasty;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText emailfil;
    private Button send;
    private ImageButton backbtn;
    private FirebaseAuth Mauth;
    private ProgressDialog Mprogress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        Mprogress = new ProgressDialog(ResetPasswordActivity.this);
        Mauth = FirebaseAuth.getInstance();
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.M){
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        emailfil = findViewById(R.id.REmailID);
        send = findViewById(R.id.RSendID);
        backbtn = findViewById(R.id.backID);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Mprogress.setTitle("Resetting Password");
                Mprogress.setMessage("Please wait we are checking your password");
                Mprogress.setCanceledOnTouchOutside(false);
                Mprogress.show();
                final String emailtext = emailfil.getText().toString();

                if(emailtext.isEmpty()){
                    Mprogress.dismiss();
                    //Toast., Toast.LENGTH_SHORT).show();
                    Toasty.error(ResetPasswordActivity.this, "Please enter your email first", Toasty.LENGTH_LONG).show();
                }
                else {


                    Mauth.sendPasswordResetEmail(emailtext).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                Toasty.success(ResetPasswordActivity.this, emailtext+" check your email for reset your password", Toasty.LENGTH_LONG).show();
                                Mprogress.dismiss();
                                finish();
                            }
                            else {
                                Mprogress.dismiss();
                                String errormessege = task.getException().getMessage().toString();
                                Toasty.error(ResetPasswordActivity.this, errormessege, Toasty.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
