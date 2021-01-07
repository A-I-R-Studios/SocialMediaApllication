package com.matrix.instagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText userName,userProfName,userStatus,userCountry,userGender,userRelation,userDob;
    private CircleImageView userProfImage;
    private Button UpdateAccountSettingsButton;
    private FirebaseAuth mAuth;
    private DatabaseReference SettingsuserRef;
    private String CurrentUserId;
    private StorageReference UserProfileImageRef;
    final static int Gallery_Pick = 1;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth  = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        SettingsuserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentUserId);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        loadingbar= new ProgressDialog(this);

        mToolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userName = findViewById(R.id.settings_username);
        userProfName = findViewById(R.id.settings_profile_full_name);
        userStatus = findViewById(R.id.settings_status);
        userCountry = findViewById(R.id.settings_country);
        userGender = findViewById(R.id.settings_gender);
        userRelation = findViewById(R.id.settings_relationship_status);
        userDob = findViewById(R.id.settings_dob);
        userProfImage = findViewById(R.id.settings_profile_image);
        UpdateAccountSettingsButton = findViewById(R.id.update_account_settings_button);

        SettingsuserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    if(dataSnapshot.hasChild("ProfileImage")) {
                        String myProfileImage = dataSnapshot.child("ProfileImage").getValue().toString();
                        String myUserName = dataSnapshot.child("Username").getValue().toString();
                        String myProfileName = dataSnapshot.child("FUllName").getValue().toString();
                        String myProfileStatus = dataSnapshot.child("Status").getValue().toString();
                        String myDob = dataSnapshot.child("DOB").getValue().toString();
                        String myCountry = dataSnapshot.child("Country").getValue().toString();
                        String myGender = dataSnapshot.child("Gender").getValue().toString();
                        String myRelationshipStatus = dataSnapshot.child("RelationShipStatus").getValue().toString();

                        Picasso.with(SettingsActivity.this).load(myProfileImage).placeholder(R.drawable.profile).into(userProfImage);
                        userName.setText(myUserName);
                        userProfName.setText(myProfileName);
                        userStatus.setText(myProfileStatus);
                        userDob.setText(myDob);
                        userCountry.setText(myCountry);
                        userGender.setText(myGender);
                        userRelation.setText(myRelationshipStatus);
                    }else {
                        String myUserName = dataSnapshot.child("Username").getValue().toString();
                        String myProfileName = dataSnapshot.child("FUllName").getValue().toString();
                        String myProfileStatus = dataSnapshot.child("Status").getValue().toString();
                        String myDob = dataSnapshot.child("DOB").getValue().toString();
                        String myCountry = dataSnapshot.child("Country").getValue().toString();
                        String myGender = dataSnapshot.child("Gender").getValue().toString();
                        String myRelationshipStatus = dataSnapshot.child("RelationShipStatus").getValue().toString();

                        userName.setText(myUserName);
                        userProfName.setText(myProfileName);
                        userStatus.setText(myProfileStatus);
                        userDob.setText(myDob);
                        userCountry.setText(myCountry);
                        userGender.setText(myGender);
                        userRelation.setText(myRelationshipStatus);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        UpdateAccountSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateAccountInfo();
            }
        });
        userProfImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,Gallery_Pick);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null){
            Uri ImageUri  = data.getData();
            CropImage.activity(ImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                loadingbar.setTitle("Profile Image");
                loadingbar.setMessage("Please wait, while we are uploading your profile image");
                loadingbar.setCanceledOnTouchOutside(true);
                loadingbar.show();
                Uri resulturi = result.getUri();
                StorageReference filepath = UserProfileImageRef.child(CurrentUserId + ".jpg");
                filepath.putFile(resulturi).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {


                        if (task.isSuccessful()){
                            Toast.makeText(SettingsActivity.this, "Image upload successfully", Toast.LENGTH_SHORT).show();
                            final String downloadUrl = task.getResult().getDownloadUrl().toString();
                            SettingsuserRef.child("ProfileImage").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Intent selfIntent = new Intent(SettingsActivity.this,SettingsActivity.class);
                                        startActivity(selfIntent);
                                        loadingbar.dismiss();
                                        Toast.makeText(SettingsActivity.this, "Profile Image upload to database successfully", Toast.LENGTH_SHORT).show();
                                    }else {
                                        loadingbar.dismiss();
                                        String message = task.getException().toString();
                                        Toast.makeText(SettingsActivity.this, "Error Occurred : "+message, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });
            }else {
                loadingbar.dismiss();
                Toast.makeText(this, "Image Can't be cropped. Try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void ValidateAccountInfo() {
        String username = userName.getText().toString();
        String profilename = userProfName.getText().toString();
        String status = userStatus.getText().toString();
        String dob = userDob.getText().toString();
        String country = userCountry.getText().toString();
        String gender = userGender.getText().toString();
        String relation = userRelation.getText().toString();

        if (TextUtils.isEmpty(username)){
            Toast.makeText(this, "please write your username...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(profilename)){
            Toast.makeText(this, "please write your profile name...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(status)){
            Toast.makeText(this, "please write your Status...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(dob)){
            Toast.makeText(this, "please write your Date of Birth...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(country)){
            Toast.makeText(this, "please write your Country...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(gender)){
            Toast.makeText(this, "please write your Gender...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(relation)){
            Toast.makeText(this, "please write your Relationship Status...", Toast.LENGTH_SHORT).show();
        }else {
            loadingbar.setTitle("Profile Image");
            loadingbar.setMessage("Please wait, while we are uploading your profile image");
            loadingbar.setCanceledOnTouchOutside(true);
            loadingbar.show();
            UpdateAccountinfo(username,profilename,status,dob,country,gender,relation);
        }

    }

    private void UpdateAccountinfo(String username, String profilename, String status, String dob, String country, String gender, String relation) {
        HashMap userMap = new HashMap();
        userMap.put("Username",username);
        userMap.put("FUllName",profilename);
        userMap.put("Status",status);
        userMap.put("DOB",dob);
        userMap.put("Country",country);
        userMap.put("Gender",gender);
        userMap.put("RelationShipStatus",relation);
        SettingsuserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    SendUserToMainActivity();
                    Toast.makeText(SettingsActivity.this, "Account Setting updated successfully", Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                }else {
                    String message = task.getException().toString();
                    Toast.makeText(SettingsActivity.this, "Error :"+message, Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                }
            }
        });
    }

    private void SendUserToMainActivity() {
        Intent main_intent = new Intent(SettingsActivity.this,MainActivity.class);
        main_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(main_intent);
        finish();
    }
}