package angleos.accelerometer;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.achartengine.ChartFactory;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.GraphicalView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class EnergyDataChart extends AppCompatActivity {

    private GraphicalView mChart;
    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
    private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
    private XYSeries mCurrentSeries;
    private XYSeriesRenderer mCurrentRenderer;

    private TextView steps;
    private int stepsCount = 0;

    private void initChart() {
        mCurrentSeries = new XYSeries("Accelerometer Data");
        mDataset.addSeries(mCurrentSeries);
        mCurrentRenderer = new XYSeriesRenderer();
        mRenderer.addSeriesRenderer(mCurrentRenderer);

        mRenderer.setXLabels(10);
        mRenderer.setYLabels(7);
        mRenderer.setShowGrid(true);
        mRenderer.setXLabelsAlign(Paint.Align.RIGHT);
        mRenderer.setYLabelsAlign(Paint.Align.RIGHT);
        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setBackgroundColor(Color.WHITE);
    }

    private void addEnergyData() {
        Scanner scan;
        File file = new File("/data/data/angleos.accelerometer/files/Energy.txt");
        double energy;
        int count = 1;
        //int interval = 0;
        String stepsOut;

        try {
            scan = new Scanner(file);
            while (scan.hasNextDouble()) {
                energy = scan.nextDouble();
                mCurrentSeries.add(count, energy);
                ++count;
                if (energy >= 20){// && count >= interval){
                    ++stepsCount;
                    //interval += count + 30;
                }
                stepsOut = "" + stepsCount;
                steps.setText(stepsOut);
            }
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_energy_data_chart);

        steps = (TextView) findViewById(R.id.steps);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
        if (mChart == null) {
            initChart();
            addEnergyData();
            mChart = ChartFactory.getCubeLineChartView(this, mDataset, mRenderer, 0.1f);
            layout.addView(mChart);
        } else {
            mChart.repaint();
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        stepsCount = 0;
    }
}