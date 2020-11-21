package com.naptechlabs.skypeclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ContexActivity extends AppCompatActivity {
    BottomNavigationView navView;
    RecyclerView contactsList;
    ImageView findPeopelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contex);
        navView = findViewById(R.id.nav_view);

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
                    startActivity(new Intent(ContexActivity.this, ContexActivity.class));
                    finish();
                    break;
            }
            return true;
        }
    };
}
