package aston.cs3mdd.carparkingapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * This is a fragment class for the profile view page.
 * @author Umar Rajput.
 */
public class ProfileView extends Fragment {
    private int id;
    private String name;
    private String carType;
    private ProfileItem profileItem;

    /**
     * Function to create this fragment class.
     * @param id of selected profile item.
     */
    public ProfileView(int id) {
        this.id = id;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get and set profile data
        profileItem = ((MainActivity)getActivity()).getProfileItem(id);
        this.name = profileItem.getName();
        this.carType = profileItem.getCarType();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //back button
        FloatingActionButton backButton = getView().findViewById(R.id.profile_view_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go back to main profile page
                ((MainActivity)getActivity()).transisionToFragment(new ProfilePage(), "L-R", "Profiles", false, true);
            }
        });

        //edit button
        Button editButton = getView().findViewById(R.id.profile_view_edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open profile edit page
                ((MainActivity)getActivity()).transisionToFragment(new ProfileEdit(id), "R-L", "Edit Profile", true, false);
            }
        });

        //delete button
        Button deleteButton = getView().findViewById(R.id.profile_view_delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //display alert dialog warning user
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete Profile")
                        .setMessage("Are you sure you want to delete this profile?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            //if user clicks yes option
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Delete profile
                                ((MainActivity)getActivity()).deleteProfile(id);
                                ((MainActivity)getActivity()).saveProfileData();
                                ((MainActivity)getActivity()).transisionToFragment(new ProfilePage(), "L-R", "Profiles", false, true);

                                //display success message
                                Toast toast = Toast.makeText(getContext(), name + " Deleted", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        //displaying profile data on textviews
        TextView profileNameView = getView().findViewById(R.id.profile_view_name);
        TextView carTypeView = getView().findViewById(R.id.profile_view_car_type);
        profileNameView.setText(name);
        carTypeView.setText(carType);
    }
}