package aston.cs3mdd.carparkingapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * This is a fragment class for the settings page.
 * @author Umar Rajput.
 */
public class SettingsPage extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //about button
        Button aboutButton = getView().findViewById(R.id.settings_about_button);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open about page
                ((MainActivity)getActivity()).transisionToFragment(new SettingsAbout(), "R-L", "About", true, false);
            }
        });

        //delete all data button
        Button deleteAllDataButton = getView().findViewById(R.id.settings_delete_all_data_button);
        deleteAllDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //display alert dialog warning user
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete All Data")
                        .setMessage("Are you sure you want to delete all data?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            //if user confirms dialog
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Delete all data
                                ((MainActivity)getActivity()).deleteAllData();

                                //display success message
                                Toast toast = Toast.makeText(getContext(), "All Data Deleted", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        //language dropdown spinner setup
        Spinner spinner = getView().findViewById(R.id.settings_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.languages, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}