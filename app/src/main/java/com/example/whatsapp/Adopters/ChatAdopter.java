package com.example.whatsapp.Adopters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.MainActivity;
import com.example.whatsapp.Models.MessagesModel;
import com.example.whatsapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ChatAdopter extends RecyclerView.Adapter{
    ArrayList<MessagesModel> messagesModels;
    Context context;
    String receiverId;

    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_TYPE = 2;

    public ChatAdopter(ArrayList<MessagesModel> messagesModels, Context context) {
        this.messagesModels = messagesModels;
        this.context = context;
    }
    public ChatAdopter(ArrayList<MessagesModel> messagesModels, Context context, String receiverId) {
        this.messagesModels = messagesModels;
        this.context = context;
        this.receiverId = receiverId;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == SENDER_VIEW_TYPE){
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender_chat,parent,false);
            return new senderViewHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_receiver_chat,parent,false);
            return new receiverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(messagesModels.get(position).getUid().equals(FirebaseAuth.getInstance().getUid())){
            return SENDER_VIEW_TYPE;
        }
        else {
            return RECEIVER_TYPE;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessagesModel messagesModel = messagesModels.get(position);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete")
                        .setMessage("Are you sure, you want to delete this message")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                if(MainActivity.chatType.equalsIgnoreCase("personalChat")){
                                    String senderRoom = FirebaseAuth.getInstance().getUid() + receiverId;
                                    database.getReference().child("chats").child(senderRoom)
                                            .child(messagesModel.getMessageId())
                                            .setValue(null);
                                }
                                else if(MainActivity.chatType.equalsIgnoreCase("groupChat")){
                                    database.getReference().child("Group Chat")
                                            .child(messagesModel.getMessageId())
                                            .setValue(null);
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
                return false;
            }
        });

        Timestamp timestamp = new Timestamp(messagesModel.getTimeStamp());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy'\n\t'HH:mm");
        if(holder.getClass() == senderViewHolder.class){
            ((senderViewHolder)holder).senderMsg.setText(messagesModel.getMessage());
            ((senderViewHolder)holder).senderTime.setText(simpleDateFormat.format(timestamp));

        }else {
            ((receiverViewHolder)holder).receiverMsg.setText(messagesModel.getMessage());
            ((receiverViewHolder)holder).receiverTime.setText(simpleDateFormat.format(timestamp));
        }    }

    @Override
    public int getItemCount() {
        return messagesModels.size();
    }

    public class receiverViewHolder extends RecyclerView.ViewHolder {

        TextView receiverMsg, receiverTime;


        public receiverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverMsg = itemView.findViewById(R.id.receiverTxtSRC);
            receiverTime = itemView.findViewById(R.id.receiverTimeTxtSRC);

        }
    }

    public class senderViewHolder extends RecyclerView.ViewHolder {

        TextView senderMsg, senderTime;

        public senderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg = itemView.findViewById(R.id.senderTxtSSC);
            senderTime = itemView.findViewById(R.id.timeSenderTxtSSC);
        }
    }

}
