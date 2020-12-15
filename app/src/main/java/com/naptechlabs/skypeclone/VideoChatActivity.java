package com.naptechlabs.skypeclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class VideoChatActivity extends AppCompatActivity implements Session.SessionListener,
        PublisherKit.PublisherListener {

    private static String API_KEY = "47008124";
    private static String SESSION_ID = "2_MX40NzAwODEyNH5-MTYwNjU0MDE0MTYyM340cUEzYVhWSVJhWWhMRis2N2FpRzJ5Ryt-fg";
    private static String TOKEN = "T1==cGFydG5lcl9pZD00NzAwODEyNCZzaWc9Zjc3NDI5Yjc2NTM5NDZlOGQ3NWJkNTJhZjEyZDJmOWI5Yjg3Yzc3NDpzZXNzaW9uX2lkPTJfTVg0ME56QXdPREV5Tkg1LU1UWXdOalUwTURFME1UWXlNMzQwY1VFellWaFdTVkpoV1doTVJpczJOMkZwUnpKNVJ5dC1mZyZjcmVhdGVfdGltZT0xNjA2NTQwMjE4Jm5vbmNlPTAuMzk0OTAzNDgwOTYxOTA4OTcmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTYwNzE0NTAxNiZpbml0aWFsX2xheW91dF9jbGFzc19saXN0PQ==";
    private static final String Log_tag = VideoChatActivity.class.getName();

    private static final int RC_VIDEO_APP_PREM = 124;

    FrameLayout publisherViewContoraler;
    FrameLayout subcribeViewContainer;
    ImageView closeVideoChatBtn;
    private DatabaseReference userRef;
    String userID = "";

    Session session;
    Publisher publisher;
    Subscriber subscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("User");
        closeVideoChatBtn = findViewById(R.id.close_video_chat_btn);
        haveStoragePermission();
        requestPermission();

        closeVideoChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(userID).hasChild("Ringing")) {
                            userRef.child(userID).child("Ringing").removeValue();

                            if (publisher != null) {
                                publisher.destroy();
                            }
                            if (subscriber != null) {
                                subscriber.destroy();
                            }
                            startActivity(new Intent(VideoChatActivity.this, ContexActivity.class));
                            finish();
                        }
                        if (dataSnapshot.child(userID).hasChild("Calling")) {
                            userRef.child(userID).child("Calling").removeValue();
                            if (publisher != null) {
                                publisher.destroy();
                            }
                            if (subscriber != null) {
                                subscriber.destroy();
                            }
                            startActivity(new Intent(VideoChatActivity.this, ContexActivity.class));
                            finish();
                        } else {
                            if (publisher != null) {
                                publisher.destroy();
                            }
                            if (subscriber != null) {
                                subscriber.destroy();
                            }
                            startActivity(new Intent(VideoChatActivity.this, ContexActivity.class));
                            finish();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    public  boolean haveStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(VideoChatActivity.this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED  && ActivityCompat.checkSelfPermission(VideoChatActivity.this,
                    Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED ) {
                //  Log.e("Permission error", "You have permission");
                publisherViewContoraler = findViewById(R.id.publisher_container);
                subcribeViewContainer = findViewById(R.id.subscriber_container);
                //inisialze and conet ot her  sessoin


                session = new Session.Builder(this, API_KEY, SESSION_ID).build();
                session.setSessionListener(VideoChatActivity.this);
                session.connect(TOKEN);

                return true;
            } else {

                //  Log.e("Permission error", "You have asked for permission");
                ActivityCompat.requestPermissions((Activity) VideoChatActivity.this, new String[]{Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO}, RC_VIDEO_APP_PREM);
                Toast.makeText(VideoChatActivity.this, "Need to Permission for Download", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else { //you dont need to worry about these stuff below api level 23
            //  Log.e("Permission error", "You already have the permission");
            return true;
        }
    }

    @AfterPermissionGranted(RC_VIDEO_APP_PREM)
    private void requestPermission() {
        String[] perms = {Manifest.permission.INTERNET, Manifest.permission
                .CAMERA, Manifest.permission.RECORD_AUDIO,};

        if (EasyPermissions.hasPermissions(this, perms)) {
            publisherViewContoraler = findViewById(R.id.publisher_container);
            subcribeViewContainer = findViewById(R.id.subscriber_container);
            //inisialze and conet ot her  sessoin


            session = new Session.Builder(this, API_KEY, SESSION_ID).build();
            session.setSessionListener(VideoChatActivity.this);
            session.connect(TOKEN);
        } else {
            EasyPermissions.requestPermissions(VideoChatActivity.this,
                    "Hey Please Give THE PERMISSION FOR VIDEO CHAT", RC_VIDEO_APP_PREM);
        }
    }

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {

    }

    //PUBLISHING A STREAM TO TEH SESSION
    @Override
    public void onConnected(Session session) {
        Log.i(Log_tag, "SESSION cONNETED");
        publisher = new Publisher.Builder(this).build();
        publisher.setPublisherListener(this);

        publisherViewContoraler.addView(publisher.getView());

        if (publisher.getView() instanceof GLSurfaceView) {
            ((GLSurfaceView) publisher.getView()).setZOrderOnTop(true);
        }
        session.publish(publisher);
    }

    @Override
    public void onDisconnected(Session session) {

    }

    //subcriveing to the stream
    @Override
    public void onStreamReceived(Session session, Stream stream) {

        Log.i(Log_tag, "Steram RECIVED");

        if (subscriber == null) {
            subscriber = new Subscriber.Builder(VideoChatActivity.this, stream).build();
            session.subscribe(subscriber);
            subcribeViewContainer.addView(subscriber.getView());
        }
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {

        Log.i(Log_tag, "Stream DROPED");
        if (subscriber != null) {
            subscriber = null;
            subcribeViewContainer.removeAllViews();
        }
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {
        Log.i(Log_tag, "Stream ERROER");
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}