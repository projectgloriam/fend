package com.projectgloriam.fend;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.projectgloriam.fend.models.User;


public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private Toolbar app_bar;
    AppBarConfiguration appBarConfiguration;
    NavController navController;
    private FirebaseStorage storage;
    public StorageReference storageRef;
    public FirebaseFirestore firebaseDb; // Access a Cloud Firestore instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkCurrentUser();

        initNavigationUI();

        initFirebaseDb();

        initFirebaseStorage();
    }

    private void initFirebaseDb() {
        firebaseDb = FirebaseFirestore.getInstance();
    }

    private void initFirebaseStorage() {

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }

    private void initNavigationUI() {
        //getting the drawer layout view
        drawerLayout = findViewById(R.id.drawer_layout);

        //Setting up nav controller (a navigation function) for nav fragment view
        navController = Navigation.findNavController(this, R.id.fragment);
        appBarConfiguration =  new AppBarConfiguration.Builder(navController.getGraph())
                .setDrawerLayout(drawerLayout)
                .build();

        //setting toolbar as app bar: as its the standard nowadays. Pretty straight forward
        app_bar = findViewById(R.id.app_bar);

        setSupportActionBar(app_bar);

        //adding navigation view of the drawer to navigation UI
        navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener( item -> {
            switch (item.getItemId()) {

                case R.id.logout: {
                    signOut();
                    break;
                }

                default: {
                    // Fallback for all other (normal) cases.
                    boolean handled = NavigationUI.onNavDestinationSelected(item, navController);

                    // This is usually done by the default ItemSelectedListener.
                    // But there can only be one! Unfortunately.
                    if (handled) drawerLayout.closeDrawer(navView);

                    // return the result of NavigationUI call
                    return handled;
                }
            }

            return false;
        });

        //setup Navigation View with Navigation UI
        NavigationUI.setupWithNavController(app_bar, navController, appBarConfiguration);
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

    //Get the currently signed-in user
    public void checkCurrentUser() {
        // [START check_current_user]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            // No user is signed in
            Intent emailActivity = new Intent(this, EmailPasswordActivity.class);
            startActivity(emailActivity);
        }
        // [END check_current_user]
    }

    //Get user's profile
    public User getUserProfile() {
        // [START get_user_profile]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null)
            return null;

        // Name, email address, and profile photo Url
        String name = user.getDisplayName();
        String email = user.getEmail();
        Uri photoUrl = user.getPhotoUrl();

        // Check if user's email is verified
        boolean emailVerified = user.isEmailVerified();

        // The user's ID, unique to the Firebase project. Do NOT use this value to
        // authenticate with your backend server, if you have one. Use
        // FirebaseUser.getIdToken() instead.
        String uid = user.getUid();

        return new User(name,email,photoUrl,emailVerified,uid);
        // [END get_user_profile]
    }

    //Update user's profile
    public void updateProfile(String name, String photoUri) {
        // [START update_profile]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(Uri.parse(photoUri))
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                            Toast.makeText(MainActivity.this, "User profile updated. Please sign out and sign back in to see changes.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        // [END update_profile]
    }

    //Set user's email address
    public void updateEmail(String email) {
        // [START update_email]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.updateEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User email address updated.");
                        }
                    }
                });
        // [END update_email]
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
                        }
                    }
                });
        // [END send_email_verification]
    }

    public void sendEmailVerificationWithContinueUrl() {
        // [START send_email_verification_with_continue_url]
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        String url = "http://www.projectgloriam.com/verify?uid=" + user.getUid();
        ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder()
                .setUrl(url)
                //.setIOSBundleId("com.projectgloriam.fend")
                // The default for this is populated with the current android package name.
                .setAndroidPackageName("com.projectgloriam.fend", false, null)
                .build();

        user.sendEmailVerification(actionCodeSettings)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                        }
                    }
                });

        // [END send_email_verification_with_continue_url]
        // [START localize_verification_email]
        auth.setLanguageCode("fr");
        // To apply the default app language instead of explicitly setting it.
        // auth.useAppLanguage();
        // [END localize_verification_email]
    }

    //Set user's password
    public void updatePassword(String newPassword) {
        // [START update_password]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.updatePassword(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User password updated.");
                        }
                    }
                });
        // [END update_password]
    }

    //Send a password reset email
    public void sendPasswordReset(String emailAddress) {
        // [START send_password_reset]
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                        }
                    }
                });
        // [END send_password_reset]
    }

    //Delete user
    public void deleteUser() {
        // [START delete_user]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User account deleted.");
                        }
                    }
                });
        // [END delete_user]
    }

    //Re-authenticate a user
    public void reauthenticate(String email, String password) {
        // [START reauthenticate]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider
                .getCredential(email, password);

        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "User re-authenticated.");
                    }
                });
        // [END reauthenticate]
    }

    private void signOut(){

        FirebaseAuth.getInstance().signOut();

        Intent emailPasswordActivityIntent = new Intent(this, EmailPasswordActivity.class);
        startActivity(emailPasswordActivityIntent);
    }

    //Access user information
    public void getCurrentUser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();
        }
    }

}