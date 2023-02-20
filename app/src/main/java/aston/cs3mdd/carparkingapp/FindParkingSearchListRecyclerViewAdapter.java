package aston.cs3mdd.carparkingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * This is an adapter class used for the find parking recycler view.
 * @author Umar Rajput.
 */
public class FindParkingSearchListRecyclerViewAdapter extends RecyclerView.Adapter<FindParkingSearchListRecyclerViewAdapter.ViewHolder>{

    private Context context;
    private ArrayList<SearchItem> searchItems;
    private final SearchParkingRecyclerViewInterface recyclerViewInterface;

    public FindParkingSearchListRecyclerViewAdapter(Context context, ArrayList<SearchItem> searchItems, SearchParkingRecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.searchItems = searchItems;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public FindParkingSearchListRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_find_parking_search_item, parent, false);
        return new FindParkingSearchListRecyclerViewAdapter.ViewHolder(view, recyclerViewInterface);
    }

    /**
     * This function is used set text values for each item in the reycler view.
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull FindParkingSearchListRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.name.setText(searchItems.get(position).getName());
        holder.street.setText(searchItems.get(position).getAddress());
        holder.distance.setText(searchItems.get(position).getDistance());
        holder.favouriteButton.setChecked(searchItems.get(position).isCheck());
    }

    @Override
    public int getItemCount() {
        return searchItems.size();
    }

    /**
     * This class is used to store data for a recycler view item.
     * @author Umar Rajput
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name, street, distance;
        private ToggleButton favouriteButton;

        public ViewHolder(@NonNull View itemView, SearchParkingRecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            name = itemView.findViewById(R.id.find_parking_search_item_name);
            street = itemView.findViewById(R.id.find_parking_search_item_street);
            distance = itemView.findViewById(R.id.find_parking_search_item_distance);
            favouriteButton = itemView.findViewById(R.id.find_parking_search_item_favourite_button);

            //item onClick listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recyclerViewInterface != null) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(position);
                        }
                    }
                }
            });

            //favourite button onClick listener
            favouriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recyclerViewInterface != null) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onFavouriteClick(position);
                        }
                    }
                }
            });
        }
    }
}
