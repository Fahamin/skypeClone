package com.naptechlabs.skypeclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Notification extends AppCompatActivity {

    RecyclerView notificationLIST;
    DatabaseReference firedReqestRef, contactsRef, userRef;
    FirebaseAuth auth;
    String currentUserID;
    String listUserID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        auth = FirebaseAuth.getInstance();
        currentUserID = auth.getCurrentUser().getUid();

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
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(firedReqestRef.child(currentUserID), Contacts.class).build();

        FirebaseRecyclerAdapter<Contacts, NotificationViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Contacts, NotificationViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final NotificationViewHolder notificationViewHolder, int i, @NonNull Contacts contacts) {

                        notificationViewHolder.acceptBtn.setVisibility(View.VISIBLE);
                        notificationViewHolder.declineBtn.setVisibility(View.VISIBLE);

                        listUserID = getRef(i).getKey();
                        DatabaseReference requestRef = getRef(i).child("request_type").getRef();
                        requestRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String type = dataSnapshot.getValue().toString();

                                    if (type.equals("received")) {
                                        notificationViewHolder.carView.setVisibility(View.VISIBLE);
                                        userRef.child(listUserID).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.hasChild("image")) {
                                                    final String imgStr = dataSnapshot.child("image").getValue().toString();
                                                    Picasso.get().load(imgStr).into(notificationViewHolder.profileImageView);
                                                }

                                                final String nameStr = dataSnapshot.child("name").getValue().toString();
                                                notificationViewHolder.userNameTxt.setText(nameStr);

                                                notificationViewHolder.acceptBtn.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        acceptFriendReqest();
                                                    }
                                                });
                                                notificationViewHolder.declineBtn.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        cancleFriendRequest();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                    } else {
                                        notificationViewHolder.carView.setVisibility(View.GONE);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_friend_design,
                                parent, false);

                        return new NotificationViewHolder(view);
                    }
                };
        notificationLIST.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    private void acceptFriendReqest() {
        contactsRef.child(currentUserID).child(listUserID).child("Contact").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    contactsRef.child(listUserID).child(currentUserID).child("Contact").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                firedReqestRef.child(currentUserID).child(listUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            firedReqestRef.child(listUserID).child(currentUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(Notification.this, "Contact Saved", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    private void cancleFriendRequest() {

        firedReqestRef.child(currentUserID).child(listUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    firedReqestRef.child(listUserID).child(currentUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Notification.this, "Friend Request decline", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }

}
