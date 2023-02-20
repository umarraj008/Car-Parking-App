package aston.cs3mdd.carparkingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * This is an adapter class used for the favourite parking recycler view.
 * @author Umar Rajput.
 */
public class FindParkingFavouriteListRecyclerViewAdapter extends RecyclerView.Adapter<FindParkingFavouriteListRecyclerViewAdapter.ViewHolder>{
    private Context context;
    private ArrayList<FavouriteItem> favouriteItems;
    private final SearchParkingRecyclerViewInterface recyclerViewInterface;

    public FindParkingFavouriteListRecyclerViewAdapter(Context context, ArrayList<FavouriteItem> favouriteItems, SearchParkingRecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.favouriteItems = favouriteItems;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public FindParkingFavouriteListRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_find_parking_favourite_item, parent, false);
        return new FindParkingFavouriteListRecyclerViewAdapter.ViewHolder(view, recyclerViewInterface);
    }

    /**
     * This function is used set text values for each item in the reycler view.
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull FindParkingFavouriteListRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.name.setText(favouriteItems.get(position).getName());
        holder.street.setText(favouriteItems.get(position).getAddress());
    }

    @Override
    public int getItemCount() {
        return favouriteItems.size();
    }

    /**
     * This class is used to store data for a recycler view item.
     * @author Umar Rajput
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name, street;
        private Button trashButton;

        public ViewHolder(@NonNull View itemView, SearchParkingRecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            name = itemView.findViewById(R.id.find_parking_favourite_item_name);
            street = itemView.findViewById(R.id.find_parking_favourite_item_street);
            trashButton = itemView.findViewById(R.id.find_parking_favourite_item_trash_button);

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

            //trash button onClick listener
            trashButton.setOnClickListener(new View.OnClickListener() {
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
