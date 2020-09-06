package com.naptechlabs.skypeclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class Register extends AppCompatActivity {

    private CountryCodePicker ccp;
    EditText poneTest, codeTest;
    Button continueAndNextBtn;
    String checker = "", phoneNumber = "";
    RelativeLayout relativeLayout;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    FirebaseAuth mAuth;
    String mVerification;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    ProgressDialog lodingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        lodingBar = new ProgressDialog(this);
        poneTest = findViewById(R.id.phoneText);
        codeTest = findViewById(R.id.codeText);
        continueAndNextBtn = findViewById(R.id.continueNextButton);
        relativeLayout = findViewById(R.id.phoneAuth);
        ccp = findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(poneTest);

        continueAndNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (continueAndNextBtn.getText().equals("Submit") || checker.equals("Code Sent")) {

                    String verificationCode = codeTest.getText().toString();
                    if (verificationCode.equals("")) {
                        Toast.makeText(Register.this, "Write your Code", Toast.LENGTH_SHORT).show();
                    } else {
                        lodingBar.setTitle("Code Verification");
                        lodingBar.setMessage("Please wait, while we are verify code");
                        lodingBar.setCanceledOnTouchOutside(false);
                        lodingBar.show();

                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerification, verificationCode);
                        signInWithPhoneAuthCredential(credential);
                    }
                } else {
                    phoneNumber = ccp.getFullNumberWithPlus();
                    if (!phoneNumber.equals("")) {
                        lodingBar.setTitle("Phone Number Verification");
                        lodingBar.setMessage("Please wait, while we are verify number");
                        lodingBar.setCanceledOnTouchOutside(false);
                        lodingBar.show();

                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                phoneNumber,        // Phone number to verify
                                60,                 // Timeout duration
                                TimeUnit.SECONDS,   // Unit of timeout
                                Register.this,               // Activity (for callback binding)
                                mCallbacks);        // OnVerificationStateChangedCallbacks

                    } else {
                        Toast.makeText(Register.this, "Please enter valid phone num", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                lodingBar.dismiss();
                Toast.makeText(Register.this, "Number is Invalid", Toast.LENGTH_SHORT).show();
                relativeLayout.setVisibility(View.VISIBLE);
                codeTest.setVisibility(View.GONE);
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                mVerification = s;
                mResendToken = forceResendingToken;
                relativeLayout.setVisibility(View.GONE);
                checker = "Code Sent";
                continueAndNextBtn.setText("Submit");
                codeTest.setVisibility(View.VISIBLE);
                lodingBar.dismiss();
                Toast.makeText(Register.this, "Code has been Send", Toast.LENGTH_SHORT).show();
            }
        };

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            lodingBar.dismiss();
                            Toast.makeText(Register.this, "Congratulatin", Toast.LENGTH_SHORT).show();

                            sendUsertoManActivity();

                        } else {
                            lodingBar.dismiss();
                            String s = task.getException().toString();
                            Toast.makeText(Register.this, "failed", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    public void sendUsertoManActivity() {
        startActivity(new Intent(this, MainActivity.class));
    }
}
