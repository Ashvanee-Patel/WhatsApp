package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsapp.Models.Users;
import com.example.whatsapp.databinding.ActivitySignUpBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    ProgressDialog progressDialog;
    Context context = SignUpActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("We are creating your account");
        progressDialog.setCancelable(false);

        binding.signUpBtSU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( binding.userNameEtSU.getText().toString().matches("") ){
                    binding.userNameEtSU.setError("User name required");
                }else if(binding.userEmailEtSU.getText().toString().matches("")){
                    binding.userEmailEtSU.setError("Email required");
                }
                else if(binding.userPasswordEtSU.getText().toString().matches("")){
                    binding.userPasswordEtSU.setError("Password required");
                }else {
                    progressDialog.show();
                    mAuth.createUserWithEmailAndPassword
                            (binding.userEmailEtSU.getText().toString(),binding.userPasswordEtSU.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if(task.isSuccessful()){
                                Users user = new Users(binding.userNameEtSU.getText().toString(),binding.userEmailEtSU.getText().toString(),
                                        binding.userPasswordEtSU.getText().toString());
                                String id = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getUser()).getUid();
                                database.getReference().child("Users").child(id).setValue(user);
                                Toast.makeText(context,"Register Successfully",Toast.LENGTH_LONG).show();
                                mAuth.signOut();
                                startActivity(new Intent(context,SignInActivity.class));
                                finish();
                            } else {
                                Toast.makeText(context, Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_LONG).show();
                            }           }
                    });
                }
            }
        });
        binding.alreadyAcTvSU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context,SignInActivity.class));
                finish();
            }
        });
    }
}