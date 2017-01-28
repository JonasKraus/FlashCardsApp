package de.uulm.einhoernchen.flashcardsapp.Activity;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Model.Statistic;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessorDate;

public class StatisticsActivity extends AppCompatActivity {


    private LineChart lineChartTest;
    private BarChart barChartTest;
    private RadarChart radarChartTest;
    private PieChart pieChartTest;

    private TextView textViewNumCards;
    private TextView textViewDuration;
    private TextView textViewDurationPerCard;

    private List<Statistic> statistics;
    private TextView textViewAverageKnowledge;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_statistics);

        statistics = Statistic.getStatistics();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_statistics);
        ((MainActivity) Globals.getContext()).setSupportActionBar(toolbar);

        setSupportActionBar(toolbar);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout_statistics);
        collapsingToolbar.setTitle(getResources().getString(R.string.toolbar_title_statistic));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewElements();

        setElementValues();

    }

    /**
     * Sets the values of the users settings to the gui elements
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-20
     */
    private void setElementValues() {

        List<Entry> entries = Globals.getDb().getEntriesForLineChart();
        LineDataSet lineDataSet = new LineDataSet(entries, getResources().getString(R.string.knowledge));
        // dataSet.setColor(...);
        // dataSet.setValueTextColor(...);
        LineData lineData = new LineData(lineDataSet);
        lineChartTest.setData(lineData);
        lineChartTest.invalidate(); // refresh

/*
        List<BarEntry> barEntries = new ArrayList<BarEntry>();

        float numDrawer0 = 0;
        float numDrawer1 = 0;
        float numDrawer2 = 0;
        float numDrawer3 = 0;
        float numDrawer4 = 0;
        float numDrawer5 = 0;
        float numDrawer6 = 0;

        for (Statistic stat: statistics) {

            switch (stat.getDrawer()) {

                case 0:
                    numDrawer0++;
                    break;
                case 1:
                    numDrawer1++;
                    break;
                case 2:
                    numDrawer2++;
                    break;
                case 3:
                    numDrawer3++;
                    break;
                case 4:
                    numDrawer4++;
                    break;
                case 5:
                    numDrawer5++;
                    break;
                case 6:
                    numDrawer6++;
                    break;
            }
        }

        barEntries.add(new BarEntry(0f, numDrawer0));
        barEntries.add(new BarEntry(1f, numDrawer1));
        barEntries.add(new BarEntry(2f, numDrawer2));
        barEntries.add(new BarEntry(3f, numDrawer3));
        barEntries.add(new BarEntry(4f, numDrawer4));
        barEntries.add(new BarEntry(5f, numDrawer5));
        barEntries.add(new BarEntry(6f, numDrawer6));

        BarDataSet set = new BarDataSet(barEntries, "ads");
*/

        BarDataSet set = new BarDataSet(Globals.getDb().getEntriesForBarChart(), "BarDataSet");

        BarData data = new BarData(set);
        data.setBarWidth(0.9f); // set custom bar width
        barChartTest.setData(data);
        barChartTest.setFitBars(true); // make the x-axis fit exactly all bars
        barChartTest.invalidate(); // refresh

        RadarDataSet setRadar = new RadarDataSet(Globals.getDb().getEntriesForRadarChart(), "RadarDataSet");
        RadarData dataRadar = new RadarData(setRadar);
        radarChartTest.setData(dataRadar);
        radarChartTest.invalidate(); // refresh

        PieDataSet pieSet = new PieDataSet(Globals.getDb().getEntriesForPieChart(), "Drawers content");

        pieSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        pieSet.setSliceSpace(3);
        pieSet.setSelectionShift(5);
        pieChartTest.setUsePercentValues(true);
        PieData dataPie = new PieData(pieSet);
        dataPie.setValueFormatter(new PercentFormatter());
        pieChartTest.setData(dataPie);
        //pieChartTest.setDescription("This is the distribution of cards to the drawers");
        pieChartTest.animateXY(1400, 1400); // refresh
        pieChartTest.invalidate(); // refresh

        // SET TextViews
        textViewNumCards.setText(statistics.size() +"");
        textViewDuration.setText(ProcessorDate.convertMillisToHMS(Statistic.getDurationOfSelection()));
        textViewDurationPerCard.setText(
                ProcessorDate.convertMillisToHMS(
                        Statistic.getDurationOfSelection() / statistics.size()
                )
        );
        textViewAverageKnowledge.setText(Statistic.getAverageKnowledgeOfSelection() + "%");

    }


    /**
     * Sets the listener to the gui elements
     * To unset listener give null as param
     *
     * @param set
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-20
     */
    private void setViewElementsListener(StatisticsActivity set) {

        // TODO

    }


    /**
     * Finds gui elements and sets them to vars
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-20
     */
    private void findViewElements() {

        lineChartTest = (LineChart) findViewById(R.id.chart_linechart_statistics);
        barChartTest = (BarChart) findViewById(R.id.chart_barchart_statistics);
        radarChartTest = (RadarChart) findViewById(R.id.chart_radarchart_statistics);
        pieChartTest = (PieChart) findViewById(R.id.chart_piechart_statistics);

        textViewNumCards = (TextView) findViewById(R.id.textview_statistcis_num_selected_cards);
        textViewDuration = (TextView) findViewById(R.id.textview_statistcis_duration);
        textViewDurationPerCard = (TextView) findViewById(R.id.textview_statistcis_duration_per_card);
        textViewAverageKnowledge = (TextView) findViewById(R.id.textview_statistcis_average_knowledge);
    }


    /**
     * Destroys the activity
     * resets listeners
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-20
     */
    @Override
    protected void onDestroy() {

        super.onDestroy();
        setViewElementsListener(null);
    }

}
