package com.naptechlabs.skypeclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Registration extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button  btnSignUp;
    private ProgressDialog mProgress;
    private FirebaseAuth auth;
    String email, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 0);
        setContentView(R.layout.activity_registration);


        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.hide();
        }

        auth = FirebaseAuth.getInstance();


        btnSignUp = (Button) findViewById(R.id.btn_login);
        inputEmail = (EditText) findViewById(R.id.inputEmailEtdId);
        inputPassword = (EditText) findViewById(R.id.inputPassEtdId);


        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Processing...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = inputEmail.getText().toString().trim();
                password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    inputEmail.setError("Enter Valid Email");
                    inputEmail.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    inputEmail.setError("Enter Valid Email");
                    inputEmail.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    inputPassword.setError("Required password");
                    inputPassword.requestFocus();
                    return;
                }

                if (password.length() < 6) {
                    inputPassword.setError("Atleast 6 characters requried");
                    inputPassword.requestFocus();
                    return;
                }

                mProgress.show();
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Registration.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (!task.isSuccessful()) {
                                    mProgress.dismiss();
                                    Toast.makeText(Registration.this, "Authentication failed!!\n check internet connection",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    mProgress.dismiss();
                                    Intent i = new Intent(Registration.this, LoginActivity.class);
                                    i.putExtra("emailS",email);
                                    startActivity(i);
                                    finish();
                                }
                            }
                        });

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


}
