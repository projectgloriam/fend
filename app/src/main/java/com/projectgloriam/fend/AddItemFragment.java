package com.projectgloriam.fend;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.projectgloriam.fend.helpers.UploadHelper;
import com.projectgloriam.fend.models.Card;
import com.projectgloriam.fend.models.CardType;
import com.projectgloriam.fend.models.Document;
import com.projectgloriam.fend.models.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddItemFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int RESULT_OK = -1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RadioButton cardOrDocRadioButton;
    RadioGroup cardOrDocRadioGroup;
    String idCard;
    String doc;

    Spinner cardTypeSpinner;
    final DocumentReference[] cardType = new DocumentReference[1];
    ArrayList<String> spinnerArray = new ArrayList<String>();

    TextView idTitle, docTitle;
    CardView idForm,docForm;

    Button saveButton, scanUploadButton;

    ImageView preview;

    Uri imageUri;

    String imageUrl;

    UploadHelper uploadHelper = new UploadHelper(this);

    // Access a Cloud Firestore instance
    FirebaseFirestore db;

    StorageReference docImageRef;

    User user;

    EditText documentNameText, nameText, numberText, issueDateText, expiryDateText;

    public AddItemFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddItemFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddItemFragment newInstance(String param1, String param2) {
        AddItemFragment fragment = new AddItemFragment();
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
        return inflater.inflate(R.layout.fragment_add_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        idCard = getResources().getString(R.string.id_card); //ID Card
        doc = getResources().getString(R.string.document); //Document

        db = ((MainActivity)getActivity()).firebaseDb;

        idTitle = view.findViewById(R.id.idGroupTextView);
        docTitle = view.findViewById(R.id.docGroupTextView);

        idForm = view.findViewById(R.id.idGroupCardView);
        docForm = view.findViewById(R.id.docGroupCardView);

        preview = view.findViewById(R.id.imagePreviewView);

        //card type
        cardTypeSpinner = view.findViewById(R.id.cardTypeSpinner);

        //Name of person
        nameText = view.findViewById(R.id.editTextTextNameOnDocument);

        //Name of person
        numberText = view.findViewById(R.id.editTextTextIDCard);

        issueDateText = view.findViewById(R.id.issueDateEditTextDate);
        expiryDateText = view.findViewById(R.id.expiryDateEditTextDate);

        documentNameText = view.findViewById(R.id.editTextTextNameOfDocument);

        user = ((MainActivity)getActivity()).getUserProfile();

        cardType[0] = null;

        db.collection("card_types")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                spinnerArray.add(document.getString("name"));
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }

                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                                    (getActivity(), android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML

                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                                    .simple_spinner_dropdown_item);

                            // Apply the adapter to the spinner
                            cardTypeSpinner.setAdapter(spinnerArrayAdapter);

                            cardTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                            {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                                {
                                    // do something upon option selection
                                    cardType[0] = db.collection("card_types").document(parent.getItemAtPosition(position).toString());
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent)
                                {
                                    // can leave this empty
                                }
                            });
                        } else {
                            Toast.makeText(getActivity(), "Card type list is empty", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Error getting card types: ", task.getException());
                        }
                    }
                });

        // ID Card or Document
        cardOrDocRadioGroup = view.findViewById(R.id.idOrDocGroup);
        // get selected radio button from radioGroup
        int cardOrDocRadioGroupSelectedId = cardOrDocRadioGroup.getCheckedRadioButtonId();

        // find the radiobutton by returned id
        cardOrDocRadioButton = view.findViewById(cardOrDocRadioGroupSelectedId);

        // This overrides the radiogroup onCheckListener
        cardOrDocRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                // This will get the radiobutton that has changed in its check state
                // find the radiobutton by returned id
                cardOrDocRadioButton = view.findViewById(checkedId);
                hideIdForm(cardOrDocRadioButton.getText().toString());
            }

        });

        scanUploadButton = view.findViewById(R.id.scanOrUploadButton);
        saveButton = view.findViewById(R.id.saveButton);

        scanUploadButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                uploadHelper.selectImage();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //Check if option is selected
                if(!cardOrDocRadioButton.isChecked()){
                    Toast.makeText(getContext(), "Please select either document or ID", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Checking if photo has been uploaded
                if (!checkPhotoIsSet())
                    return;

                uploadImage()// Register observers to listen for when the download is done or if it fails
                        .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(getContext(), "Image upload failed. Please try again", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageUrl = taskSnapshot.getMetadata().getPath();
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        Toast.makeText(getContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();

                        //If it is an ID card...
                        if (cardOrDocRadioButton.getText().toString().equals(idCard)) {
                            saveIdCard();
                            //If it is an Document...
                        } else if (cardOrDocRadioButton.getText().toString().equals(doc)) {
                            saveDocument();
                        }
                    }
                });
            }
        });

    }

    private void hideIdForm (String text) {
        //If it is an ID card...
        if (text.equals(idCard)) {
            //Make id form and title visible
            idTitle.setVisibility(View.VISIBLE);
            idForm.setVisibility(View.VISIBLE);

            //Make doc form and title invisible (gone from view)
            docTitle.setVisibility(View.GONE);
            docForm.setVisibility(View.GONE);
        } else {
            //Make id form and title invisible (gone from view)
            idTitle.setVisibility(View.GONE);
            idForm.setVisibility(View.GONE);

            //Make doc form and title visible
            docTitle.setVisibility(View.VISIBLE);
            docForm.setVisibility(View.VISIBLE);
        }
    }

    private UploadTask uploadImage() {
        //Folder is either "docs" or "cards"
        docImageRef = ((MainActivity)getActivity()).storageRef.child("images/"+imageUri.getLastPathSegment());
        UploadTask uploadTask = docImageRef.putFile(imageUri);

        return uploadTask;
    }

    private void saveDocument() {
        // Send the positive button event back to the host activity

        if(documentNameText.getText().toString().matches("")){
            Toast.makeText(getActivity(), "Please enter the document name", Toast.LENGTH_SHORT).show();
            return;
        }

        Document doc = new Document(documentNameText.getText().toString(),
                imageUrl,
                user.getUid());
        db.collection("documents").document(documentNameText.getText().toString()).set(doc)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");

                        navigateBackToHome();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error saving document", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    private void saveIdCard() {

        if(numberText.getText().toString().matches("") || nameText.getText().toString().matches("") || issueDateText.getText().toString().matches("") || expiryDateText.getText().toString().matches("")){
            Toast.makeText(getActivity(), "Please don't leave any field blank.", Toast.LENGTH_SHORT).show();
            return;
        }

        //Checking if photo is set
        if (!checkPhotoIsSet())
            return;

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

        Date issueDate = new Date();
        Date expiryDate = new Date();

        try {
            issueDate = df.parse(issueDateText.getText().toString());
            expiryDate = df.parse(expiryDateText.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Creating card object
        Card card = new Card(numberText.getText().toString(),
                                nameText.getText().toString(),
                                cardType[0],
                                issueDate,
                                expiryDate,
                                imageUrl,
                                user.getUid()
                );

        //Saving card
        db.collection("cards").document(card.getNumber()).set(card)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "ID Card successfully saved!");
                        navigateBackToHome();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing ID card", e);
                        Toast.makeText(getActivity(), "Error saving ID card", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean checkPhotoIsSet() {
        if(imageUri==null){
            Toast.makeText(getActivity(), "Please upload photo before saving", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void navigateBackToHome() {
        NavHostFragment.findNavController(this).navigate(R.id.action_addItemFragment_to_homeFragment);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Bitmap result = uploadHelper.takePhoto();
                preview.setImageBitmap(result);
                imageUri = data.getData();
            } else if (requestCode == 2) {
                Bitmap thumbnail = uploadHelper.chosePhoto(data);
                preview.setImageBitmap(thumbnail);
                imageUri = data.getData();
            }
        }
    }

}