package com.naptechlabs.skypeclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FindPeople extends AppCompatActivity {

    RecyclerView findFriendList;
    EditText searchET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_people);


        findFriendList = findViewById(R.id.findFriendList);
        searchET = findViewById(R.id.serarch_userText);
        findFriendList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    public static class  FindFriendViewHolder extends RecyclerView.ViewHolder
    {
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


        }
    }
}
