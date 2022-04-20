package com.projectgloriam.fend;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailPasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText email, password;
    Button submit, signup_redirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_password);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Handle login click
        submit = findViewById(R.id.loginButton);

        signup_redirect = findViewById(R.id.signUpRedirectButton);

        handleLoginClick();

        handleSignUpRedirect();
    }

    private void handleSignUpRedirect() {
        signup_redirect.setOnClickListener(view -> {
            Intent redirect = new Intent(getApplicationContext(), SignUpActivity.class);
            startActivity(redirect);
        });
    }

    private void handleLoginClick() {
        //Setting onClickListeners
        submit.setOnClickListener(view -> {

            //Email
            email = findViewById(R.id.editTextTextEmailAddress);

            //Password
            password = findViewById(R.id.editTextTextPassword);

            //Check empty fields
            if (email.getText().toString().matches("") || password.getText().toString().matches("") ) {
                Toast.makeText(this, "Please don't leave any field blank", Toast.LENGTH_SHORT).show();
                return;
            }

            //Sign in user
            signInWithEmailAndPassword(email.getText().toString(), password.getText().toString());
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload(currentUser);
        }
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

    private void reload(FirebaseUser user) {
        updateUI(user);
    }

    //Sign in existing users
    private void signInWithEmailAndPassword(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
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