package com.naptechlabs.skypeclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

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

    private static String API_KEY = "";
    private static String SESSION_ID = "";
    private static String TOKEN = "";
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
                            startActivity(new Intent(VideoChatActivity.this, Registration.class));
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
                            startActivity(new Intent(VideoChatActivity.this, Registration.class));
                            finish();
                        } else {
                            if (publisher != null) {
                                publisher.destroy();
                            }
                            if (subscriber != null) {
                                subscriber.destroy();
                            }
                            startActivity(new Intent(VideoChatActivity.this, Registration.class));
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