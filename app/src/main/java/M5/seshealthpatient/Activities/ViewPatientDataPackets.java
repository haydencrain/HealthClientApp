package M5.seshealthpatient.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;

import M5.seshealthpatient.Models.DataPacket;
import M5.seshealthpatient.Models.PatientUser;
import M5.seshealthpatient.R;

public class ViewPatientDataPackets extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private String mPatientId;
    private DatabaseReference mUserDb;
    private ListView mListView;
    private LinkedList<DataPacket> mDataPackets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_patient_data_packets);
        mPatientId = (String)getIntent().getSerializableExtra("PATIENT_ID");


        mListView = findViewById(R.id.dataPacketsListView);

        mUserDb = FirebaseDatabase.getInstance().getReference("Users/" + mPatientId);
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
        mDataPackets = new LinkedList<>();
        LinkedList<String> dataPacketDisplays = new LinkedList<>();
        int i = 0;
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            DataPacket dataPacket = snapshot.getValue(DataPacket.class);
            mDataPackets.add(dataPacket);
            dataPacketDisplays.add(dataPacket.getQuery());
        }

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, dataPacketDisplays);
        mListView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this, ViewDataPacket.class);
        intent.putExtra("DATA_PACKET", mDataPackets.get(i));
        startActivity(intent);
    }
}
