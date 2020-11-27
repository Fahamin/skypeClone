package com.naptechlabs.skypeclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    String reciveID, reciveName, reciveImage;

    ImageView profileImage;
    TextView userNameTxt;
    Button addFriendBtn, removeFriendBtn;

    FirebaseAuth auth;
    String senderUserID;
    String currentState = "new";

    DatabaseReference firedReqestRef, contactsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        reciveID = getIntent().getStringExtra("visit_user_id");
        reciveImage = getIntent().getStringExtra("profile_image");
        reciveName = getIntent().getStringExtra("profile_name");


        profileImage = findViewById(R.id.profile_imageID);
        userNameTxt = findViewById(R.id.profile_user_name);
        addFriendBtn = findViewById(R.id.add_friend);
        removeFriendBtn = findViewById(R.id.user_remove_btn);

        Picasso.get().load(reciveImage).into(profileImage);
        userNameTxt.setText(reciveName);

        auth = FirebaseAuth.getInstance();
        senderUserID = auth.getCurrentUser().getUid();
        firedReqestRef = FirebaseDatabase.getInstance().getReference().child("Friend Request");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("contacts");
        
        manageClick();

    }

    private void manageClick() {

        firedReqestRef.child(senderUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(reciveID))
                {
                    String requestType = dataSnapshot.child(reciveID).child("request_type").getValue().toString();
                    if(requestType.equals("sent"))
                    {
                        currentState = "request_sent";
                        addFriendBtn.setText("Cancle Friend Request");
                    }
                    else if(requestType.equals("received"))
                    {
                        currentState = "request_received";
                        addFriendBtn.setText("Accept Friend Request");
                        removeFriendBtn.setVisibility(View.VISIBLE);
                        removeFriendBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancleFriendRequest();
                            }
                        });
                    }
                    else
                    {
                        contactsRef.child(senderUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if(dataSnapshot.hasChild(reciveID))
                                {
                                    currentState  = "friends";
                                    addFriendBtn.setText("Delete Contact ");
                                }
                                else
                                {
                                    currentState = "new";
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if (senderUserID.equals(reciveID)) {
            addFriendBtn.setVisibility(View.GONE);
        } else {
            addFriendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentState.equals("new")) {
                        sendFriendRequest();
                    }
                    if (currentState.equals("request_sent")) {
                        cancleFriendRequest();
                    }
                    if (currentState.equals("request_received")) {

                        acceptFriendReqest();
                    }
                    if (currentState.equals("request_sent ")) {
                        cancleFriendRequest();
                    }
                }
            });
        }

    }

    private void acceptFriendReqest() {
        contactsRef.child(senderUserID).child(reciveID).child("Contact").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    contactsRef.child(reciveID).child(senderUserID).child("Contact").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                firedReqestRef.child(senderUserID).child(reciveID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            firedReqestRef.child(reciveID).child(senderUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        currentState = "friends";
                                                        addFriendBtn.setText("Delete Contact");
                                                        removeFriendBtn.setVisibility(View.GONE);
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

        firedReqestRef.child(senderUserID).child(reciveID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    firedReqestRef.child(reciveID).child(senderUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                currentState = "new";
                                addFriendBtn.setText("Add Friend");
                            }
                        }
                    });
                }
            }
        });

    }

    private void sendFriendRequest() {

        firedReqestRef.child(senderUserID).child(reciveID).
                child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    firedReqestRef.child(reciveID).child(senderUserID).
                            child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                currentState = "request_sent";
                                addFriendBtn.setText("Cancle Friend Request");
                                Toast.makeText(ProfileActivity.this, "Firend reqest sent", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }

            }
        });
    }

}
