package aston.cs3mdd.carparkingapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * This is a fragment class for the profile page.
 * @author Umar Rajput.
 */
public class ProfilePage extends Fragment implements RecyclerViewInterface {

    /**
     * This stores a list of profiles.
     */
    public ArrayList<ProfileItem> profileItems;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //update profile items list
        ((MainActivity)getActivity()).updateProfileItemsList();
        this.profileItems = ((MainActivity)getActivity()).getProfileItems();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //get recycler view
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.profileRecyclerView);

//        test data
//        profileItems.add(new ProfileItem("Umar",        "Ford KA",              R.drawable.ic_launcher_background));
//        profileItems.add(new ProfileItem("Jerry ",      "Audi A7",              R.drawable.ic_launcher_background));
//        profileItems.add(new ProfileItem("Rory ",       "Honda Civic",          R.drawable.ic_launcher_background));
//        profileItems.add(new ProfileItem("Leena ",      "Audi RS3",             R.drawable.ic_launcher_background));

        //setup recycler view
        ProfileRecyclerViewAdapter adapter = new ProfileRecyclerViewAdapter(getContext(), profileItems, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //create profile button
        FloatingActionButton createProfileButton = getView().findViewById(R.id.profile_create_Button);
        createProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //change current fragment to create profile page
                ((MainActivity)getActivity()).transisionToFragment(new ProfileCreate(), "R-L", "Create Profile", true, false);
            }
        });

    }

    /**
     * Function for when a profile item is clicked in the recycler view.
     * @param position index of item in reycler view.
     */
    @Override
    public void onItemClick(int position) {
//        ((MainActivity)getActivity()).transisionToFragment(new ProfileView(profileItems.get(position).getName(), profileItems.get(position).getCarType()), "R-L", profileItems.get(position).getName() + "'s Profile", true, false);

        //get profile item and change current fragment to profile view page
        ProfileItem profileItem = ((MainActivity)getActivity()).getProfileItem(position);
        ((MainActivity)getActivity()).transisionToFragment(new ProfileView(position), "R-L", profileItem.getName() + "'s Profile", true, false);
    }
}











