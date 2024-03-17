package com.example.unixplore;

//import static com.example.unixplore.R.id.progessBar;
//import static com.example.unixplore.R.id.imageView_show_hide_pwd;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

//import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginPage extends AppCompatActivity {

    private EditText editTextLoginEmail, editTextLoginPwd;

    private ProgressBar progressBar;

    private FirebaseAuth authProfile;

    private static final String TAG = "LoginPage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Login"); // or whatever title you want to set
        }

        //getSupportActionBar().setTitle("Login");

        editTextLoginEmail = findViewById(R.id.editText_login_email);
        editTextLoginPwd = findViewById(R.id.editText_login_pwd);
        progressBar = findViewById(R.id.progessBar);

        authProfile = FirebaseAuth.getInstance();

        ImageView imageViewShowHidePwd = findViewById(R.id.imageView_show_hide_pwd);
        imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
        imageViewShowHidePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextLoginPwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    editTextLoginPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
                } else {
                    editTextLoginPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });

        Button buttonLogin = findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(view -> {

            if (authProfile.getCurrentUser() != null){
                Toast.makeText(LoginPage.this, "Already Logged In", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(LoginPage.this, ProfileFragment.class));
                finish();

            }
            else {
                String textEmail = editTextLoginEmail.getText().toString();
                String textPwd = editTextLoginPwd.getText().toString();
                if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(LoginPage.this, "Please enter your Email Address", Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("Email Address is required");
                    editTextLoginEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(LoginPage.this, "Please re-enter your Email Address", Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("Valid Email Address is required");
                    editTextLoginEmail.requestFocus();
                } else if (TextUtils.isEmpty(textPwd)) {
                    Toast.makeText(LoginPage.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    editTextLoginPwd.setError("Password is required");
                    editTextLoginPwd.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(textEmail, textPwd);
                }
            }
        });







//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
    }

    private void loginUser(String email, String pwd) {

        authProfile.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(LoginPage.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser firebaseUser = authProfile.getCurrentUser();

                    assert firebaseUser != null;
                    if (firebaseUser.isEmailVerified()){
                        Toast.makeText(LoginPage.this, "You are logged in now", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginPage.this, ProfileFragment.class));
                        finish();
                    } else {
                        firebaseUser.sendEmailVerification();
                        authProfile.signOut();
                        showAlertDialog();
                    }


                } else {
                    try {
                        throw Objects.requireNonNull(task.getException());
                    } catch(FirebaseAuthInvalidUserException e){
                        editTextLoginEmail.setError("User does not exists or is no longer valid. Please register.");
                        editTextLoginEmail.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        editTextLoginEmail.setError("Invalid credentials. Kindly check and re-enter again.");
                        editTextLoginEmail.requestFocus();
                    } catch (Exception e){
                        Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                        Toast.makeText(LoginPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    private void showAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginPage.this);
        builder.setTitle("Email Not Verified");
        builder.setMessage("Please verify your email now. You cannot login without email verification");

        builder.setPositiveButton("Continue", (dialogInterface, i) -> {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_APP_EMAIL);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        AlertDialog alertDialog = builder.create();

        alertDialog.show();

    }

    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = authProfile.getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified()) {
            // If user is already logged in and email is verified, navigate to the profile page
            Toast.makeText(LoginPage.this, "Already Logged In", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginPage.this, ProfileFragment.class));
            finish();
        } else if (currentUser != null && !currentUser.isEmailVerified()) {
            // If user is logged in but email is not verified, show alert dialog
            showAlertDialog();
        } else {
            // If user is not logged in, show a toast indicating they can log in now
            Toast.makeText(LoginPage.this, "You can log in now", Toast.LENGTH_SHORT).show();
        }
    }
}