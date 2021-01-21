package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.whatsapp.databinding.ActivitySignInPhoneBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class SignInPhoneActivity extends AppCompatActivity {
    ActivitySignInPhoneBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInPhoneBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnNumberSubmit.setOnClickListener(view -> {
            String phoneNumber = binding.etNumber.getText().toString();
            if(phoneNumber.length() != 10){
                binding.etNumber.setError("Please enter valid mobile number");
                binding.etNumber.requestFocus();
                return;
            }
            phoneNumber = "+91"+phoneNumber;
            Intent intent = new Intent(getApplicationContext(),SigninPhoneVerifyActivity.class);
            intent.putExtra("phoneNumber",phoneNumber);
            startActivity(intent);


        });
    }


}