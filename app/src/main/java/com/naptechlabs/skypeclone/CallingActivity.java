package com.naptechlabs.skypeclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.telecom.Call;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class CallingActivity extends AppCompatActivity {

    TextView nameContacts;
    ImageView profileIMAGE, canclecallBtn, aceptCallBtn;
    String reciverUserID = "", reciverUSERiMAGE = "", reciverUserNAME = "";
    String senderUserID = "", senderUSERiMAGE = "", senderUserNAME = "", checker = "";
    DatabaseReference userRef;
    String callinID = "", ringinID = "";

    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);

        aceptCallBtn = findViewById(R.id.makeCall);
        nameContacts = findViewById(R.id.name_Calling);
        profileIMAGE = findViewById(R.id.profile_image_calling);
        canclecallBtn = findViewById(R.id.cancleCALL);

        senderUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        reciverUserID = getIntent().getStringExtra("visit_user_id").toString();
        userRef = FirebaseDatabase.getInstance().getReference().child("User");

        mediaPlayer = MediaPlayer.create(this, R.raw.tone);
        canclecallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                checker = "clicked";
                cancleCallingUser();
            }
        });

        aceptCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                final HashMap<String, Object> callingPicckupMap = new HashMap<>();
                callingPicckupMap.put("picked", "picked");
                userRef.child(senderUserID).child("Ringing")
                        .updateChildren(callingPicckupMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    mediaPlayer.stop();
                                    startActivity(new Intent(CallingActivity.this, VideoChatActivity.class));
                                }
                            }
                        });
            }
        });
        getReciverUserInfo();
    }

    private void cancleCallingUser() {
        //from sender side
        userRef.child(senderUserID).child("Calling").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists() && dataSnapshot.hasChild("calling")) {
                    callinID = dataSnapshot.child("calling").getValue().toString();

                    userRef.child(callinID).child("Ringing").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                userRef.child(senderUserID).child("Calling")
                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        startActivity(new Intent(CallingActivity.this, ContexActivity.class));
                                        finish();
                                    }
                                });
                            }

                        }
                    });
                } else startActivity(new Intent(CallingActivity.this, ContexActivity.class));
                finish();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //from receiver side
        userRef.child(senderUserID).child("Ringing").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists() && dataSnapshot.hasChild("ringing")) {
                    ringinID = dataSnapshot.child("ringing").getValue().toString();

                    userRef.child(ringinID).child("ringing").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                userRef.child(senderUserID).child("Ringing")
                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        startActivity(new Intent(CallingActivity.this, ContexActivity.class));
                                        finish();
                                    }
                                });
                            }

                        }
                    });
                } else startActivity(new Intent(CallingActivity.this, ContexActivity.class));
                finish();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getReciverUserInfo() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(reciverUserID).exists()) {
                    reciverUSERiMAGE = dataSnapshot.child(reciverUserID).child("image").getValue().toString();
                    reciverUserNAME = dataSnapshot.child(reciverUserID).child("name").getValue().toString();
                    nameContacts.setText(reciverUserNAME);
                    Picasso.get().load(reciverUSERiMAGE).placeholder(R.drawable.profile_image).into(profileIMAGE);

                }

                if (dataSnapshot.child(senderUserID).exists()) {

                    senderUSERiMAGE = dataSnapshot.child(senderUserID).child("image").getValue().toString();
                    senderUserNAME = dataSnapshot.child(senderUserID).child("name").getValue().toString();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mediaPlayer.start();
        userRef.child(reciverUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!checker.equals("clicked") && !dataSnapshot.hasChild("Calling") && !dataSnapshot.hasChild("Ringing")) {


                    final HashMap<String, Object> callinInfo = new HashMap<>();
                    callinInfo.put("calling", reciverUserID);

                    userRef.child(senderUserID)
                            .child("Calling")
                            .updateChildren(callinInfo)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        final HashMap<String, Object> ringinInfo = new HashMap<>();

                                        ringinInfo.put("ringing", senderUserID);

                                        userRef.child(reciverUserID)
                                                .child("Ringing")
                                                .updateChildren(ringinInfo);
                                    }
                                }
                            });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(senderUserID).hasChild("Ringing")
                        && !dataSnapshot.child(senderUserID).hasChild("Calling")) {
                    aceptCallBtn.setVisibility(View.VISIBLE);
                }
                if (dataSnapshot.child(reciverUserID).child("Ringing").hasChild("picked")) {
                   mediaPlayer.stop();
                    startActivity(new Intent(CallingActivity.this, VideoChatActivity.class));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}