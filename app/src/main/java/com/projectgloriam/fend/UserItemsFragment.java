package com.projectgloriam.fend;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.projectgloriam.fend.adapters.CardAdapter;
import com.projectgloriam.fend.adapters.DocumentAdapter;
import com.projectgloriam.fend.adapters.MenuAdapter;
import com.projectgloriam.fend.models.Card;
import com.projectgloriam.fend.models.Document;
import com.projectgloriam.fend.models.Menu;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserItemsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserItemsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //RecyclerView Adapter
    private RecyclerView idsRecyclerView;
    private RecyclerView docsRecyclerView;
    private RecyclerView.Adapter cAdapter;
    private RecyclerView.Adapter dAdapter;
    private RecyclerView.LayoutManager layoutManager;

    // Access a Cloud Firestore instance
    FirebaseFirestore db;

    public UserItemsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserItemsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserItemsFragment newInstance(String param1, String param2) {
        UserItemsFragment fragment = new UserItemsFragment();
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
        return inflater.inflate(R.layout.fragment_user_items, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getActivity());

        //User id cards adapter
        idsRecyclerView = view.findViewById(R.id.user_ids_recycler);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        idsRecyclerView.setHasFixedSize(true);

        docsRecyclerView.setLayoutManager(layoutManager);

        ArrayList<Card> cDataset = new ArrayList<>();

        //Getting the ID cards
        db.collection("cards").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                cDataset.add(document.toObject(Card.class));
                            }
                        } else {
                            Toast.makeText(getActivity(), "Error getting documents", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Error getting ID Cards: ", task.getException());
                        }
                    }
                });


        // specify an adapter

        cAdapter = new CardAdapter(cDataset, R.id.action_userItemsFragment_to_detailsFragment, getContext());
        idsRecyclerView.setAdapter(cAdapter);

        //User documents adapter
        docsRecyclerView = view.findViewById(R.id.user_docs_recycler);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        docsRecyclerView.setHasFixedSize(true);

        docsRecyclerView.setLayoutManager(layoutManager);

        // specify an adapter
        ArrayList<Document> dDataset = new ArrayList<>();

        //Getting the Documents
        db.collection("documents").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                dDataset.add(document.toObject(Document.class));
                            }
                        } else {
                            Toast.makeText(getActivity(), "Error getting documents", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        dAdapter = new DocumentAdapter(dDataset, R.id.action_userItemsFragment_to_detailsFragment, getContext());
        docsRecyclerView.setAdapter(dAdapter);
    }
}