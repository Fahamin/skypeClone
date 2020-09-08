package com.naptechlabs.skypeclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Notification extends AppCompatActivity {

    RecyclerView notificationLIST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        notificationLIST = findViewById(R.id.notification_List);
        notificationLIST.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTxt;
        Button acceptBtn, declineBtn;
        ImageView profileImageView;
        RelativeLayout carView;

        public NotificationViewHolder(@NonNull View view) {
            super(view);

            userNameTxt = view.findViewById(R.id.name_notification);
            acceptBtn = view.findViewById(R.id.request_accept_btn);
            declineBtn = view.findViewById(R.id.request_decline_btn);
            profileImageView = view.findViewById(R.id.image_notification);
            carView = view.findViewById(R.id.cardViewID);


        }
    }
}