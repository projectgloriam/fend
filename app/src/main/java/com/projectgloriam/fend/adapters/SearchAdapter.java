package com.projectgloriam.fend.adapters;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.projectgloriam.fend.HomeFragmentDirections;
import com.projectgloriam.fend.MainActivity;
import com.projectgloriam.fend.R;
import com.projectgloriam.fend.UserItemsFragmentDirections;
import com.projectgloriam.fend.models.Card;
import com.projectgloriam.fend.models.CardType;
import com.projectgloriam.fend.models.Document;

import java.util.ArrayList;

import android.widget.ImageView;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ItemViewHolder> {
    private ArrayList<Card> cardDataset;
    Context context;
    Drawable iconDrawable;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    public StorageReference storageRef;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CardView cardView;
        public TextView idTextView, typeTextView, nameTextView;
        public ImageView imageView;

        public ItemViewHolder(View v) {
            super(v);

            cardView = v.findViewById(R.id.cv);
            typeTextView = v.findViewById(R.id.card_type);
            idTextView = v.findViewById(R.id.card_number);
            nameTextView = v.findViewById(R.id.full_name);
            imageView = v.findViewById(R.id.card_photo);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public SearchAdapter(ArrayList<Card> cDataset, Context context) {
        this.context = context;
        cardDataset = cDataset;

        // Access a Cloud Firestore instance
        db = FirebaseFirestore.getInstance();

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SearchAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_item, parent, false);

        ItemViewHolder vh = new ItemViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        position = holder.getAdapterPosition();
        //Setting the name
        holder.nameTextView.setText(cardDataset.get(position).getFullName());
        //Setting the id
        holder.idTextView.setText(cardDataset.get(position).getNumber());

        //Setting the type
        if(cardDataset.get(position).getType() != null)
            db.collection("card_types")
                    .document(cardDataset.get(position).getType().getId()).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            CardType cardType = documentSnapshot.toObject(CardType.class);
                            holder.typeTextView.setText(cardType.getName());
                        }
                    });

        storageRef.child(cardDataset.get(position).getPhoto()).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
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

    }

    @Override
    public int getItemCount() {
        return cardDataset.size();
    }

}