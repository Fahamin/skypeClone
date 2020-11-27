package com.naptechlabs.skypeclone;

import android.content.Intent;
import android.os.Bundle;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.naptechlabs.skypeclone.CallingActivity;
import com.naptechlabs.skypeclone.FindPeople;
import com.naptechlabs.skypeclone.R;
import com.naptechlabs.skypeclone.Setting;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ContexActivity extends AppCompatActivity {
    BottomNavigationView navView;
    RecyclerView contactsList;
    ImageView findPeopelBtn;

    DatabaseReference firedReqestRef, contactsRef, userRef;
    FirebaseAuth auth;
    String currentUserID;
    String listUserID = "";
    String userName = "", profileImgeLink = "";
    String calledBy = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contex);
        navView = findViewById(R.id.nav_view);

        auth = FirebaseAuth.getInstance();
        currentUserID = auth.getCurrentUser().getUid();
        contactsRef = FirebaseDatabase.getInstance().getReference().child("contacts");
        userRef = FirebaseDatabase.getInstance().getReference().child("User");
        navView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        findPeopelBtn = findViewById(R.id.find_PepoleBtn);
        contactsList = findViewById(R.id.contactsList);
        contactsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        findPeopelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ContexActivity.this, FindPeople.class));
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        CheckForReciveCALL();
        validationUser();
        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(contactsRef.child(currentUserID), Contacts.class).build();


        FirebaseRecyclerAdapter<Contacts, ContextViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, ContextViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContextViewHolder contextViewHolder, int i, @NonNull Contacts contacts) {
                listUserID = getRef(i).getKey();
                userRef.child(listUserID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.hasChild("image") && dataSnapshot.hasChild("name")) {

                                userName = dataSnapshot.child("name").getValue().toString();
                                profileImgeLink = dataSnapshot.child("image").getValue().toString();

                                contextViewHolder.userNameTxt.setText(userName);
                                Picasso.get().load(profileImgeLink).into(contextViewHolder.profileImageView);

                                contextViewHolder.callBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(ContexActivity.this, CallingActivity.class)
                                                .putExtra("visit_user_id", listUserID));
                                        finish();
                                    }
                                });
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
            public ContextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_design,
                        parent, false);

                return new ContextViewHolder(view);
            }
        };

        contactsList.setAdapter(adapter);
        adapter.startListening();
    }

    private void CheckForReciveCALL() {
        userRef.child(currentUserID)
                .child("Ringing").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild("ringing"))
                {
                    calledBy = dataSnapshot.child("ringing").getValue().toString();

                    startActivity(new Intent(ContexActivity.this, CallingActivity.class)
                            .putExtra("visit_user_id", calledBy));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void validationUser() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child("User").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    startActivity(new Intent(ContexActivity.this, Setting.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public static class ContextViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTxt;
        Button callBtn;
        ImageView profileImageView;


        public ContextViewHolder(@NonNull View view) {
            super(view);

            userNameTxt = view.findViewById(R.id.name_contacts);
            callBtn = view.findViewById(R.id.call_btn);
            profileImageView = view.findViewById(R.id.image_contacts);


        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {

                case R.id.navigation_home:
                    startActivity(new Intent(ContexActivity.this, ContexActivity.class));
                    break;
                case R.id.navigation_notifications:
                    startActivity(new Intent(ContexActivity.this, Notification.class));
                    break;
                case R.id.navigation_setting:
                    startActivity(new Intent(ContexActivity.this, Setting.class));
                    break;
                case R.id.navigation_logout:
                    FirebaseAuth.getInstance().signOut();
                    finish();
                    break;
            }
            return true;
        }
    };
}
