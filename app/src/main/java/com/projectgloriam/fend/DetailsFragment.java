package com.projectgloriam.fend;

import static android.content.ContentValues.TAG;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.projectgloriam.fend.models.Card;
import com.projectgloriam.fend.models.CardType;
import com.projectgloriam.fend.models.Document;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Access a Cloud Firestore instance
    FirebaseFirestore db;

    Card card;
    Document document;
    String cardDocID;
    Boolean isDocument;
    ImageView documentImage;
    TextView idText, idLabelText, nameText, nameLabelText, typeLabelText, typeText, issueDateText, issueDateLabelText, expiryDateText, expiryDateLabelText;

    public DetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailsFragment newInstance(String param1, String param2) {
        DetailsFragment fragment = new DetailsFragment();
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
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        cardDocID = DetailsFragmentArgs.fromBundle(getArguments()).getItemId();
        isDocument = DetailsFragmentArgs.fromBundle(getArguments()).getIsDocument();

        documentImage = view.findViewById(R.id.cardDetailsImageView);
        idText = view.findViewById(R.id.idTextView);
        idLabelText = view.findViewById(R.id.idLabelTextView);
        nameText = view.findViewById(R.id.fullNameTextView);
        nameLabelText = view.findViewById(R.id.fullNameLabelTextView);
        typeText = view.findViewById(R.id.typeTextView);
        typeLabelText = view.findViewById(R.id.typeLabelTextView);
        issueDateText = view.findViewById(R.id.issueDateTextView);
        issueDateLabelText = view.findViewById(R.id.issueDateLabelTextView);
        expiryDateText = view.findViewById(R.id.expiryDateTextView);
        expiryDateLabelText = view.findViewById(R.id.expiryDateLabelTextView);

        DateFormat dateFormat = new SimpleDateFormat("E, dd MMMM yyyy");

        if(!isDocument){
            //Getting the card details
            db.collection("cards")
                    .document(cardDocID).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Card card = documentSnapshot.toObject(Card.class);

                            //Setting ID
                            idText.setText(card.getNumber());

                            //Setting name
                            nameText.setText(card.getFullName());

                            //Setting issue date
                            issueDateText.setText(dateFormat.format(card.getIssueDate()));

                            //Setting expiry date
                            expiryDateText.setText(dateFormat.format(card.getExpiryDate()));

                            //Setting the Type
                            if(card.getType() != null)
                                db.collection("card_types")
                                    .document(card.getType().getId()).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            CardType cardType = documentSnapshot.toObject(CardType.class);
                                            typeText.setText(cardType.getName());
                                        }
                                    });

                            //Setting image
                            ((MainActivity)getActivity()).storageRef.child(card.getPhoto()).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    // Use the bytes to display the image
                                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    documentImage.setImageBitmap(Bitmap.createScaledBitmap(bmp, bmp.getWidth(), bmp.getHeight(), false));
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                    Log.e(TAG, "Couldn't fetch image", exception);
                                }
                            });
                        }
                    });
        } else {
            //Getting the card details
            db.collection("documents")
                    .document(cardDocID).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Document doc = documentSnapshot.toObject(Document.class);

                            //Setting name
                            typeText.setText(doc.getName());

                            //name_of_document
                            idLabelText.setVisibility(View.GONE);
                            nameLabelText.setVisibility(View.GONE);
                            typeLabelText.setText(getResources().getText(R.string.name_of_document));
                            issueDateLabelText.setVisibility(View.GONE);
                            expiryDateLabelText.setVisibility(View.GONE);

                            //Setting image
                            ((MainActivity)getActivity()).storageRef.child(doc.getPhoto()).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    // Use the bytes to display the image
                                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    documentImage.setImageBitmap(Bitmap.createScaledBitmap(bmp, bmp.getWidth(), bmp.getHeight(), false));
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                    Log.e(TAG, "Couldn't fetch image", exception);
                                }
                            });
                        }
                    });
        }

    }
}