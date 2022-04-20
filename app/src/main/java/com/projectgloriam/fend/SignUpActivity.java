package com.projectgloriam.fend;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText email, password, confirm_password;
    Button submit, signin_redirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Handle login click
        submit = findViewById(R.id.registerButton);

        signin_redirect = findViewById(R.id.signInRedirectButton);

        handleRegisterClick();
        handleSignInRedirect();
    }

    private void handleSignInRedirect() {
        signin_redirect.setOnClickListener(view -> {
            Intent redirect = new Intent(getApplicationContext(), EmailPasswordActivity.class);
            startActivity(redirect);
        });
    }

    private void handleRegisterClick() {
        //Setting onClickListeners
        submit.setOnClickListener(view -> {

            //Email
            email = findViewById(R.id.editTextTextEmailAddress);

            //Password
            password = findViewById(R.id.editTextTextPassword);

            //Confirm password
            confirm_password = findViewById(R.id.editTextTextConfirmPassword);

            //Check empty fields
            if (email.getText().toString().matches("")
                    || password.getText().toString().matches("")
                    || confirm_password.getText().toString().matches("") ) {
                Toast.makeText(this, R.string.blank_field, Toast.LENGTH_SHORT).show();
                return;
            }

            //Checking password length is 5 or more
            if (password.getText().length() < 5 || confirm_password.getText().length() < 5 ) {
                Toast.makeText(this, R.string.invalid_password_length, Toast.LENGTH_SHORT).show();
                return;
            }

            //Comparing passwords
            if ( !password.getText().toString().equals(confirm_password.getText().toString()) ) {
                Toast.makeText(this, R.string.passwords_dont_match, Toast.LENGTH_SHORT).show();
                return;
            }

            //Sign up user
            createAccount(email.getText().toString(), password.getText().toString());
        });
    }

    //Sign up new users
    private void createAccount(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI();
                        }
                    }
                });
    }

    //Navigate to main activity
    private void updateUI(FirebaseUser user) {
        // User is signed in
        Intent mainActivity = new Intent(this, MainActivity.class);
        mainActivity.putExtra("user", user);
        startActivity(mainActivity);
    }
}