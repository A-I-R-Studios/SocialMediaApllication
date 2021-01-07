package com.matrix.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private TextView userName,userProfName,userStatus,userCountry,userGender,userRelation,userDob;
    private CircleImageView userProfImage;
    private DatabaseReference profileUserRef , FriendsRef , PostsRef;
    private FirebaseAuth mAuth;
    private String CurrentUserID;
    private Button MyPosts , MyFriends;
    private int countFriends = 0 , countPosts = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth  = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();
        profileUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentUserID);
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");

        userName = findViewById(R.id.my_username);
        userProfName = findViewById(R.id.my_profile_full_name);
        userStatus = findViewById(R.id.my_profile_status);
        userCountry = findViewById(R.id.my_country);
        userGender = findViewById(R.id.my_gender);
        userRelation = findViewById(R.id.my_relationship_status);
        userDob = findViewById(R.id.my_dob);
        userProfImage = findViewById(R.id.my_profile_pic);
        MyFriends = findViewById(R.id.my_post_button);
        MyPosts = findViewById(R.id.my_friends_button);
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        FriendsRef.child(CurrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    countFriends = (int) dataSnapshot.getChildrenCount();
                    MyFriends.setText(Integer.toString(countFriends)+" Friends");
                }else {
                    MyFriends.setText("0 Friends");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        MyFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToFriendsActivity();
            }
        });
        MyPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToPostsActivity();
            }
        });

        PostsRef.orderByChild("uid").startAt(CurrentUserID).endAt(CurrentUserID+"\uf8f8").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    countPosts = (int) dataSnapshot.getChildrenCount();
                    MyPosts.setText(Integer.toString(countPosts)+" Posts");
                }else {
                    MyPosts.setText("0 Posts");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        profileUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("ProfileImage")) {
                        String myProfileImage = dataSnapshot.child("ProfileImage").getValue().toString();
                        String myUserName = dataSnapshot.child("Username").getValue().toString();
                        String myProfileName = dataSnapshot.child("FUllName").getValue().toString();
                        String myProfileStatus = dataSnapshot.child("Status").getValue().toString();
                        String myDob = dataSnapshot.child("DOB").getValue().toString();
                        String myCountry = dataSnapshot.child("Country").getValue().toString();
                        String myGender = dataSnapshot.child("Gender").getValue().toString();
                        String myRelationshipStatus = dataSnapshot.child("RelationShipStatus").getValue().toString();

                        Picasso.with(ProfileActivity.this).load(myProfileImage).placeholder(R.drawable.profile).into(userProfImage);
                        userName.setText("@" + myUserName);
                        userProfName.setText(myProfileName);
                        userStatus.setText(myProfileStatus);
                        userDob.setText("DOB : " + myDob);
                        userCountry.setText("Country : " + myCountry);
                        userGender.setText("Gender : " + myGender);
                        userRelation.setText("Relationship Status : " + myRelationshipStatus);
                    }else {

                        String myUserName = dataSnapshot.child("Username").getValue().toString();
                        String myProfileName = dataSnapshot.child("FUllName").getValue().toString();
                        String myProfileStatus = dataSnapshot.child("Status").getValue().toString();
                        String myDob = dataSnapshot.child("DOB").getValue().toString();
                        String myCountry = dataSnapshot.child("Country").getValue().toString();
                        String myGender = dataSnapshot.child("Gender").getValue().toString();
                        String myRelationshipStatus = dataSnapshot.child("RelationShipStatus").getValue().toString();


                        userName.setText("@" + myUserName);
                        userProfName.setText(myProfileName);
                        userStatus.setText(myProfileStatus);
                        userDob.setText("DOB : " + myDob);
                        userCountry.setText("Country : " + myCountry);
                        userGender.setText("Gender : " + myGender);
                        userRelation.setText("Relationship Status : " + myRelationshipStatus);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void SendUserToFriendsActivity(){
        Intent friendIntent = new Intent(ProfileActivity.this,FriendActivity.class);
        startActivity(friendIntent);
    }
    private void SendUserToPostsActivity(){
        Intent postIntent = new Intent(ProfileActivity.this,MyPostsActivity.class);
        startActivity(postIntent);
    }
}