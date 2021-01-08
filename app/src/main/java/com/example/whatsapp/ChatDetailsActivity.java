package com.example.whatsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.whatsapp.Adopters.ChatAdopter;
import com.example.whatsapp.Models.MessagesModel;
import com.example.whatsapp.databinding.ActivityChatDetailsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ChatDetailsActivity extends AppCompatActivity {
    ActivityChatDetailsBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;


    Context context = ChatDetailsActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        MainActivity.chatType = "personalChat";
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        final String senderId = auth.getUid();
        String receiverId = getIntent().getStringExtra("userId");
        String receiverPic = getIntent().getStringExtra("userPic");
        String receiverName = getIntent().getStringExtra("userName");

        binding.receiverNameTv.setText(receiverName);

        if(receiverPic != null){
            Picasso.get().load(receiverPic).placeholder(R.drawable.ic_account_circle_24).into(binding.receiverProfileImage);
        }
        binding.backArrowIvCD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(context,MainActivity.class));
//                finish();
                onBackPressed();
            }
        });

        final ArrayList<MessagesModel> messagesModels = new ArrayList<>();
        final ChatAdopter chatAdopter = new ChatAdopter(messagesModels, context,receiverId);
        binding.recyclerViewCD.setAdapter(chatAdopter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        binding.recyclerViewCD.setLayoutManager(layoutManager);

        final String senderRoom = senderId + receiverId;
        final String receiverRoom = receiverId + senderId;

        // Read from the database
        database.getReference().child("chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messagesModels.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    MessagesModel model = snapshot.getValue(MessagesModel.class);
                    assert model != null;
                    model.setMessageId(snapshot.getKey());
                    messagesModels.add(model);
                }
                chatAdopter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        binding.sendMessageIvCD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    String message = binding.messageEtCD.getText().toString().trim();
                    binding.messageEtCD.setText("");
                    if(message.length()>0){
                        final MessagesModel model = new MessagesModel(senderId,message);
                        model.setTimeStamp(new Date().getTime());

                        database.getReference().child("chats")
                                .child(senderRoom)
                                .push()
                                .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats")
                                        .child(receiverRoom)
                                        .push()
                                        .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                });
                            }
                        });

                    }


            }
        });
    }

}