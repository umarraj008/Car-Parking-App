package aston.cs3mdd.carparkingapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * This is a fragment class for the create profile page.
 * @author Umar Rajput
 */
public class ProfileCreate extends Fragment {
    private String name = "";
    private String carType = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_create, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //done button
        Button doneButton = getView().findViewById(R.id.profile_create_done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get name from textbox
                TextInputEditText nameTextbox = getView().findViewById(R.id.profile_create_name_textbox);
                name = nameTextbox.getText().toString();

                //get cartype from textbox
                TextInputEditText carTypeTextbox = getView().findViewById(R.id.profile_create_car_type_textbox);
                carType = carTypeTextbox.getText().toString();

//                Log.i("asd", "name: " + name + ", carType: " + carType);

                //validation to check if text has been entered
                if (name.length() <= 0 || carType.length() <= 0) {
                    //display warning toast
                    Toast toast = Toast.makeText(getContext(), "All fields must be filled", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    //save profile data and go back to the main profile page
                    ((MainActivity)getActivity()).createProfile(name, carType, 0);
                    ((MainActivity)getActivity()).saveProfileData();
                    ((MainActivity)getActivity()).transisionToFragment(new ProfilePage(), "L-R", "Profiles", false, true);
                }
            }
        });

        //cancel button
        Button cancelButton = getView().findViewById(R.id.profile_create_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go back to the main profile page
                ((MainActivity)getActivity()).transisionToFragment(new ProfilePage(), "L-R", "Profiles", false, true);
            }
        });
    }
}