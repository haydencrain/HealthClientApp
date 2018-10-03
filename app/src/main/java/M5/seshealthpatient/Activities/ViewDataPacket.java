package M5.seshealthpatient.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


import M5.seshealthpatient.Models.DataPacket;
import M5.seshealthpatient.R;

public class ViewDataPacket extends AppCompatActivity {

    private DataPacket mDataPacket;

    private TextView mQueryTV;
    private TextView mHeartRateTV;
    private TextView mLocationTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data_packet);
        bindViewComponents();
        mDataPacket = (DataPacket)getIntent().getSerializableExtra("DATA_PACKET");
        if (mDataPacket.getQuery() != null)
            mQueryTV.setText(mDataPacket.getQuery());
        if (mDataPacket.getHeartRate() != null)
            mHeartRateTV.setText(mDataPacket.getHeartRate());
        if (mDataPacket.getLatitude() != 0 || mDataPacket.getLongitude() != 0)
            mLocationTV.setText(mDataPacket.getLatitude() + ", " + mDataPacket.getLongitude());
    }

    public void bindViewComponents() {
        mQueryTV = findViewById(R.id.queryTV);
        mHeartRateTV = findViewById(R.id.heartRateTV);
        mLocationTV = findViewById(R.id.locationTV);
    }
}
