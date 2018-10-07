package M5.seshealthpatient.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

import M5.seshealthpatient.Models.DataPacket;
import M5.seshealthpatient.Models.PatientUser;
import M5.seshealthpatient.R;

public class ViewPatientDataPackets extends BaseActivity implements AdapterView.OnItemClickListener {

    private String mPatientId;
    private PatientUser mPatient;
    private DatabaseReference mUserDb;
    private ListView mListView;
    private LinkedList<DataPacket> mDataPackets;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_view_patient_data_packets;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Data Packets");
        mPatientId = (String)getIntent().getSerializableExtra("PATIENT_ID");
        mListView = findViewById(R.id.dataPacketsListView);

        mUserDb = FirebaseDatabase.getInstance().getReference("Users/" + mPatientId);
        mUserDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mPatient = dataSnapshot.getValue(PatientUser.class);
                setTitle(mPatient.getName() + " - Data Packets");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mUserDb.child("Queries").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                createDataPacketList(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mListView.setOnItemClickListener(this);

    }

    public void createDataPacketList(DataSnapshot dataSnapshot) {
        LinkedList<DataPacket> dataPackets = new LinkedList<>();
        LinkedList<String> dataPacketQueries = new LinkedList<>();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            DataPacket dataPacket = snapshot.getValue(DataPacket.class);
            dataPackets.add(dataPacket);
            dataPacketQueries.add(dataPacket.getTitle());
        }

        mDataPackets = dataPackets;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2, android.R.id.text1, dataPacketQueries) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = view.findViewById(android.R.id.text1);
                TextView text2 = view.findViewById(android.R.id.text2);

                DataPacket dataPacket = mDataPackets.get(position);
                Date date = new Date(dataPacket.getSentDate());
                String dateString = String.format(Locale.ENGLISH, "%1$s %2$tr %2$te %2$tb %2$tY", "Sent at:", date);
                text1.setText(dataPacket.getTitle());
                text2.setText(dateString);

                return view;
            }
        };
        mListView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this, ViewDataPacket.class);
        intent.putExtra("DATA_PACKET", mDataPackets.get(i));
        intent.putExtra("PATIENT_ID", mPatientId);
        startActivity(intent);
    }
}

/* EXAMPLE CODE TO SUCCESSFULLY CALL THIS ACTIVITY (PASS IN THE PATIENT ID INTO THE INTENT)
v.findViewById(R.id.btnTEMP).setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
        Intent in = new Intent(getActivity(), ViewPatientDataPackets.class);
        in.putExtra("PATIENT_ID", "ebso6YbxA4PInNY64dTeGrww0x33");
        startActivity(in);
    }
});
*/