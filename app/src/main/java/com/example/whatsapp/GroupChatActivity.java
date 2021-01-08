package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.example.whatsapp.Adopters.ChatAdopter;
import com.example.whatsapp.Models.MessagesModel;
import com.example.whatsapp.databinding.ActivityGroupChatBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class GroupChatActivity extends AppCompatActivity {

    ActivityGroupChatBinding binding;
    FirebaseDatabase database;
    ChatAdopter chatAdopter;
    Context context = GroupChatActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        MainActivity.chatType = "groupChat";
        binding.backArrowIvGC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        database = FirebaseDatabase.getInstance();
        final ArrayList<MessagesModel> messagesModels = new ArrayList<>();
        final String senderId = FirebaseAuth.getInstance().getUid();
        binding.groupNameTv.setText("Arpan Group");
        chatAdopter = new ChatAdopter(messagesModels, context);
        binding.recyclerViewGC.setAdapter(chatAdopter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        binding.recyclerViewGC.setLayoutManager(layoutManager);

        database.getReference().child("Group Chat")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messagesModels.clear();
                        for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                            MessagesModel model = dataSnapshot.getValue(MessagesModel.class);
                            assert model != null;
                            model.setMessageId(dataSnapshot.getKey());
                            messagesModels.add(model);
                        }
                        chatAdopter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.sendMessageIvGC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String message = binding.messageEtGC.getText().toString();
                if(message.length() > 0){
                    final MessagesModel model = new MessagesModel(senderId,message);
                    model.setTimeStamp(new Date().getTime());
                    binding.messageEtGC.setText("");

                    database.getReference().child("Group Chat")
                            .push()
                            .setValue(model)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            });

                }
            }
        });
    }
}