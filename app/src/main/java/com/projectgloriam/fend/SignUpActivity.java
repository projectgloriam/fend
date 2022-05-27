package com.projectgloriam.fend;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Handle login click
        submit = findViewById(R.id.registerButton);

        signin_redirect = findViewById(R.id.signInRedirectButton);

        handleRegisterClick();
        handleSignInRedirect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) searchItem.getActionView();

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        return super.onCreateOptionsMenu(menu);
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
                            sendEmailVerification();
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

    //Send a user a verification email
    public void sendEmailVerification() {
        // [START send_email_verification]
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                            verificationAlert().show();
                        }
                    }
                });
        // [END send_email_verification]
    }

    private AlertDialog verificationAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.verification_sent);
        // Add the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                backToLogin();
            }
        });

        // Create the AlertDialog
        return builder.create();
    }

    //Navigate to email activity
    private void backToLogin() {
        Intent emailPasswordActivity = new Intent(this, EmailPasswordActivity.class);
        emailPasswordActivity.putExtra("verify", true);
        startActivity(emailPasswordActivity);
    }
}