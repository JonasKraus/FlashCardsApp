package de.uulm.einhoernchen.flashcardsapp.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import de.uulm.einhoernchen.flashcardsapp.Activity.MainActivity;
import de.uulm.einhoernchen.flashcardsapp.Const.Constants;
import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private DbManager db = Globals.getDb();
    private Switch switchSnyc;
    private RadioGroup radioGroupLearnMode;
    private Switch switchAnswerMultiChoiceRandom;
    private Switch switchNightMode;
    private Switch switchShowLastDrawer;


    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Globals.setVisibilityToolbarMain(View.GONE);
        Globals.setVisibilityFab(View.GONE);
        Globals.setVisibilityFabAdd(View.GONE);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_settings);
        ((MainActivity) Globals.getContext()).setSupportActionBar(toolbar);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) view.findViewById(R.id.toolbar_layout_settings);
        collapsingToolbar.setTitle(getResources().getString(R.string.toolbar_title_settings));

        findViewElements(view);

        setViewElementsListener(this);

        return view;
    }


    /**
     * Sets the listener to the gui elements
     * To unset listener give null as param
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-14
     *@param set
     */
    private void setViewElementsListener(SettingsFragment set) {

        switchSnyc.setOnCheckedChangeListener(set);
        switchAnswerMultiChoiceRandom.setOnCheckedChangeListener(set);
        switchShowLastDrawer.setOnCheckedChangeListener(set);
        switchNightMode.setOnCheckedChangeListener(set);

        radioGroupLearnMode.setOnCheckedChangeListener(set);

    }


    /**
     * Finds gui elements and sets them to vars
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-14
     *
     * @param view
     */
    private void findViewElements(View view) {

        switchSnyc = (Switch) view.findViewById(R.id.switch_settings_sync);
        switchAnswerMultiChoiceRandom = (Switch) view.findViewById(R.id.switch_settings_answer_multi_choice_random);
        switchShowLastDrawer = (Switch) view.findViewById(R.id.switch_settings_night_mode);
        switchNightMode = (Switch) view.findViewById(R.id.switch_settings_night_mode);

        radioGroupLearnMode = (RadioGroup) view.findViewById(R.id.radio_group_learn_mode);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Globals.setVisibilityToolbarMain(View.VISIBLE);
        Globals.setVisibilityFab(View.VISIBLE);

        // Unset listener
        setViewElementsListener(null);
    }


    /**
     * Listens on the change event of compound buttons
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-14
     *
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()) {

            case R.id.switch_settings_sync:
                // TODO

                break;
            case R.id.switch_settings_answer_multi_choice_random:
                // TODO

                break;
            case R.id.switch_setting_show_last_drawer:
                // TODO

                break;
            case R.id.switch_settings_night_mode:
                // TODO

                break;
        }

    }


    /**
     * Listens on the check changed event of radioGroups
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-14
     *
     * @param group
     * @param checkedId
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        switch (group.getId()) {

            case R.id.radio_group_learn_mode:
                //TODO

                Constants mode = null;

                switch (radioGroupLearnMode.getCheckedRadioButtonId()) {

                    case R.id.radio_learn_mode_drawer:
                        //TODO

                        mode = Constants.SETTINGS_LEARN_MODE_DRAWER;
                        break;

                    case R.id.radio_learn_mode_knowledge:
                        //TODO
                        mode = Constants.SETTINGS_LEARN_MODE_KNOWLEDGE;
                        break;

                    case R.id.radio_learn_mode_date:
                        //TODO
                        mode = Constants.SETTINGS_LEARN_MODE_DATE;
                        break;

                    case R.id.radio_learn_mode_random:
                        //TODO
                        mode = Constants.SETTINGS_LEARN_MODE_RANDOM;
                        break;
                }


                Log.d("radio", radioGroupLearnMode.getCheckedRadioButtonId() + "");
                Log.d("radio mode", mode + "");

                break;
        }

    }
}
