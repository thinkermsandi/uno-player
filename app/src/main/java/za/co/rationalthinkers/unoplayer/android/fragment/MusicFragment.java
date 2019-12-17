package za.co.rationalthinkers.unoplayer.android.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import za.co.rationalthinkers.unoplayer.android.R;
import za.co.rationalthinkers.unoplayer.android.adapter.MusicViewPagerAdapter;

public class MusicFragment extends Fragment {

    private OnMusicActionListener mListener;

    private Context mContext;
    private MusicViewPagerAdapter viewPagerAdapter;

    //UI References
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public interface OnMusicActionListener {
        void onMusicExit();
    }

    public MusicFragment() {
        // Required empty public constructor
    }

    public static MusicFragment newInstance() {
        return new MusicFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_music, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI(view);
        setUpListeners();

        setUpViewPager();
    }

    private void initUI(View view){
        tabLayout = view.findViewById(R.id.view_pager_tabs);
        viewPager = view.findViewById(R.id.view_pager);
    }

    private void setUpListeners(){

    }

    private void setUpViewPager(){
        viewPagerAdapter = new MusicViewPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(1);
        viewPager.setOffscreenPageLimit(3);

        tabLayout.setupWithViewPager(viewPager); // Give the TabLayout the ViewPager
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnMusicActionListener) {
            mListener = (OnMusicActionListener) context;
        }
        else {
            throw new RuntimeException(context.toString() + " must implement OnMusicActionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
