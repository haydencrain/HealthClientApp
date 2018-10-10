package M5.seshealthpatient.Activities;

import android.os.Bundle;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.LinkedList;

import M5.seshealthpatient.R;

public class HeartRateGraph extends BaseActivity {
    String[] mHeartRates;
    String mPatientName;

    @Override
    protected int getLayoutId() { return R.layout.activity_heart_rate_graph; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPatientName = (String)getIntent().getSerializableExtra("PATIENT_NAME");
        mHeartRates = (String[])getIntent().getSerializableExtra("HEART_RATES");
        setTitle(mPatientName + "'s Heart Rate Graph");

        DataPoint[] dataPoints = new DataPoint[mHeartRates.length];
        for (int i = 0; i < dataPoints.length; i++) {
            int heartRate = 0;
            String stringRate = mHeartRates[i];
            if (stringRate != null && !stringRate.isEmpty()) heartRate = Integer.parseInt(stringRate);
            dataPoints[i] = new DataPoint(i, heartRate);
        }

        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints);
        graph.addSeries(series);

    }
}