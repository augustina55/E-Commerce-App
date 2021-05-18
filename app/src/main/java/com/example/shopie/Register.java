package com.example.shopie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class Register extends AppCompatActivity {
    EditText
            editTextName,editTextEmail,editTextPassword,editTextConfirmPassword;
    TextView accountText;
    CircularProgressButton cirRegisterButton;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        mAuth=FirebaseAuth.getInstance();
        editTextPassword=findViewById(R.id.editTextPassword);
        editTextName=findViewById(R.id.editTextName);
        editTextEmail=findViewById(R.id.editTextEmail);
        editTextConfirmPassword=findViewById(R.id.editTextConfirmPassword);
        accountText=findViewById(R.id.accountText);
        accountText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this,Login.class));
                finish();
            }
        });
        cirRegisterButton=findViewById(R.id.cirRegisterButton);
        cirRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(editTextName.getText().toString()))
                    editTextName.setError("Please Enter Name");
                else if(TextUtils.isEmpty(editTextEmail.getText().toString()))
                    editTextEmail.setError("Please Enter Email");
                else if(editTextPassword.getText().toString().trim().length() <6)
                    editTextPassword.setError("Password must contain 6 characters");
                else if(TextUtils.isEmpty(editTextPassword.getText().toString()))
                    editTextPassword.setError("Please Enter password");
                else
                if(TextUtils.isEmpty(editTextConfirmPassword.getText().toString()))
                    editTextConfirmPassword.setError("Please Enter confirm password");
                else
                if(! editTextPassword.getText().toString().trim().equals(editTextConfirmPassword.getText().toString().trim()))
                    editTextPassword.setError("Password didnt match");
                else
                    createAccount(editTextEmail.getText().toString().trim(),editTextPassword.getText().toString().trim());
                cirRegisterButton.stopAnimation();
            }
        });
    }
    private void createAccount(String email,String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Register.this,"Account created! Please login using email and password",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Register.this,Login.class));
                            finish();
                        }else
                            Toast.makeText(Register.this,"Something went wrong, please try again later",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Register.this,"Error:"+e.getMessage(),Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }
}