package com.projectgloriam.fend.adapters;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.projectgloriam.fend.HomeFragmentDirections;
import com.projectgloriam.fend.MainActivity;
import com.projectgloriam.fend.R;
import com.projectgloriam.fend.UserItemsFragmentDirections;
import com.projectgloriam.fend.models.Card;
import com.projectgloriam.fend.models.CardType;
import com.projectgloriam.fend.models.Document;

import java.util.ArrayList;

import android.widget.ImageView;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.ItemViewHolder> {
    private ArrayList<Document> docDataset;
    Context context;
    Drawable iconDrawable;
    private FirebaseFirestore db;
    private Integer navigationUrl;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CardView cardView;
        public TextView titleTextView, typeTextView;
        public ImageView imageView;

        public ItemViewHolder(View v) {
            super(v);

            cardView = v.findViewById(R.id.document_item_card);
            typeTextView = v.findViewById(R.id.document_item_type);
            titleTextView = v.findViewById(R.id.document_item_title);
            imageView = v.findViewById(R.id.card_photo);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public DocumentAdapter(ArrayList<Document> dDataset, Integer nUrl, Context context) {
        this.context = context;
        docDataset = dDataset;
        navigationUrl = nUrl;

        // Access a Cloud Firestore instance
        db = FirebaseFirestore.getInstance();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DocumentAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.document_item, parent, false);

        ItemViewHolder vh = new ItemViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        position = holder.getAdapterPosition();

        //Setting the title
        holder.titleTextView.setText(docDataset.get(position).getName());

        ((MainActivity)context).storageRef.child(docDataset.get(position).getPhoto()).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Use the bytes to display the image
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, bmp.getWidth(), bmp.getHeight(), false));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.e(TAG, "Couldn't fetch image", exception);
            }
        });

        //Setting on click listener
        int finalPosition = position;

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (navigationUrl == R.id.action_homeFragment_to_detailsFragment){
                    HomeFragmentDirections.ActionHomeFragmentToDetailsFragment action = HomeFragmentDirections.actionHomeFragmentToDetailsFragment(docDataset.get(finalPosition).getName(), true);
                    Navigation.findNavController(view).navigate(action);
                } else if (navigationUrl == R.id.action_userItemsFragment_to_detailsFragment){
                    UserItemsFragmentDirections.ActionUserItemsFragmentToDetailsFragment action = UserItemsFragmentDirections.actionUserItemsFragmentToDetailsFragment(docDataset.get(finalPosition).getName(), true);
                    Navigation.findNavController(view).navigate(action);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return docDataset.size();
    }

}