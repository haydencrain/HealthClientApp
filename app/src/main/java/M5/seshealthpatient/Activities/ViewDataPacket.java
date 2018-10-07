package M5.seshealthpatient.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import M5.seshealthpatient.Models.DataPacket;
import M5.seshealthpatient.Models.PatientUser;
import M5.seshealthpatient.R;

public class ViewDataPacket extends AppCompatActivity {

    private DatabaseReference mUserDb;
    private DataPacket mDataPacket;
    private String mPatientId;
    private PatientUser mPatient;
    private TextView mSentFromTV;
    private TextView mQueryTV;
    private TextView mHeartRateTV;
    private TextView mLocationTV;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data_packet);
        bindViewComponents();
        mDataPacket = (DataPacket)getIntent().getSerializableExtra("DATA_PACKET");
        mPatientId = (String)getIntent().getSerializableExtra("PATIENT_ID");
        toolbar.setTitle("Data Packet - " + mDataPacket.getTitle());
        if (mDataPacket.getQuery() != null)
            mQueryTV.setText(mDataPacket.getQuery());
        if (mDataPacket.getHeartRate() != null)
            mHeartRateTV.setText(mDataPacket.getHeartRate());
        if (mDataPacket.getLatitude() != 0 || mDataPacket.getLongitude() != 0)
            mLocationTV.setText(mDataPacket.getLatitude() + ", " + mDataPacket.getLongitude());

        mUserDb = FirebaseDatabase.getInstance().getReference("Users/" + mPatientId);
        mUserDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mPatient = dataSnapshot.getValue(PatientUser.class);
                mSentFromTV.setText(mPatient.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void bindViewComponents() {
        mSentFromTV = findViewById(R.id.sentFromTV);
        mQueryTV = findViewById(R.id.queryTV);
        mHeartRateTV = findViewById(R.id.heartRateTV);
        mLocationTV = findViewById(R.id.locationTV);
        toolbar = findViewById(R.id.dataPacketToolbar);
    }
}
