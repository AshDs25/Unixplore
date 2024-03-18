package com.example.unixplore;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

//import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterPage extends AppCompatActivity {

    private EditText editTextRegisterFullName, editTextRegisterEmail, editTextRegisterDoB, editTextRegisterMobile, editTextRegisterPwd, editTextRegisterConfirmPwd;

    private ProgressBar progressBar;

    private RadioGroup radioGroupRegisterGender;

    private RadioButton radioButtonRegisterGenderSelected;

    private DatePickerDialog picker;

    private static final String TAG= "RegisterPage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_page);

//        getSupportActionBar().setTitle("Register");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Register");
        }

        Toast.makeText(RegisterPage.this, "You can register now", Toast.LENGTH_LONG).show();

        progressBar = findViewById(R.id.progessBar);
        editTextRegisterFullName = findViewById(R.id.editText_register_full_name);
        editTextRegisterEmail = findViewById(R.id.editText_register_email);
        editTextRegisterDoB = findViewById(R.id.editText_register_dob);
        editTextRegisterMobile = findViewById(R.id.editText_register_mobile);
        editTextRegisterPwd = findViewById(R.id.editText_register_password);
        editTextRegisterConfirmPwd = findViewById(R.id.editText_register_confirm_password);

        radioGroupRegisterGender = findViewById(R.id.radio_group_register_gender);
        radioGroupRegisterGender.clearCheck();

        editTextRegisterDoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final  Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                picker = new DatePickerDialog(RegisterPage.this, new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextRegisterDoB.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        Button buttonRegister = findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(view -> {
            int selectedGenderId = radioGroupRegisterGender.getCheckedRadioButtonId();
            radioButtonRegisterGenderSelected = findViewById(selectedGenderId);

            String textFullName = editTextRegisterFullName.getText().toString();
            String textEmail = editTextRegisterEmail.getText().toString();
            String textDoB = editTextRegisterDoB.getText().toString();
            String textMobile = editTextRegisterMobile.getText().toString();
            String textPwd = editTextRegisterPwd.getText().toString();
            String textConfirmPwd = editTextRegisterConfirmPwd.getText().toString();
            String textGender;


            String mobileRegex = "[6-9][0-9]{9}";
            Matcher mobileMatcher;
            Pattern mobilePattern = Pattern.compile(mobileRegex);
            mobileMatcher = mobilePattern.matcher(textMobile);

            if (TextUtils.isEmpty(textFullName)) {
                Toast.makeText(RegisterPage.this, "Enter your full name", Toast.LENGTH_LONG).show();
                editTextRegisterFullName.setError("Full name is required");
                editTextRegisterFullName.requestFocus();
            } else if (TextUtils.isEmpty(textEmail)) {
                Toast.makeText(RegisterPage.this, "Enter your email", Toast.LENGTH_LONG).show();
                editTextRegisterEmail.setError("Email is required");
                editTextRegisterEmail.requestFocus();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                Toast.makeText(RegisterPage.this, "Re-enter your email", Toast.LENGTH_LONG).show();
                editTextRegisterEmail.setError("Valid email is required");
                editTextRegisterEmail.requestFocus();
            } else if (TextUtils.isEmpty(textDoB)) {
                Toast.makeText(RegisterPage.this, "Enter your date of birth", Toast.LENGTH_LONG).show();
                editTextRegisterDoB.setError("Date of Birth is required");
                editTextRegisterDoB.requestFocus();
            } else if (radioGroupRegisterGender.getCheckedRadioButtonId() == -1) {
                Toast.makeText(RegisterPage.this, "Select your gender", Toast.LENGTH_LONG).show();
                radioButtonRegisterGenderSelected.setError("Gender is required");
                radioButtonRegisterGenderSelected.requestFocus();
            } else if (TextUtils.isEmpty(textMobile)) {
                Toast.makeText(RegisterPage.this, "Enter your mobile number", Toast.LENGTH_LONG).show();
                editTextRegisterMobile.setError("Mobile number is required");
                editTextRegisterMobile.requestFocus();
            } else if (textMobile.length() != 10) {
                Toast.makeText(RegisterPage.this, "Re-enter your mobile number", Toast.LENGTH_LONG).show();
                editTextRegisterMobile.setError("Mobile number should be of 10 digits");
                editTextRegisterMobile.requestFocus();
            } else if (!mobileMatcher.find()) {
                Toast.makeText(RegisterPage.this, "Re-enter your mobile number", Toast.LENGTH_LONG).show();
                editTextRegisterMobile.setError("Mobile number entered is not valid");
                editTextRegisterMobile.requestFocus();
            } else if (TextUtils.isEmpty(textPwd)) {
                Toast.makeText(RegisterPage.this, "Enter your password", Toast.LENGTH_LONG).show();
                editTextRegisterPwd.setError("Password is required");
                editTextRegisterPwd.requestFocus();
            } else if (textPwd.length() < 6) {
                Toast.makeText(RegisterPage.this, "Password should be at least 6 digits long", Toast.LENGTH_LONG).show();
                editTextRegisterPwd.setError("Password is too weak");
                editTextRegisterPwd.requestFocus();
            } else if (TextUtils.isEmpty(textConfirmPwd)) {
                Toast.makeText(RegisterPage.this, "Confirm your password", Toast.LENGTH_LONG).show();
                editTextRegisterConfirmPwd.setError("Password confirmation is required");
                editTextRegisterConfirmPwd.requestFocus();
            } else if (!textPwd.equals(textConfirmPwd)) {
                Toast.makeText(RegisterPage.this, "Please enter correct password", Toast.LENGTH_LONG).show();
                editTextRegisterConfirmPwd.setError("Password confirmation is required");
                editTextRegisterConfirmPwd.requestFocus();
                editTextRegisterPwd.clearComposingText();
                editTextRegisterConfirmPwd.clearComposingText();
            } else {
                textGender = radioButtonRegisterGenderSelected.getText().toString();
                progressBar.setVisibility(View.VISIBLE);
                registerUser(textFullName, textEmail, textDoB, textGender, textMobile, textPwd);
            }

        });












//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
    }

    private void registerUser(String textFullName, String textEmail, String textDoB, String textGender, String textMobile, String textPwd) {

        FirebaseAuth auth = FirebaseAuth.getInstance();



        auth.createUserWithEmailAndPassword(textEmail, textPwd).addOnCompleteListener(RegisterPage.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                    firebaseUser.updateProfile(profileChangeRequest);


                    ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textDoB, textGender, textMobile);


                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

                    referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                firebaseUser.sendEmailVerification();

                                Toast.makeText(RegisterPage.this, "User registered successfully. Kindly verify your Email Address", Toast.LENGTH_LONG).show();


                                Intent intent = new Intent(RegisterPage.this, ProfilePage.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(RegisterPage.this, "User registration failed. Please retry again", Toast.LENGTH_LONG).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });

                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e){
                        editTextRegisterPwd.setError("Your password is too weak. Kindly use a mix of alphabets, numbers and special characters");
                        editTextRegisterPwd.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        editTextRegisterPwd.setError("Your email is invalid or already registered. Kindly use new email address");
                        editTextRegisterPwd.requestFocus();
                    } catch (FirebaseAuthUserCollisionException e){
                        editTextRegisterPwd.setError("User is already registered with this email address. Kindly use another email address");
                        editTextRegisterPwd.requestFocus();
                    } catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(RegisterPage.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}