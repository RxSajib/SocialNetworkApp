package com.example.socialnetworkapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tapadoo.alerter.Alerter;

import es.dmoral.toasty.Toasty;

public class RegisterActivity extends AppCompatActivity {

    private TextView RWelcomeText;
    private EditText Remail;
    private EditText Rpassword;
    private EditText Rconpassword;
    private Button RegisterButton;
    private Button AlreadyButton;
    private FloatingActionButton BackButton;
    private FirebaseAuth Mauth;
    private ProgressDialog Mprogress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Mauth = FirebaseAuth.getInstance();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        Mprogress = new ProgressDialog(RegisterActivity.this);
        RWelcomeText = findViewById(R.id.RegistertextID);
        Remail = findViewById(R.id.NewEmailID);
        Rpassword = findViewById(R.id.NewPasswordID);
        Rconpassword = findViewById(R.id.NewConFrimPasswordID);
        RegisterButton = findViewById(R.id.RegisterButtonID);
        AlreadyButton = findViewById(R.id.AlreadyAccButtonID);
        BackButton = findViewById(R.id.BackButtonID);


        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailtext = Remail.getText().toString();
                String passwordtext = Rpassword.getText().toString();
                String cpasswordtext = Rconpassword.getText().toString();

                if(emailtext.isEmpty() || passwordtext.isEmpty() || cpasswordtext.isEmpty()){
                    Alerter.create(RegisterActivity.this)
                            .setBackgroundColorRes(R.color.alarter_colour)
                            .enableSwipeToDismiss()
                            .setTitle("Follow Me !!")
                            .setText("Please input your valid email and password")
                            .show();
                }
                else {
                    if(!passwordtext.equals(cpasswordtext)){
                        Alerter.create(RegisterActivity.this)
                                .setBackgroundColorRes(R.color.alarter_wrong)
                                .setTitle("Error !")
                                .setText("Your Password is Wrong or didn't match")
                                .enableSwipeToDismiss()
                                .setIcon(R.drawable.cancel)
                                .show();

                    }

                    else {

                        Mprogress.setTitle("Task is ongoing ...");
                        Mprogress.setMessage("Please wait we are creating new account for you");
                        Mprogress.setCanceledOnTouchOutside(false);
                        Mprogress.show();
                        Mauth.createUserWithEmailAndPassword(emailtext, passwordtext)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if(task.isSuccessful()){
                                            Intent intent = new Intent(RegisterActivity.this, SetupActivity.class);
                                            startActivity(intent);
                                            Toasty.success(RegisterActivity.this, "Account setup success", Toasty.LENGTH_LONG).show();
                                            Mprogress.dismiss();
                                        }
                                        else {
                                            String errormessege = task.getException().getMessage().toString();
                                            Toasty.error(RegisterActivity.this, errormessege, Toasty.LENGTH_LONG).show();
                                            Mprogress.dismiss();
                                        }
                                    }
                                });
                    }
                }

            }
        });



        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backintent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(backintent);
            }
        });

        AlreadyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backintent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(backintent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser Muser = Mauth.getCurrentUser();
        if(Muser != null){
            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }
}
