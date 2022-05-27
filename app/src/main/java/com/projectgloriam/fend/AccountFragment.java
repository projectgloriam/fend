package com.projectgloriam.fend;

import static android.content.ContentValues.TAG;

import static com.projectgloriam.fend.AddItemFragment.REQUEST_IMAGE_CAPTURE;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.projectgloriam.fend.helpers.UploadHelper;
import com.projectgloriam.fend.models.Card;
import com.projectgloriam.fend.models.User;
import com.projectgloriam.fend.models.UserPreferences;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageView profilePicture;
    private EditText name, email, password, emergencyEmail, emergencyTelephone;
    private Switch expiry, notify;
    private User user;
    private UserPreferences userPreferences;
    Button save, upload;

    // Access a Cloud Firestore instance
    FirebaseFirestore db;

    UploadHelper uploadHelper = new UploadHelper(this);

    private static final int RESULT_OK = -1;

    String profileUrl = "";

    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        user = ((MainActivity)getActivity()).getUserProfile();

        profilePicture = view.findViewById(R.id.profilePhotoImageView);

        profilePicture.setImageURI(user.getPhotoUrl());

        name = view.findViewById(R.id.editTextTextName);
        name.setText((String) user.getName());

        email = view.findViewById(R.id.editTextTextEmailAddress);
        email.setText((String) user.getEmail());

        password = view.findViewById(R.id.editTextTextPassword);

        upload = view.findViewById(R.id.photoButton);

        expiry = view.findViewById(R.id.expiryReminderSwitch);

        emergencyEmail = view.findViewById(R.id.editTextTextEmergencyEmailAddress);

        emergencyTelephone = view.findViewById(R.id.editTextTextEmergencyTelephone);

        notify = view.findViewById(R.id.notifiedSwitch);

        save = view.findViewById(R.id.saveButton);

        //Fetching user preferences
        db.collection("user_preferences").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        userPreferences = document.toObject(UserPreferences.class);

                        //set user's preferences from firestore db
                        setUserPreferences();
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        //Set user preferences
                        userPreferences = new UserPreferences(user.getUid(),false,"","",false);
                        Log.d(TAG, "User has no such preferences");
                    }
                } else {
                    Toast.makeText(getContext(), "Error fetching all your preferences", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        //Handle user photo upload
        handleUploadPhoto();

        //Handle save click
        handleSavePreferencesClick();

    }

    private void handleUploadPhoto() {
        upload.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                uploadHelper.selectImage();
            }
        });
    }

    private void setUserPreferences() {
        expiry.setChecked(userPreferences.isExpiryReminder());
        emergencyEmail.setText(userPreferences.getEmergencyEmail());
        emergencyTelephone.setText(userPreferences.getEmergencyTelephone());
        notify.setChecked(userPreferences.isNotify());
    }

    private void handleSavePreferencesClick() {
        //Setting onClickListeners
        save.setOnClickListener(view -> {

            //Check empty fields
            if (name.getText().toString().matches("") || email.getText().toString().matches("") ) {
                Toast.makeText(getContext(), "Please don't leave any field blank", Toast.LENGTH_SHORT).show();
                return;
            }

            //Save user name photo
            ((MainActivity)getActivity()).updateProfile(name.getText().toString(), profileUrl);

            //Save email
            ((MainActivity)getActivity()).updateEmail(email.getText().toString());

            //Save password
            if (!password.getText().toString().matches(""))
                ((MainActivity)getActivity()).updatePassword(password.getText().toString());

            userPreferences.update(expiry.isChecked(), emergencyEmail.getText().toString(), emergencyTelephone.getText().toString(), notify.isChecked());

            db.collection("user_preferences").document(userPreferences.getUid())
                    .set(userPreferences)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            //Navigate back to home
                            navigateBackToHome();
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Error saving your preferences", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "Error writing document", e);
                        }
                    });

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                Bitmap thumbnail = uploadHelper.chosePhoto((Uri) extras.get(MediaStore.EXTRA_OUTPUT));
                profilePicture.setImageBitmap(thumbnail);
                profileUrl = extras.get(MediaStore.EXTRA_OUTPUT).toString();

            } else if (requestCode == 2) {
                Bitmap thumbnail = uploadHelper.chosePhoto(data.getData());
                profilePicture.setImageBitmap(thumbnail);
                profileUrl = data.getData().toString();
            }
        }
    }

    private void navigateBackToHome() {
        NavHostFragment.findNavController(this).navigate(R.id.action_accountFragment_to_homeFragment);
    }

}