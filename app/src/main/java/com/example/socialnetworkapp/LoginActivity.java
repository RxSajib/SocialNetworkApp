package com.example.socialnetworkapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tapadoo.alerter.Alerter;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {

    private TextView welcomeText;
    private Button fbButton;
    private Button googleButton;
    private EditText email;
    private EditText password;
    private Button loginButton;
    private TextView forgetpassword;
    private Button registerButton;
    private FirebaseAuth Mauth;
    private ProgressDialog Mprogress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        Mprogress = new ProgressDialog(LoginActivity.this);
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.M){
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        Mauth = FirebaseAuth.getInstance();
        welcomeText = findViewById(R.id.WelcomeTextID);
        fbButton = findViewById(R.id.FacebookButtonID);
        googleButton = findViewById(R.id.GoogleButtonID);
        email = findViewById(R.id.EmailID);
        password = findViewById(R.id.PasswordID);
        loginButton = findViewById(R.id.LoginButtonID);
        forgetpassword = findViewById(R.id.ForgotTextID);
        registerButton = findViewById(R.id.RegisterNowID);

        forgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailtext = email.getText().toString();
                String passwordtext = password.getText().toString();

                if(emailtext.isEmpty() || passwordtext.isEmpty()){
                    Alerter.create(LoginActivity.this)
                            .setTitle("Follow Me !")
                            .setText("Please Input Your Email and Password")
                            .setBackgroundColorRes(R.color.alarter_colour)
                            .enableSwipeToDismiss()
                            .show();
                }
                else {

                    Mprogress.setTitle("Task is ongoing");
                    Mprogress.setMessage("Please wait we are checking your account");
                    Mprogress.setCanceledOnTouchOutside(false);
                    Mprogress.show();
                    Mauth.signInWithEmailAndPassword(emailtext, passwordtext)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if(task.isSuccessful()){

                                        Toasty.success(LoginActivity.this, "Login Success", Toasty.LENGTH_LONG).show();
                                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();
                                        Mprogress.dismiss();
                                    }
                                    else {
                                        String errormessege = task.getException().getMessage().toString();
                                        Toasty.error(LoginActivity.this, errormessege, Toasty.LENGTH_LONG).show();
                                        Mprogress.dismiss();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Mprogress.dismiss();
                            String errormessege = e.getMessage().toString();
                            Toasty.error(LoginActivity.this, errormessege, Toasty.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser Muser = Mauth.getCurrentUser();
        if(Muser != null){
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }
}
