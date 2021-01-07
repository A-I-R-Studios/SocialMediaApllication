package com.matrix.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.Queue;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.matrix.instagram.R.layout.all_user_display_layout;

public class FindFriendsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageView SearchButton;
    private EditText SearchInputText;
    private RecyclerView search_list;
    private DatabaseReference allUserDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        allUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");

        SearchButton = findViewById(R.id.search_people_friend_button);
        SearchInputText = findViewById(R.id.search_box_input);

        mToolbar = findViewById(R.id.find_friends_appbar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("find friends");

        search_list = findViewById(R.id.search_result_list);
        search_list.setLayoutManager(new LinearLayoutManager(this));

        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchBoxInput = SearchInputText.getText().toString();
                Display_all_details(searchBoxInput);
            }
        });

    }

    public void Display_all_details(String searchBoxInput){
        Toast.makeText(this, "Searching...", Toast.LENGTH_SHORT).show();
        Query query = allUserDatabaseRef.orderByChild("Username").startAt(searchBoxInput).endAt(searchBoxInput + "\uf8ff");
        FirebaseRecyclerAdapter<Find_Friends,Find_Friends_ViewHolder> find_friends_viewholder = new FirebaseRecyclerAdapter<Find_Friends, Find_Friends_ViewHolder>(Find_Friends.class, all_user_display_layout,Find_Friends_ViewHolder.class,query) {
            @Override
            protected void populateViewHolder(Find_Friends_ViewHolder find_friends_viewHolder, Find_Friends find_friends, int i) {

                find_friends_viewHolder.setUsername(find_friends.getUsername());
                find_friends_viewHolder.setStatus(find_friends.getStatus());
                find_friends_viewHolder.setProfileImage(getApplicationContext(),find_friends.getProfileImage());

                find_friends_viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visits_user_id = getRef(i).getKey();
                        Intent profileIntent = new Intent(FindFriendsActivity.this,PersonProfileActivity.class);
                        profileIntent.putExtra("visits_user_id",visits_user_id);
                        startActivity(profileIntent);
                    }
                });
            }
        };
       search_list.setAdapter(find_friends_viewholder);
    }

    public static class Find_Friends_ViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public Find_Friends_ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setUsername(String username){
            TextView userName = mView.findViewById(R.id.all_users_profile_name);
            userName.setText(username);
        }

        public void setStatus(String status){
            TextView username = mView.findViewById(R.id.all_users_profile_status);
            username.setText(status);
        }
        public void setProfileImage(Context ctx, String profileImage){
            CircleImageView myImage = mView.findViewById(R.id.all_users_profile_image);
            Picasso.with(ctx).load(profileImage).placeholder(R.drawable.profile).into(myImage);

        }

    }

}






    

