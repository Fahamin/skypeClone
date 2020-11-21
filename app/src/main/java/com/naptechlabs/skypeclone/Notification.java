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

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Notification extends AppCompatActivity {

    RecyclerView notificationLIST;
    DatabaseReference firedReqestRef, contactsRef,userRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        notificationLIST = findViewById(R.id.notification_List);
        notificationLIST.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        firedReqestRef = FirebaseDatabase.getInstance().getReference().child("Friend Request");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("contacts");
        userRef = FirebaseDatabase.getInstance().getReference().child("User");
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

    @Override
    protected void onStart() {
        super.onStart();
    FirebaseRecyclerOptions  options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(firedReqestRef, Contacts.class).build();

    }
}
