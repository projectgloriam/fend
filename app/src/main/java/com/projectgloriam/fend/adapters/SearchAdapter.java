package com.projectgloriam.fend.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.projectgloriam.fend.R;
import com.projectgloriam.fend.models.Card;
import com.projectgloriam.fend.models.CardType;

import java.util.HashMap;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.CardViewHolder> {
    private HashMap<String, Card> mDataset;
    public Context mContext;
    private FirebaseFirestore db;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class CardViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CardView cardView;
        public ImageView card_photo;
        public TextView full_name, card_type, card_number;

        public CardViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cv);
            card_photo = itemView.findViewById(R.id.card_photo);
            full_name = itemView.findViewById(R.id.full_name);

            card_type = itemView.findViewById(R.id.card_type);
            card_number = itemView.findViewById(R.id.card_number);
        }

    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public SearchAdapter(HashMap<String, Card> myDataset, Context context) {

        mDataset = myDataset;
        mContext = context;

        // Access a Cloud Firestore instance
        db = FirebaseFirestore.getInstance();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup,
                                                               int viewType) {
        // create a new view
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        CardViewHolder cvh = new CardViewHolder(v);
        return cvh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        // - get elements from your dataset at this position
        // - replace the contents of the views with those elements
        position = holder.getAdapterPosition();
        holder.card_number.setText(mDataset.get(position).getNumber());
        holder.full_name.setText(mDataset.get(position).getFullName());
        db.collection("card_types")
                .document(mDataset.get(position).getType().getId()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                CardType cardType = documentSnapshot.toObject(CardType.class);
                holder.card_type.setText(cardType.getName());
            }
        });


        Glide.with(holder.card_photo.getContext())
                .load(mDataset.get(position).getPhoto())
                .placeholder(R.drawable.ic_baseline_card_24)
                .fitCenter()
                .into(holder.card_photo);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    //override the onAttachedToRecyclerView method
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}