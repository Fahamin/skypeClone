package com.naptechlabs.skypeclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class FindPeople extends AppCompatActivity {

    RecyclerView findFriendList;
    EditText searchET;
    String str = "";
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_people);


        findFriendList = findViewById(R.id.findFriendList);
        searchET = findViewById(R.id.serarch_userText);
        findFriendList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        userRef = FirebaseDatabase.getInstance().getReference().child("User");

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (searchET.getText().toString().equals("")) {
                    Toast.makeText(FindPeople.this, "Please name enter", Toast.LENGTH_SHORT).show();
                } else {
                    str = charSequence.toString();
                    onStart();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts> options = null;
        if (str.equals("")) {
            options = new FirebaseRecyclerOptions.Builder<Contacts>()
                    .setQuery(userRef, Contacts.class).build();

        } else {
            options = new FirebaseRecyclerOptions.Builder<Contacts>()
                    .setQuery(userRef.orderByChild("name").startAt(str)
                            .endAt(str + "\uf8ff"), Contacts.class).build();
        }
        FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, final int i, @NonNull final Contacts contacts) {

                        holder.userNameTxt.setText(contacts.getName());
                        Picasso.get().load(contacts.getImage()).into(holder.profileImageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String visit_user_id = getRef(i).getKey();

                                startActivity(new Intent(FindPeople.this,
                                        ProfileActivity.class)
                                        .putExtra("visit_user_id", visit_user_id)
                                        .putExtra("profile_image", contacts.getImage())
                                        .putExtra("profile_name", contacts.getName()));

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_design,
                                parent, false);


                        return new FindFriendViewHolder(view);
                    }
                };
        findFriendList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class FindFriendViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTxt;
        Button callBtn;
        ImageView profileImageView;
        RelativeLayout carView;

        public FindFriendViewHolder(@NonNull View view) {
            super(view);

            userNameTxt = view.findViewById(R.id.name_contacts);
            callBtn = view.findViewById(R.id.call_btn);
            profileImageView = view.findViewById(R.id.image_contacts);
            carView = view.findViewById(R.id.cardViewContatsID);

            callBtn.setVisibility(View.GONE);
        }
    }
}
