package aston.cs3mdd.carparkingapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;

/**
 * This is a fragment class for the edit profile page.
 * @author Umar Rajput
 */
public class ProfileEdit extends Fragment {
    private int id;
    private String name;
    private String carType;
    private String parking;
    private String notes;

    /**
     * Function to create this fragment class.
     * @param id of selected profile item.
     */
    public ProfileEdit(int id) {
        this.id = id;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set variables to profile data
        this.name = ((MainActivity)getActivity()).profileItems.get(id).getName();
        this.carType = ((MainActivity)getActivity()).profileItems.get(id).getCarType();
        this.parking = ((MainActivity)getActivity()).profileItems.get(id).getParking();
        this.notes = ((MainActivity)getActivity()).profileItems.get(id).getNotes();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //set name textbox text to current name
        TextInputLayout nameTextbox = getView().findViewById(R.id.profile_edit_name_textbox_layout);
        nameTextbox.getEditText().setText(name);

        //set cartype textbox text to current cartype
        TextInputLayout carTypeTextbox = getView().findViewById(R.id.profile_edit_car_type_textbox_layout);
        carTypeTextbox.getEditText().setText(carType);

        //done button
        Button doneButton = getView().findViewById(R.id.profile_edit_done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get name and cartype from textboxes
                String newName = nameTextbox.getEditText().getText().toString();
                String newCarType = carTypeTextbox.getEditText().getText().toString();

                //update and save profile data and go back to the main profile page
                ((MainActivity)getActivity()).updateProfile(id, newName, newCarType, parking, notes);
                ((MainActivity)getActivity()).saveProfileData();
                ((MainActivity)getActivity()).transisionToFragment(new ProfileView(id), "L-R", "Profiles", false, true);
            }
        });

        //cancel button
        Button cancelButton = getView().findViewById(R.id.profile_edit_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go back to the main profile page
                ((MainActivity)getActivity()).transisionToFragment(new ProfileView(id), "L-R", "Profiles", false, true);

            }
        });
    }
}