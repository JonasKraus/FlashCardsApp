package de.uulm.einhoernchen.flashcardsapp.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import de.uulm.einhoernchen.flashcardsapp.Model.Statistic;
import de.uulm.einhoernchen.flashcardsapp.R;


/**
 * @author Jonas Kraus jonas.kraus@uni-ulm.de
 * @since 2017.01.15
 */

public class DialogKnowledge extends Dialog implements View.OnClickListener, SeekBar.OnSeekBarChangeListener{

    private Button buttonCorrect;
    private Button buttonOk;
    private Button buttonIncorrect;
    private Statistic statistics;
    private SeekBar seekBarKnowledge;
    private TextView textPercentKnowledge;

    private int knowledge = 0;

    /**
     * Constructor matching super
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-17
     */
    public DialogKnowledge(Context context, Statistic statistics) {

        super(context);

        this.statistics = statistics;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_knowledge);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        buttonCorrect = (Button) findViewById(R.id.button_dialog_knowledge_correct);
        buttonCorrect.setOnClickListener(this);
        buttonOk = (Button) findViewById(R.id.button_dialog_knowledge_ok);
        buttonOk.setOnClickListener(this);
        buttonIncorrect = (Button) findViewById(R.id.button_dialog_knowledge_incorrect);
        buttonIncorrect.setOnClickListener(this);

        seekBarKnowledge = (SeekBar) findViewById(R.id.seek_bar_knowledge);
        seekBarKnowledge.setOnSeekBarChangeListener(this);

        textPercentKnowledge = (TextView) findViewById(R.id.text_knowledge);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.button_dialog_knowledge_correct:

                knowledge = 100;
                break;

            case R.id.button_dialog_knowledge_incorrect:

                knowledge = 0;
                break;

            case R.id.button_dialog_knowledge_ok:

                knowledge = seekBarKnowledge.getProgress();

                break;
        }

        statistics.save(knowledge);
        dismiss();

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        knowledge = progress;
        textPercentKnowledge.setText(progress + "%");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
