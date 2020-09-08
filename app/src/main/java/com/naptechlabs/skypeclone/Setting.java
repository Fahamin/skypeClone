package com.naptechlabs.skypeclone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class Setting extends AppCompatActivity {

    ImageView profile_imageView;
    Button saveBtn;
    EditText userEDT, bioEDT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        profile_imageView = findViewById(R.id.setting_Profile_image);
        saveBtn = findViewById(R.id.save_setting_Btn);
        userEDT = findViewById(R.id.username_setting_EDT);
        bioEDT = findViewById(R.id.bio_setting_EDT);

    }
}
