package aston.cs3mdd.carparkingapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * This is an adapter class for the tab layout in the find parking page.
 */
public class FindParkingTabLayoutAdapter extends FragmentStateAdapter {

    public FindParkingTabLayoutAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    /**
     * This function changes displayed fragment to the given value.
     * @param position the tab number for the page that is going to be displayed.
     * @return
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: //find parking tab
                return new FindParkingSearchList();
            case 1: //favourites tab
                return new FindParkingFavouriteList();
            default: //find parking tab
                return new FindParkingSearchList();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
