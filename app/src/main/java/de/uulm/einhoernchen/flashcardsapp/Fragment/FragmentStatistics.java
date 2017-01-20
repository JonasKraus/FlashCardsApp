package de.uulm.einhoernchen.flashcardsapp.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import de.uulm.einhoernchen.flashcardsapp.Activity.MainActivity;
import de.uulm.einhoernchen.flashcardsapp.Const.Constants;
import de.uulm.einhoernchen.flashcardsapp.Model.Settings;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentStatistics#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentStatistics extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public FragmentStatistics() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentSettings.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentStatistics newInstance(String param1, String param2) {
        FragmentStatistics fragment = new FragmentStatistics();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


        Globals.setVisibilityToolbarMain(View.GONE);
        Globals.setVisibilityFab(View.VISIBLE);
        Globals.setVisibilityFabAdd(View.GONE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_statistics);
        ((MainActivity) Globals.getContext()).setSupportActionBar(toolbar);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) view.findViewById(R.id.toolbar_layout_statistics);
        collapsingToolbar.setTitle(getResources().getString(R.string.toolbar_title_statistic));

        findViewElements(view);

        setElementValues();

        return view;
    }


    /**
     * Sets the values of the users settings to the gui elements
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-20
     */
    private void setElementValues() {

        // TODO switchSnyc.setChecked(settings.isAllowSync());
    }


    /**
     * Sets the listener to the gui elements
     * To unset listener give null as param
     *
     * @param set
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-20
     */
    private void setViewElementsListener(FragmentStatistics set) {

        // TODO

    }


    /**
     * Finds gui elements and sets them to vars
     *
     * @param view
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-14
     */
    private void findViewElements(View view) {

        // TODO switchSnyc = (Switch) view.findViewById(R.id.switch_settings_sync);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Unset listener
        setViewElementsListener(null);
    }
}