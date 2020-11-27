package com.naptechlabs.skypeclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.HashMap;

public class Setting extends AppCompatActivity {

    ImageView profile_imageView;
    Button saveBtn;
    EditText userEDT, bioEDT;
    private static int galleryRequestCode = 9;
    private Uri imageUri = null;
    ProgressDialog progressDialog;
    private StorageReference userProfileImageRef;
    private String dowwnloadurl;
    private DatabaseReference userRef;

    FirebaseUser userID;


    String imagedb = null;
    String biodb = null;
    String namedb = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        profile_imageView = findViewById(R.id.setting_Profile_image);
        saveBtn = findViewById(R.id.save_setting_Btn);
        userEDT = findViewById(R.id.username_setting_EDT);
        bioEDT = findViewById(R.id.bio_setting_EDT);
        progressDialog = new ProgressDialog(this);
        userProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile");
        userRef = FirebaseDatabase.getInstance().getReference().child("User");

        profile_imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent gallerInten = new Intent();
                gallerInten.setAction(Intent.ACTION_GET_CONTENT);
                gallerInten.setType("Image/*");
                startActivityForResult(gallerInten, galleryRequestCode);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserData();
                startActivity(new Intent(Setting.this, ContexActivity.class));
            }


        });
        //here is data confirm to after to retirve  or app carsh
        String ss = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (saveUserData()) {
            retriveUserInfo();



        }

    }

    private boolean saveUserData() {
        final String getuserName = userEDT.getText().toString();
        final String getuserBio = bioEDT.getText().toString();
        if (imageUri == null) {

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).hasChild("image")) {
                        savedataWithoutImage();
                    } else
                        Toast.makeText(Setting.this, "Please select image", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else if (getuserName.equals("")) {
            Toast.makeText(this, "Requred name", Toast.LENGTH_SHORT).show();
        } else if (getuserBio.equals("")) {
            Toast.makeText(this, "Requred bio", Toast.LENGTH_SHORT).show();
        } else {

            progressDialog.setTitle("Account Setting");
            progressDialog.setMessage("Please wait ....");
            progressDialog.show();
            final StorageReference filepath = userProfileImageRef.child(FirebaseAuth.getInstance().getCurrentUser()
                    .getUid());

            final UploadTask uploadTask = filepath.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    dowwnloadurl = filepath.getDownloadUrl().toString();

                    return filepath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        dowwnloadurl = task.getResult().toString();
                        HashMap<String, Object> profileMaap = new HashMap<>();
                        profileMaap.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        profileMaap.put("name", getuserName);
                        profileMaap.put("bio", getuserBio);
                        profileMaap.put("image", dowwnloadurl);

                        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(profileMaap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    retriveUserInfo();
                                    startActivity(new Intent(Setting.this, ContexActivity.class));
                                    progressDialog.dismiss();

                                    Toast.makeText(Setting.this, "Profile update", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
        }

        return true;
    }

    public void retriveUserInfo() {
        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild("image")) {
                             imagedb = dataSnapshot.child("image").getValue().toString();

                        }
                        if (dataSnapshot.hasChild("bio")) {
                            biodb = dataSnapshot.child("bio").getValue().toString();


                        }
                        if (dataSnapshot.hasChild("name")) {
                            namedb = dataSnapshot.child("name").getValue().toString();

                        }

                        if (imagedb != null) {
                            Picasso.get().load(imagedb).placeholder(R.drawable.profile_image
                            ).into(profile_imageView);
                        }
                        if (biodb != null) {
                            bioEDT.setText(biodb);

                        }
                        if (namedb != null) {
                            userEDT.setText(namedb);

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void savedataWithoutImage() {

        final String getuserName = userEDT.getText().toString();
        final String getuserBio = bioEDT.getText().toString();
        if (getuserName.equals("")) {
            Toast.makeText(this, "Requred name", Toast.LENGTH_SHORT).show();
        } else if (getuserBio.equals("")) {
            Toast.makeText(this, "Requred bio", Toast.LENGTH_SHORT).show();
        } else {

            progressDialog.setTitle("Account Setting");
            progressDialog.setMessage("Please wait ....");
            progressDialog.show();
            HashMap<String, Object> profileMaap = new HashMap<>();
            profileMaap.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
            profileMaap.put("name", getuserName);
            profileMaap.put("bio", getuserBio);

            userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(profileMaap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {
                        startActivity(new Intent(Setting.this, ContexActivity.class));
                        finish();
                        progressDialog.dismiss();
                        Toast.makeText(Setting.this, "Profile update", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == galleryRequestCode && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            profile_imageView.setImageURI(imageUri);
        }
    }
}
