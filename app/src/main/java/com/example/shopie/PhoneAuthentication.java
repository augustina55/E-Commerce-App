package com.example.shopie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneAuthentication extends AppCompatActivity {
    ProgressBar progress_bar;
    Button generate_btn,verify_otp;
    EditText country_code_text,phone_number_text,phone_code;
    private String mVerificationId;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_authentication);
        progress_bar=findViewById(R.id.progress_bar);
        progress_bar.setVisibility(View.INVISIBLE);
        mAuth=FirebaseAuth.getInstance();
        phone_code=findViewById(R.id.phone_code);
        phone_number_text=findViewById(R.id.phone_number_text);
        country_code_text=findViewById(R.id.country_code_text);
        verify_otp=findViewById(R.id.verify_otp);
        generate_btn=findViewById(R.id.generate_btn);
        generate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress_bar.setVisibility(View.VISIBLE);
                if(country_code_text.getText().toString().trim().isEmpty()){
                    country_code_text.setError("Enter a Country code");
                    country_code_text.requestFocus();
                }else if(phone_number_text.getText().toString().trim().isEmpty() ||
                        phone_number_text.getText().toString().trim().length()<10){
                    phone_number_text.setError("Enter a valid phone number");
                    phone_number_text.requestFocus();
                }else
                    SendOTP(country_code_text.getText().toString().trim(),phone_number_text.getText().toString().trim());
                progress_bar.setVisibility(View.INVISIBLE);
            }
        });
        verify_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress_bar.setVisibility(View.VISIBLE);
                String code = phone_code.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    phone_code.setError("Enter valid code");
                    phone_code.requestFocus();
                    return;
                } //verifying the code entered manually
                verifyVerificationCode(code);
                progress_bar.setVisibility(View.INVISIBLE);
            }
        });
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                phone_code.setText(code);
                verifyVerificationCode(code);
            }
        }
        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(PhoneAuthentication.this, e.getMessage(),Toast.LENGTH_LONG).show();
        }
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            //storing the verification id that is sent to the user
            mVerificationId = s;

            country_code_text.setVisibility(View.INVISIBLE);
            phone_number_text.setVisibility(View.INVISIBLE);
            phone_code.setVisibility(View.VISIBLE);
            generate_btn.setVisibility(View.INVISIBLE);
            verify_otp.setVisibility(View.VISIBLE);

        }
    };
    private void SendOTP(String code,String mobile){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                code + mobile,
                60,
                TimeUnit.SECONDS,
                PhoneAuthentication.this,
                mCallbacks);
    }

    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential =PhoneAuthProvider.getCredential(mVerificationId, code);
        //signing the user
        signInWithPhoneAuthCredential(credential);
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential)
    {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //verification successful we will start the profile activity
                    Intent intent = new Intent(PhoneAuthentication.this, Home.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    //verification unsuccessful.. display an error message
                    String message = "Somthing is wrong, we will fix it soon...";
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        message = "Invalid code entered...";
                    }
                    Toast.makeText(PhoneAuthentication.this, message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}