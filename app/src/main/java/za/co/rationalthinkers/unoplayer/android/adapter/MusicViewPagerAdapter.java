package za.co.rationalthinkers.unoplayer.android.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import za.co.rationalthinkers.unoplayer.android.fragment.AlbumsFragment;
import za.co.rationalthinkers.unoplayer.android.fragment.ArtistsFragment;
import za.co.rationalthinkers.unoplayer.android.fragment.SongsFragment;

public class MusicViewPagerAdapter extends FragmentPagerAdapter {

    private static int NUM_PAGES = 3;

    public MusicViewPagerAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int i) {

        switch (i){
            case 0:
                return ArtistsFragment.newInstance();

            case 1:
                return SongsFragment.newInstance();

            case 2:
                return AlbumsFragment.newInstance();

            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        switch (position){
            case 0:
                return "Artists";

            case 1:
                return "Songs";

            case 2:
                return "Albums";

            default:
                return null;
        }

    }

}
