package com.projectgloriam.fend.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.projectgloriam.fend.R;
import com.projectgloriam.fend.models.Menu;

import java.util.ArrayList;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {
    private ArrayList<Menu> mDataset;
    Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MenuViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CardView cardView;
        public TextView titleTextView, descTextView;

        public MenuViewHolder(View v) {
            super(v);

            cardView = v.findViewById(R.id.menu_item_card);
            descTextView = v.findViewById(R.id.menu_item_description);
            titleTextView = v.findViewById(R.id.menu_item_title);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MenuAdapter(ArrayList<Menu> myDataset, Context context) {
        this.context = context;
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MenuAdapter.MenuViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.menu_item, parent, false);

        MenuViewHolder vh = new MenuViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        position = holder.getAdapterPosition();

        //Setting the title
        holder.titleTextView.setText(mDataset.get(position).getTitle());

        //Setting the description
        holder.descTextView.setText(mDataset.get(position).getDescription());

        //Setting on click listener
        int finalPosition = position;

        if (mDataset.get(finalPosition).getAction() != null)
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Navigation.findNavController(view).navigate(mDataset.get(finalPosition).getAction());
                }
            });

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}