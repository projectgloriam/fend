package com.projectgloriam.fend;

import static android.content.ContentValues.TAG;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.projectgloriam.fend.models.User;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //RecyclerView Adapter
    private RecyclerView menuRecyclerView;
    private RecyclerView idsRecyclerView;
    private RecyclerView docsRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.Adapter cAdapter;
    private RecyclerView.Adapter dAdapter;
    private RecyclerView.LayoutManager mLayoutManager, cLayoutManager, dLayoutManager;
    private TextView welcome;
    // Access a Cloud Firestore instance
    FirebaseFirestore db;
    ProgressBar userPb;
    boolean areCardsReady, areDocsReady = false;

    private User user;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userPb = view.findViewById(R.id.userIdsProgressBar);

        user = ((MainActivity)getActivity()).getUserProfile();
        //Setting welcome text
        Resources res = getResources();
        String welcomeName = user.getName() == null ? "" : user.getName();
        String welcome_text = String.format(res.getString(R.string.welcome_user), welcomeName);
        welcome = view.findViewById(R.id.welcomeTextView);
        welcome.setText(welcome_text);

        db = FirebaseFirestore.getInstance();

        menuRecyclerView = view.findViewById(R.id.menu_recycler);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        menuRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        menuRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter
        ArrayList<Menu> menuDataset = new ArrayList<>();

        menuDataset.add(new Menu(R.string.add_items, R.string.add_items_desc, R.id.action_homeFragment_to_addItemFragment));
        menuDataset.add(new Menu(R.string.view_items, R.string.view_items_desc, R.id.action_homeFragment_to_userItemsFragment));

        mAdapter = new MenuAdapter(menuDataset, getContext());
        menuRecyclerView.setAdapter(mAdapter);

        //User id cards adapter
        idsRecyclerView = view.findViewById(R.id.user_ids_recycler);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        idsRecyclerView.setHasFixedSize(true);

        cLayoutManager = new LinearLayoutManager(getActivity());
        idsRecyclerView.setLayoutManager(cLayoutManager);

        ArrayList<Card> cDataset = new ArrayList<>();

        userPb.setVisibility(View.VISIBLE);
        //Getting the ID cards
        db.collection("cards")
                .whereEqualTo("uid", user.getUid())
                .limit(3).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                cDataset.add(document.toObject(Card.class));
                            }

                            // specify an adapter
                            cAdapter = new CardAdapter(cDataset, R.id.action_homeFragment_to_detailsFragment, getContext());
                            idsRecyclerView.setAdapter(cAdapter);
                        } else {
                            Toast.makeText(getActivity(), "Your ID card list is empty", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Error getting ID Cards: ", task.getException());
                        }

                        areCardsReady = true;

                        if(areCardsReady && areDocsReady)
                            userPb.setVisibility(View.GONE);

                    }
                });

        //User documents adapter
        docsRecyclerView = view.findViewById(R.id.user_docs_recycler);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        docsRecyclerView.setHasFixedSize(true);

        dLayoutManager = new LinearLayoutManager(getActivity());
        docsRecyclerView.setLayoutManager(dLayoutManager);

        // specify an adapter
        ArrayList<Document> dDataset = new ArrayList<>();

        //Getting the Documents
        db.collection("documents")
                .whereEqualTo("uid", user.getUid())
                .limit(3).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                dDataset.add(document.toObject(Document.class));
                            }

                            dAdapter = new DocumentAdapter(dDataset, R.id.action_homeFragment_to_detailsFragment, getActivity());
                            docsRecyclerView.setAdapter(dAdapter);
                        } else {
                            Toast.makeText(getActivity(), "Your documents list is empty", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                        areDocsReady = true;

                        if(areCardsReady && areDocsReady)
                            userPb.setVisibility(View.GONE);

                    }
                });
    }
}