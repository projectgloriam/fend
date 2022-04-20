package com.projectgloriam.fend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.projectgloriam.fend.adapters.SearchAdapter;
import com.projectgloriam.fend.models.Card;

import java.util.HashMap;

public class SearchableActivity extends AppCompatActivity {

    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db;
    private SearchView searchView;

    private static final String TAG = "SearchableActivity";
    ProgressBar pb;

    // Source can be CACHE, SERVER, or DEFAULT.
    Source source = Source.CACHE;

    private RecyclerView recyclerView;

    //add an implement method for onclick method as a second argument
    private RecyclerView.Adapter mAdapter;

    // use a linear layout manager
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        db = FirebaseFirestore.getInstance();

        pb = findViewById(R.id.progressBar);

        //set List Adapter

        recyclerView = findViewById(R.id.list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        handleIntent(intent);
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

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
        super.onNewIntent(intent);
    }

    //Getting the cart_page query
    private void handleIntent(Intent intent){
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            // specify an adapter

            // Visible the progress bar
            pb.setVisibility(View.VISIBLE);

            mAdapter = new SearchAdapter(doMySearch(query), getApplicationContext());

            recyclerView.setAdapter(mAdapter);

            //Set progress bar visibility to none
            pb.setVisibility(View.GONE);
        }
    }

    private HashMap<String, Card> doMySearch(String query){
        //TODO: Handle communication between Firebase and this android app

        final HashMap<String, Card> searchDataset = new HashMap<>();

        db.collection("cards")
                //.orderBy("name").startAt(query).endAt(query + "\uf8ff")
                .whereEqualTo("number", query)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                searchDataset.put(document.getId(), document.toObject(Card.class));
                            }

                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "An error occurred. Please try searching again.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                });

        return searchDataset;
    }

}