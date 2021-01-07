package com.matrix.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText UserEmail,UserPassword,UserConfirmPassword;
    private Button CreateAccountButton;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();

        UserEmail = findViewById(R.id.register_email);
        UserPassword = findViewById(R.id.register_password);
        UserConfirmPassword = findViewById(R.id.register_confirm_password);
        CreateAccountButton = findViewById(R.id.register_create_account);
        loadingbar = new ProgressDialog(this);

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
            }
        });
    }

    private void CreateNewAccount() {
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();
        String confirmpassword = UserConfirmPassword.getText().toString();

        try {
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Please write your email...", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "please write your password...", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(confirmpassword)) {
                Toast.makeText(this, "please confirm your password...", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmpassword)) {
                Toast.makeText(this, "Your password do not match with your confirm password", Toast.LENGTH_SHORT).show();
            }else {
                loadingbar.setTitle("Creating New Account");
                loadingbar.setMessage("Please wait, while we are creating your new account");
                loadingbar.show();
                loadingbar.setCanceledOnTouchOutside(true);
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        SendEmailVerificationMessage();
                        loadingbar.dismiss();
                    }else {
                        String message = task.getException().getMessage();
                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                        loadingbar.dismiss();
                    }
                }
            });

            }
        }catch (Exception e){
            String error = e.getMessage();
            Toast.makeText(this, "Please Check Your Internet Connection and "+error, Toast.LENGTH_SHORT).show();
        }

    }

    private void SendEmailVerificationMessage(){
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        SendUserToSetupActvity();
                        Toast.makeText(RegisterActivity.this, "Registration Successfully, we've sent you a email. Please check and verify your account", Toast.LENGTH_LONG).show();
                        mAuth.signOut();
                    }else {
                        String Message = task.getException().toString();
                        mAuth.signOut();
                        Toast.makeText(RegisterActivity.this, "Error"+Message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private void SendUserToSetupActvity() {
        Intent setupIntent = new Intent(RegisterActivity.this,LoginActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser CurrentUser = mAuth.getCurrentUser();
        if (CurrentUser != null){
            SendUserToMainActivity();
        }

    }

    private void SendUserToMainActivity() {
        Intent main_intent = new Intent(RegisterActivity.this,MainActivity.class);
        main_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(main_intent);
        finish();
    }
}