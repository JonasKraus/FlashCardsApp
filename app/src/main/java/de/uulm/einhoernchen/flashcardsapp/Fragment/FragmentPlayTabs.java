package de.uulm.einhoernchen.flashcardsapp.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.uulm.einhoernchen.flashcardsapp.Fragment.Adapter.MyFragmentPagerAdapter;
import de.uulm.einhoernchen.flashcardsapp.Model.Message;
import de.uulm.einhoernchen.flashcardsapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link FragmentPlayTabs#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentPlayTabs extends Fragment {


    private Message challenge = null;

    public FragmentPlayTabs() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentPlayTabs.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentPlayTabs newInstance(String param1, String param2) {
        FragmentPlayTabs fragment = new FragmentPlayTabs();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_play, container, false);

        // Setting ViewPager for each Tabs
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager_play);
        setupViewPager(viewPager);

        TabLayout tabs = (TabLayout) view.findViewById(R.id.tabs_play);
        tabs.setupWithViewPager(viewPager);


        return view;
    }

    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {

        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getChildFragmentManager());

        FragmentPlayFlashCards fragmentPlayFlashCards = new FragmentPlayFlashCards();
        fragmentPlayFlashCards.setMessage(challenge);

        adapter.addFragment(fragmentPlayFlashCards, getResources().getString(R.string.tab_play));
        adapter.addFragment(new FragmentStatistics(), getResources().getString(R.string.toolbar_title_statistic));

        viewPager.setAdapter(adapter);
    }

    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setChallenge(Message challenge) {

        this.challenge = challenge;
    }
}
