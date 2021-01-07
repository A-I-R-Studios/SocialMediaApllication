package com.matrix.instagram;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private Button LoginButton;
    private EditText UserEmail,UserPassword;
    private TextView NeedNewAccountLink,ResetPasswordLink;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingbar;
    public Boolean EmailAddressVerifier;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        NeedNewAccountLink = findViewById(R.id.register_account_link);
        UserEmail = findViewById(R.id.login_email);
        UserPassword = findViewById(R.id.login_password);
        LoginButton = findViewById(R.id.login_button);
        ResetPasswordLink = findViewById(R.id.forget_password_link);
        loadingbar= new ProgressDialog(this);


        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowingUserToLogin();
            }
        });

        NeedNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterActivity();
            }
        });

        ResetPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,ResetPasswordActivity.class));
            }
        });


    }

    private void AllowingUserToLogin() {
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter your email....", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter your password....", Toast.LENGTH_SHORT).show();
        }
        else{
            loadingbar.setTitle("Logging in");
            loadingbar.setMessage("Please wait until you are log in");
            loadingbar.setCanceledOnTouchOutside(true);
            loadingbar.show();
          mAuth.signInWithEmailAndPassword(email,password)
        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
         @Override
         public void onComplete(@NonNull Task<AuthResult> task) {
            if(task.isSuccessful()){
                VeriflyEmailAddress();
                loadingbar.dismiss();
            }else {
                String message = task.getException().toString();
                Toast.makeText(LoginActivity.this, "Check your internet connection or password and try again", Toast.LENGTH_SHORT).show();
                loadingbar.dismiss();
            }
         }
        });
        }

    }

    private void VeriflyEmailAddress(){
        FirebaseUser user = mAuth.getCurrentUser();
        EmailAddressVerifier = user.isEmailVerified();

        if (EmailAddressVerifier){
            SendUserToMainActivity();

        }else {
            Toast.makeText(this, "please verify your account first", Toast.LENGTH_LONG).show();
            mAuth.signOut();
        }
    }
    private void SendUserToMainActivity() {
        Intent main_intent = new Intent(LoginActivity.this,MainActivity.class);
        main_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(main_intent);
        finish();
    }

    private void SendUserToRegisterActivity() {
        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser CurrentUser = mAuth.getCurrentUser();
        if (CurrentUser != null){
            SendUserToMainActivity();
        }

    }
}