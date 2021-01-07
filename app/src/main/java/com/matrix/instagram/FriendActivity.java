package com.matrix.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendActivity extends AppCompatActivity {

    private RecyclerView myFriendList;
    private DatabaseReference FriendsRef ,UserRef;
    private FirebaseAuth mAuth;
    private String online_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(online_user_id);
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");


        myFriendList = findViewById(R.id.friend_list);
        myFriendList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myFriendList.setLayoutManager(linearLayoutManager);

        DisplayAllFriends();
    }

    private void updateUserStatus(String state){
        String SaveCurrentDate , SaveCurrentTime;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        SaveCurrentDate  = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        SaveCurrentTime = currentTime.format(calForDate.getTime());

        Map currentStateMap = new HashMap();
        currentStateMap.put("time",SaveCurrentTime);
        currentStateMap.put("date",SaveCurrentDate);
        currentStateMap.put("type",state);
        UserRef.child(online_user_id).child("userState").updateChildren(currentStateMap);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUserStatus("online");
    }

    @Override
    protected void onStop() {
        super.onStop();
        updateUserStatus("offline");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateUserStatus("offline");
    }



    private void DisplayAllFriends() {
        FirebaseRecyclerAdapter<Friends,FriendsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>
                (
                        Friends.class,
                        R.layout.all_user_display_layout,
                        FriendsViewHolder.class,
                        FriendsRef

                )
        {
            @Override
            protected void populateViewHolder(FriendsViewHolder friendsViewHolder, Friends friends, int i) {

                friendsViewHolder.setDate(friends.getDate());
                final String userIds = getRef(i).getKey();
                UserRef.child(userIds).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            final String userName = dataSnapshot.child("Username").getValue().toString();
                            final String profileImage = dataSnapshot.child("ProfileImage").getValue().toString();
                            final String type;

                            if (dataSnapshot.hasChild("userState")){
                                type = dataSnapshot.child("userState").child("type").getValue().toString();
                                if (type.equals("online")){
                                    friendsViewHolder.onlineStatusView.setVisibility(View.VISIBLE);
                                }else {
                                    friendsViewHolder.onlineStatusView.setVisibility(View.INVISIBLE);
                                }
                            }

                            friendsViewHolder.setUsername(userName);
                            friendsViewHolder.setProfileImage(getApplicationContext(),profileImage);

                            friendsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CharSequence option[]=new CharSequence[]{
                                            userName + "'s Profile",
                                            "Send Message"
                                    };
                                    AlertDialog.Builder builder = new AlertDialog.Builder(FriendActivity.this);
                                    builder.setTitle("Select Option");
                                    builder.setItems(option, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(which == 0){
                                                Intent profileIntent = new Intent(FriendActivity.this,PersonProfileActivity.class);
                                                profileIntent.putExtra("visits_user_id",userIds);
                                                startActivity(profileIntent);
                                            }
                                            if (which ==1){
                                                Intent profileIntent = new Intent(FriendActivity.this,ChatActivity.class);
                                                profileIntent.putExtra("visits_user_id",userIds);
                                                profileIntent.putExtra("userName",userName);
                                                startActivity(profileIntent);
                                            }
                                        }
                                    });
                                    builder.show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        myFriendList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder{

            View mView;
            ImageView onlineStatusView;
            public FriendsViewHolder(@NonNull View itemView) {
                super(itemView);
                mView = itemView;

                onlineStatusView = itemView.findViewById(R.id.all_user_online_icon);

            }

        public void setUsername(String username){
            TextView userName = mView.findViewById(R.id.all_users_profile_name);
            userName.setText(username);
        }
        public void setDate(String date){
            TextView datex = mView.findViewById(R.id.all_users_profile_status);
            datex.setText("Friends since: "+date);
        }

        public void setProfileImage(Context ctx, String profileImage){
            CircleImageView myImage = mView.findViewById(R.id.all_users_profile_image);
            Picasso.with(ctx).load(profileImage).placeholder(R.drawable.profile).into(myImage);
        }
        }

}