package de.uulm.einhoernchen.flashcardsapp.Activity;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import de.uulm.einhoernchen.flashcardsapp.Const.Constants;
import de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentSettings;
import de.uulm.einhoernchen.flashcardsapp.Model.Settings;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;

import static de.uulm.einhoernchen.flashcardsapp.Fragment.FragmentSettings.equalsId;

public class SettingsActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener  {

    //private DbManager db = Globals.getDb();
    private Switch switchSnyc;
    private RadioGroup radioGroupLearnMode;
    private Switch switchAnswerMultiChoiceRandom;
    private Switch switchNightMode;
    private Switch switchShowLastDrawer;

    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settings);

        settings = Settings.getSettings();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
        ((MainActivity) Globals.getContext()).setSupportActionBar(toolbar);

        setSupportActionBar(toolbar);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout_settings);
        collapsingToolbar.setTitle(getResources().getString(R.string.toolbar_title_settings));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewElements();

        setElementValues();

        setViewElementsListener(this);
    }

    /**
     * Sets the values of the users settings to the gui elements
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-15
     *
     */
    private void setElementValues() {

        switchSnyc.setChecked(settings.isAllowSync());
        switchAnswerMultiChoiceRandom.setChecked(settings.isMultiplyChoiceAnswerOrderRandom());
        switchShowLastDrawer.setChecked(settings.isHideLastDrawer());
        switchNightMode.setChecked(settings.isNightMode());

        radioGroupLearnMode.check(equalsId(settings.getLearnMode()));
    }


    /**
     * Sets the listener to the gui elements
     * To unset listener give null as param
     *
     * @param set
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-20
     */
    private void setViewElementsListener(SettingsActivity set) {

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
     * @since 2017-01-20
     *
     */
    private void findViewElements() {

        switchSnyc = (Switch) findViewById(R.id.switch_settings_sync);
        switchAnswerMultiChoiceRandom = (Switch) findViewById(R.id.switch_settings_answer_multi_choice_random);
        switchShowLastDrawer = (Switch) findViewById(R.id.switch_setting_show_last_drawer);
        switchNightMode = (Switch) findViewById(R.id.switch_settings_night_mode);

        radioGroupLearnMode = (RadioGroup) findViewById(R.id.radio_group_learn_mode);
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

                settings.setAllowSync(isChecked);
                break;
            case R.id.switch_settings_answer_multi_choice_random:

                settings.setMultiplyChoiceAnswerOrderRandom(isChecked);
                break;
            case R.id.switch_setting_show_last_drawer:

                settings.setHideLastDrawer(isChecked);
                break;
            case R.id.switch_settings_night_mode:

                settings.setNightMode(isChecked);
                break;
        }

        settings.save();

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

                settings.setLearnMode(mode);
                break;
        }

        settings.save();

    }


    /**
     * Gets the gui id of the referenced enum constant
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-15
     *
     * @param constant
     * @return
     */
    public static int equalsId(Constants constant) {

        switch (constant) {

            case SETTINGS_LEARN_MODE_KNOWLEDGE:

                return R.id.radio_learn_mode_knowledge;

            case SETTINGS_LEARN_MODE_DATE:

                return R.id.radio_learn_mode_date;

            case SETTINGS_LEARN_MODE_RANDOM:

                return R.id.radio_learn_mode_random;

            case SETTINGS_LEARN_MODE_DRAWER:

                return R.id.radio_learn_mode_drawer;
        }

        return R.id.radio_learn_mode_drawer; // default

    }


    /**
     * Destroys the activity
     * resets listeners
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-20
     *
     */
    @Override
    protected void onDestroy() {

        super.onDestroy();
        setViewElementsListener(null);
    }

}
