package com.matrix.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonProfileActivity extends AppCompatActivity {

    private TextView userName,userProfName,userStatus,userCountry,userGender,userRelation,userDob;
    private CircleImageView userProfImage;
    private Button SendFriendRequestBtn,DeclineFriendRequestBtn;
    private DatabaseReference FriendRequestRef,UsersRef,FriendsRef;
    private FirebaseAuth mAuth;
    private String senderUserId,receiveUserId,CURRENT_STATE,saveCurrentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        mAuth  = FirebaseAuth.getInstance();
        senderUserId = mAuth.getCurrentUser().getUid();
        receiveUserId = getIntent().getExtras().get("visits_user_id").toString();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        FriendRequestRef = FirebaseDatabase.getInstance().getReference().child("FriendRequests");
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        InitializeFields();

        UsersRef.child(receiveUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String myProfileImage = dataSnapshot.child("ProfileImage").getValue().toString();
                    String myUserName = dataSnapshot.child("Username").getValue().toString();
                    String myProfileName = dataSnapshot.child("FUllName").getValue().toString();
                    String myProfileStatus = dataSnapshot.child("Status").getValue().toString();
                    String myDob = dataSnapshot.child("DOB").getValue().toString();
                    String myCountry = dataSnapshot.child("Country").getValue().toString();
                    String myGender = dataSnapshot.child("Gender").getValue().toString();
                    String myRelationshipStatus = dataSnapshot.child("RelationShipStatus").getValue().toString();

                    Picasso.with(PersonProfileActivity.this).load(myProfileImage).placeholder(R.drawable.profile).into(userProfImage);
                    userName.setText("@"+myUserName);
                    userProfName.setText(myProfileName);
                    userStatus.setText(myProfileStatus);
                    userDob.setText("DOB : "+myDob);
                    userCountry.setText("Country : "+myCountry);
                    userGender.setText("Gender : "+myGender);
                    userRelation.setText("Relationship Status : "+myRelationshipStatus);
                    MaintainanceofButtons();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DeclineFriendRequestBtn.setVisibility(View.INVISIBLE);
        DeclineFriendRequestBtn.setEnabled(false);

        if (!senderUserId.equals(receiveUserId)){
            SendFriendRequestBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SendFriendRequestBtn.setEnabled(false);
                    if(CURRENT_STATE.equals("not_friends")){
                        SendFriendRequestToaPerson();
                    }if (CURRENT_STATE.equals("request_sent")){
                        CancelFriendRequest();
                    }if (CURRENT_STATE.equals("request_received")){
                        AcceptFriendRequest();
                    }if (CURRENT_STATE.equals("friends")){
                        UnFriendAnExistingFriend();
                    }

                }
            });
        }
        else {
            DeclineFriendRequestBtn.setVisibility(View.INVISIBLE);
            SendFriendRequestBtn.setVisibility(View.INVISIBLE);
        }
    }

    private void UnFriendAnExistingFriend() {
        FriendsRef.child(senderUserId).child(receiveUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    FriendsRef.child(receiveUserId).child(senderUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                SendFriendRequestBtn.setEnabled(true);
                                CURRENT_STATE = "not_friends";
                                SendFriendRequestBtn.setText("send friend request");
                                DeclineFriendRequestBtn.setVisibility(View.INVISIBLE);
                                DeclineFriendRequestBtn.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });
    }

    private void AcceptFriendRequest() {
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        FriendsRef.child(senderUserId).child(receiveUserId).child("date").setValue(saveCurrentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    FriendsRef.child(receiveUserId).child(senderUserId).child("date").setValue(saveCurrentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                FriendRequestRef.child(senderUserId).child(receiveUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            FriendRequestRef.child(receiveUserId).child(senderUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        SendFriendRequestBtn.setEnabled(true);
                                                        CURRENT_STATE = "friends";
                                                        SendFriendRequestBtn.setText("unfriend this person");
                                                        DeclineFriendRequestBtn.setVisibility(View.INVISIBLE);
                                                        DeclineFriendRequestBtn.setEnabled(false);
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    });
                }else {

                }
            }
        });

    }

    private void CancelFriendRequest() {
        FriendRequestRef.child(senderUserId).child(receiveUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    FriendRequestRef.child(receiveUserId).child(senderUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                SendFriendRequestBtn.setEnabled(true);
                                CURRENT_STATE = "not_friends";
                                SendFriendRequestBtn.setText("send friend request");
                                DeclineFriendRequestBtn.setVisibility(View.INVISIBLE);
                                DeclineFriendRequestBtn.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });
    }

    private void MaintainanceofButtons() {
        FriendRequestRef.child(senderUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(receiveUserId)){
                    String request_type = dataSnapshot.child(receiveUserId).child("request_type").getValue().toString();
                    if(request_type.equals("sent")){
                        CURRENT_STATE = "request_sent";
                        SendFriendRequestBtn.setText("Cancel Friend request");

                        DeclineFriendRequestBtn.setVisibility(View.INVISIBLE);
                        DeclineFriendRequestBtn.setEnabled(false);
                    }else if (request_type.equals("received")){
                        CURRENT_STATE = "request_received";
                        SendFriendRequestBtn.setText("Accept Friend Request");
                        DeclineFriendRequestBtn.setVisibility(View.VISIBLE);
                        DeclineFriendRequestBtn.setEnabled(true);

                        DeclineFriendRequestBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CancelFriendRequest();
                            }
                        });
                    }
                }
                else{
                    FriendsRef.child(senderUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(receiveUserId)){
                                CURRENT_STATE = "friends";
                                SendFriendRequestBtn.setText("unfriend this person");
                                DeclineFriendRequestBtn.setVisibility(View.INVISIBLE);
                                DeclineFriendRequestBtn.setEnabled(false);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void SendFriendRequestToaPerson() {
        FriendRequestRef.child(senderUserId).child(receiveUserId).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    FriendRequestRef.child(receiveUserId).child(senderUserId).child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                SendFriendRequestBtn.setEnabled(true);
                                CURRENT_STATE = "request_sent";
                                SendFriendRequestBtn.setText("Cancel friend request");
                                DeclineFriendRequestBtn.setVisibility(View.INVISIBLE);
                                DeclineFriendRequestBtn.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });
    }

    private void InitializeFields() {
        userName = findViewById(R.id.person_username);
        userProfName = findViewById(R.id.person_full_name);
        userStatus = findViewById(R.id.person_profile_status);
        userCountry = findViewById(R.id.person_country);
        userGender = findViewById(R.id.person_gender);
        userRelation = findViewById(R.id.person_relationship_status);
        userDob = findViewById(R.id.person_dob);
        userProfImage = findViewById(R.id.person_profile_pic);
        SendFriendRequestBtn = findViewById(R.id.person_send_friend_request_btn);
        DeclineFriendRequestBtn = findViewById(R.id.person_decline_friend_request_btn);
        CURRENT_STATE = "not_friends";
    }
}