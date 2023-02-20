package aston.cs3mdd.carparkingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * This is an adapter class used for the profile page recycler view.
 * @author Umar Rajput.
 */
public class ProfileRecyclerViewAdapter extends RecyclerView.Adapter<ProfileRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ProfileItem> profileItems;
    private final RecyclerViewInterface recyclerViewInterface;

    public ProfileRecyclerViewAdapter(Context context, ArrayList<ProfileItem> profileItems, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.profileItems = profileItems;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public ProfileRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.profile_item, parent, false);
        return new ProfileRecyclerViewAdapter.ViewHolder(view, recyclerViewInterface);
    }

    /**
     * This function is used set text values for each item in the reycler view.
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ProfileRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.tvName.setText(profileItems.get(position).getName());
        holder.tvCarType.setText(profileItems.get(position).getCarType());
        holder.imageView.setImageResource(R.drawable.car_image);
    }

    @Override
    public int getItemCount() {
        return profileItems.size();
    }

    /**
     * This class is used to store data for a recycler view item.
     * @author Umar Rajput
     */
    public static class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;
        private TextView tvName, tvCarType;

        public ViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            imageView = itemView.findViewById(R.id.profile_item_image);
            tvName = itemView.findViewById(R.id.profile_item_name);
            tvCarType = itemView.findViewById(R.id.profile_item_car_type);

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
        }
    }
}
